package com.stockpro.purchase.service;

import com.stockpro.alert.service.AlertService;
import com.stockpro.auth.domain.User;
import com.stockpro.auth.domain.UserRole;
import com.stockpro.auth.repository.UserRepository;
import com.stockpro.movement.domain.MovementType;
import com.stockpro.product.domain.Product;
import com.stockpro.product.repository.ProductRepository;
import com.stockpro.purchase.domain.POLineItem;
import com.stockpro.purchase.domain.PurchaseOrder;
import com.stockpro.purchase.domain.PurchaseOrderStatus;
import com.stockpro.purchase.repository.PurchaseOrderRepository;
import com.stockpro.supplier.domain.Supplier;
import com.stockpro.supplier.repository.SupplierRepository;
import com.stockpro.warehouse.domain.Warehouse;
import com.stockpro.warehouse.repository.WarehouseRepository;
import com.stockpro.warehouse.service.StockInventoryService;
import com.stockpro.warehouse.service.WarehouseAccessService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * PO lifecycle: Draft → Pending → Approved → Partially/Fully Received / Cancelled.
 * Goods receipt delegates stock increases to {@link StockInventoryService} (STOCK_IN with PO reference).
 */
@Service
public class PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final SupplierRepository supplierRepository;
    private final WarehouseRepository warehouseRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final StockInventoryService stockInventoryService;
    private final AlertService alertService;
    private final WarehouseAccessService warehouseAccessService;

    public PurchaseOrderService(PurchaseOrderRepository purchaseOrderRepository,
                                SupplierRepository supplierRepository,
                                WarehouseRepository warehouseRepository,
                                UserRepository userRepository,
                                ProductRepository productRepository,
                                StockInventoryService stockInventoryService,
                                AlertService alertService,
                                WarehouseAccessService warehouseAccessService) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.supplierRepository = supplierRepository;
        this.warehouseRepository = warehouseRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.stockInventoryService = stockInventoryService;
        this.alertService = alertService;
        this.warehouseAccessService = warehouseAccessService;
    }

    @Transactional
    public PurchaseOrder createDraft(Long supplierId,
                                     Long warehouseId,
                                     Long createdById,
                                     LocalDate orderDate,
                                     LocalDate expectedDate,
                                     String notes,
                                     List<LineDraft> lines) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found"));
        Warehouse wh = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new IllegalArgumentException("Warehouse not found"));
        User creator = userRepository.findById(createdById)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (!supplier.isActive()) {
            throw new IllegalStateException("Supplier is inactive");
        }

        PurchaseOrder po = new PurchaseOrder();
        po.setSupplier(supplier);
        po.setWarehouse(wh);
        po.setCreatedBy(creator);
        po.setStatus(PurchaseOrderStatus.DRAFT);
        po.setOrderDate(orderDate != null ? orderDate : LocalDate.now());
        po.setExpectedDate(expectedDate);
        po.setNotes(notes);
        po.setLineItems(new ArrayList<>());

        for (LineDraft line : lines) {
            Product p = productRepository.findById(line.productId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + line.productId()));
            POLineItem li = new POLineItem();
            li.setPurchaseOrder(po);
            li.setProduct(p);
            li.setQuantity(line.quantity());
            li.setUnitCost(line.unitCost());
            li.syncTotalCost();
            li.setReceivedQty(0);
            po.getLineItems().add(li);
        }
        po.recalcTotal();
        PurchaseOrder saved = purchaseOrderRepository.save(po);
        return saved;
    }

    @Transactional
    public PurchaseOrder submitForApproval(Long poId, Long actorId) {
        PurchaseOrder po = getPo(poId);
        if (po.getStatus() != PurchaseOrderStatus.DRAFT && po.getStatus() != PurchaseOrderStatus.REJECTED) {
            throw new IllegalStateException("Only DRAFT or REJECTED PO can be submitted");
        }
        po.setStatus(PurchaseOrderStatus.PENDING_APPROVAL);
        alertService.notifyPoPendingApprovers(poId, po.getReferenceNumber() != null ? po.getReferenceNumber() : "#" + poId);
        return purchaseOrderRepository.save(po);
    }

    @Transactional
    public PurchaseOrder approve(Long poId, Long actorId) {
        PurchaseOrder po = getPo(poId);
        if (po.getStatus() != PurchaseOrderStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("PO is not pending approval");
        }
        po.setStatus(PurchaseOrderStatus.APPROVED);
        return purchaseOrderRepository.save(po);
    }

    @Transactional
    public PurchaseOrder reject(Long poId, Long actorId) {
        PurchaseOrder po = getPo(poId);
        if (po.getStatus() != PurchaseOrderStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("PO is not pending approval");
        }
        po.setStatus(PurchaseOrderStatus.REJECTED);
        return purchaseOrderRepository.save(po);
    }

    @Transactional
    public PurchaseOrder cancel(Long poId, Long actorId, String reason) {
        PurchaseOrder po = getPo(poId);
        if (po.getStatus() == PurchaseOrderStatus.FULLY_RECEIVED || po.getStatus() == PurchaseOrderStatus.CANCELLED) {
            throw new IllegalStateException("PO cannot be cancelled");
        }
        po.setStatus(PurchaseOrderStatus.CANCELLED);
        po.setNotes((po.getNotes() != null ? po.getNotes() + "\n" : "") + "Cancelled: " + reason);
        return purchaseOrderRepository.save(po);
    }

    /**
     * Partial receipts: pass lineItemId → qty received this time. Updates PO aggregate status.
     */
    @Transactional
    public PurchaseOrder receiveGoods(Long poId, Long performerId, Map<Long, Integer> lineIdToQty) {
        PurchaseOrder po = getPo(poId);
        if (po.getStatus() != PurchaseOrderStatus.APPROVED && po.getStatus() != PurchaseOrderStatus.PARTIALLY_RECEIVED) {
            throw new IllegalStateException("PO must be APPROVED or PARTIALLY_RECEIVED to receive goods");
        }

        Warehouse wh = po.getWarehouse();
        User performer = userRepository.findById(performerId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        warehouseAccessService.requireWarehouseAccess(performer, wh.getId());

        for (var e : lineIdToQty.entrySet()) {
            POLineItem line = po.getLineItems().stream()
                    .filter(li -> li.getId().equals(e.getKey()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Unknown line: " + e.getKey()));
            int recv = e.getValue();
            if (recv <= 0) {
                continue;
            }
            int remaining = line.getQuantity() - line.getReceivedQty();
            if (recv > remaining) {
                throw new IllegalArgumentException("Receive qty exceeds remaining for line " + line.getId());
            }
            line.setReceivedQty(line.getReceivedQty() + recv);

            stockInventoryService.recordMovement(
                    line.getProduct().getId(),
                    wh.getId(),
                    MovementType.STOCK_IN,
                    recv,
                    performerId,
                    poId,
                    "PO",
                    line.getUnitCost(),
                    "GRN for PO " + poId);
        }

        boolean allDone = po.getLineItems().stream().allMatch(li -> li.getReceivedQty() >= li.getQuantity());
        po.setStatus(allDone ? PurchaseOrderStatus.FULLY_RECEIVED : PurchaseOrderStatus.PARTIALLY_RECEIVED);
        if (allDone) {
            po.setReceivedDate(LocalDate.now());
        }
        purchaseOrderRepository.save(po);

        // Supplier rating hook: case study — optionally bump rating (simplified: skip).

        return po;
    }

    public PurchaseOrder getPo(Long poId) {
        return purchaseOrderRepository.findById(poId)
                .orElseThrow(() -> new IllegalArgumentException("PO not found"));
    }

    public List<PurchaseOrder> listAll() {
        return purchaseOrderRepository.findAll();
    }

    public List<PurchaseOrder> listByStatus(PurchaseOrderStatus status) {
        return purchaseOrderRepository.findByStatus(status);
    }

    /**
     * Staff with warehouse assignments only see POs for those sites; other roles see everything.
     */
    public List<PurchaseOrder> listForUser(User user, PurchaseOrderStatus status) {
        if (user.getRole() != UserRole.STAFF) {
            return status != null ? purchaseOrderRepository.findByStatus(status) : purchaseOrderRepository.findAll();
        }
        var restricted = warehouseAccessService.restrictedWarehouseIdsFor(user);
        if (restricted.isEmpty()) {
            return status != null ? purchaseOrderRepository.findByStatus(status) : purchaseOrderRepository.findAll();
        }
        var wids = restricted.get();
        if (status != null) {
            return purchaseOrderRepository.findByStatusAndWarehouse_IdIn(status, wids);
        }
        return purchaseOrderRepository.findByWarehouse_IdIn(wids);
    }

    public PurchaseOrder getPoForUser(Long poId, User user) {
        PurchaseOrder po = getPo(poId);
        warehouseAccessService.requireWarehouseAccess(user, po.getWarehouse().getId());
        return po;
    }

    public record LineDraft(Long productId, int quantity, BigDecimal unitCost) {}
}
