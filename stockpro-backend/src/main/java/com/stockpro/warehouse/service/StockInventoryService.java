package com.stockpro.warehouse.service;

import com.stockpro.auth.domain.User;
import com.stockpro.auth.repository.UserRepository;
import com.stockpro.movement.domain.MovementType;
import com.stockpro.movement.domain.StockMovement;
import com.stockpro.movement.repository.StockMovementRepository;
import com.stockpro.product.domain.Product;
import com.stockpro.product.repository.ProductRepository;
import com.stockpro.warehouse.domain.StockLevel;
import com.stockpro.warehouse.domain.Warehouse;
import com.stockpro.warehouse.repository.StockLevelRepository;
import com.stockpro.warehouse.repository.WarehouseRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Core stock mutation workflow (monolith): one transactional boundary updates {@link StockLevel},
 * appends an immutable {@link com.stockpro.movement.domain.StockMovement}, writes audit, then
 * evaluates alert thresholds. This replaces HTTP calls between "warehouse-service" and
 * "movement-service" in the microservices version of the case study.
 */
@Service
public class StockInventoryService {

    private final StockLevelRepository stockLevelRepository;
    private final StockMovementRepository movementRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final UserRepository userRepository;
    private final WarehouseAccessService warehouseAccessService;
    private final WarehouseCapacityService warehouseCapacityService;

    public StockInventoryService(StockLevelRepository stockLevelRepository,
                                 StockMovementRepository movementRepository,
                                 ProductRepository productRepository,
                                 WarehouseRepository warehouseRepository,
                                 UserRepository userRepository,
                                 WarehouseAccessService warehouseAccessService,
                                 WarehouseCapacityService warehouseCapacityService) {
        this.stockLevelRepository = stockLevelRepository;
        this.movementRepository = movementRepository;
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
        this.userRepository = userRepository;
        this.warehouseAccessService = warehouseAccessService;
        this.warehouseCapacityService = warehouseCapacityService;
    }

    /**
     * @param quantityPositive magnitude for in/out types; for {@link MovementType#ADJUSTMENT} pass
     *                        signed delta (can be negative).
     */
    @Transactional
    public StockMovement recordMovement(Long productId,
                                        Long warehouseId,
                                        MovementType type,
                                        int quantityPositive,
                                        Long performedByUserId,
                                        Long referenceId,
                                        String referenceType,
                                        BigDecimal unitCost,
                                        String notes) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new IllegalArgumentException("Warehouse not found"));
        User performer = userRepository.findById(performedByUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        warehouseAccessService.requireWarehouseAccess(performer, warehouseId);

        StockLevel level = stockLevelRepository
                .findByWarehouse_IdAndProduct_Id(warehouseId, productId)
                .orElseGet(() -> createEmptyLevel(warehouse, product));

        int delta = switch (type) {
            case STOCK_IN, TRANSFER_IN, RETURN -> {
                if (quantityPositive <= 0) {
                    throw new IllegalArgumentException("Inbound quantity must be > 0");
                }
                yield quantityPositive;
            }
            case STOCK_OUT, TRANSFER_OUT, WRITE_OFF -> {
                if (quantityPositive <= 0) {
                    throw new IllegalArgumentException("Outbound quantity must be > 0");
                }
                yield -quantityPositive;
            }
            case ADJUSTMENT -> quantityPositive;
        };

        int newQty = level.getQuantity() + delta;
        if (newQty < 0) {
            throw new IllegalArgumentException("Insufficient stock; cannot go negative");
        }
        if (newQty < level.getReservedQuantity()) {
            throw new IllegalArgumentException("Quantity would fall below reserved quantity");
        }

        level.setQuantity(newQty);
        try {
            stockLevelRepository.save(level);
        } catch (ObjectOptimisticLockingFailureException ex) {
            throw new IllegalStateException(
                    "Concurrent stock update detected — retry the operation (optimistic lock).", ex);
        }

        StockMovement mov = new StockMovement();
        mov.setProduct(product);
        mov.setWarehouse(warehouse);
        mov.setMovementType(type);
        // Preserve sign for adjustments (audit); inbound/outbound types stay non-negative.
        mov.setQuantity(type == MovementType.ADJUSTMENT ? delta : Math.abs(delta));
        mov.setReferenceId(referenceId);
        mov.setReferenceType(referenceType);
        mov.setUnitCost(unitCost);
        mov.setPerformedBy(performer);
        mov.setNotes(notes);
        mov.setBalanceAfter(newQty);
        movementRepository.save(mov);


        warehouseCapacityService.refreshUsedCapacity(warehouseId);

        return mov;
    }

    /**
     * Inter-warehouse transfer: paired out/in in a single transaction (case study: atomic transfer).
     */
    @Transactional
    public void transferStock(Long fromWarehouseId,
                              Long toWarehouseId,
                              Long productId,
                              int quantity,
                              Long performedByUserId,
                              String reason) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Transfer quantity must be positive");
        }
        if (fromWarehouseId.equals(toWarehouseId)) {
            throw new IllegalArgumentException("Source and destination must differ");
        }
        recordMovement(productId, fromWarehouseId, MovementType.TRANSFER_OUT, quantity, performedByUserId,
                toWarehouseId, "TRANSFER", null, reason);
        recordMovement(productId, toWarehouseId, MovementType.TRANSFER_IN, quantity, performedByUserId,
                fromWarehouseId, "TRANSFER", null, reason);
    }

    private StockLevel createEmptyLevel(Warehouse warehouse, Product product) {
        StockLevel sl = new StockLevel();
        sl.setWarehouse(warehouse);
        sl.setProduct(product);
        sl.setQuantity(0);
        sl.setReservedQuantity(0);
        return stockLevelRepository.save(sl);
    }
}
