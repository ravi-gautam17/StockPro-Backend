package com.stockpro.warehouse.web;

import com.stockpro.auth.service.CurrentUserService;
import com.stockpro.auth.domain.User;
import com.stockpro.movement.domain.MovementType;
import com.stockpro.movement.domain.StockMovement;
import com.stockpro.movement.repository.StockMovementRepository;
import com.stockpro.warehouse.service.StockInventoryService;
import com.stockpro.warehouse.service.WarehouseAccessService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Stock receipts/issues/adjustments/transfers — Warehouse Staff / Manager flows.
 */
@RestController
@RequestMapping("/api/v1/stock")
@SecurityRequirement(name = "bearer")
public class StockOperationsController {

    private final StockInventoryService stockInventoryService;
    private final StockMovementRepository movementRepository;
    private final CurrentUserService currentUserService;
    private final WarehouseAccessService warehouseAccessService;

    public StockOperationsController(StockInventoryService stockInventoryService,
                                     StockMovementRepository movementRepository,
                                     CurrentUserService currentUserService,
                                     WarehouseAccessService warehouseAccessService) {
        this.stockInventoryService = stockInventoryService;
        this.movementRepository = movementRepository;
        this.currentUserService = currentUserService;
        this.warehouseAccessService = warehouseAccessService;
    }

    @PostMapping("/movements")
    @PreAuthorize("hasAnyRole('STAFF','MANAGER','ADMIN')")
    public StockMovement movement(@RequestBody MovementRequest req) {
        long uid = currentUserService.requireCurrentUser().getId();
        return stockInventoryService.recordMovement(
                req.productId(), req.warehouseId(), req.type(), req.quantity(), uid,
                req.referenceId(), req.referenceType(), req.unitCost(), req.notes());
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasAnyRole('STAFF','MANAGER','ADMIN')")
    public void transfer(@RequestBody TransferRequest req) {
        long uid = currentUserService.requireCurrentUser().getId();
        stockInventoryService.transferStock(req.fromWarehouseId(), req.toWarehouseId(),
                req.productId(), req.quantity(), uid, req.reason());
    }

    @GetMapping("/movements")
    public List<StockMovement> movements(@RequestParam(required = false) Long warehouseId,
                                         @RequestParam(required = false) Long productId) {
        User viewer = currentUserService.requireCurrentUser();
        var restricted = warehouseAccessService.restrictedWarehouseIdsFor(viewer);

        if (restricted.isPresent()) {
            List<Long> allow = restricted.get();
            if (warehouseId != null && !allow.contains(warehouseId)) {
                throw new AccessDeniedException("No access to this warehouse");
            }
            if (productId != null && warehouseId != null) {
                return movementRepository.findByProduct_IdAndWarehouse_IdOrderByMovementDateDesc(productId, warehouseId);
            }
            if (warehouseId != null) {
                return movementRepository.findByWarehouse_IdOrderByMovementDateDesc(warehouseId);
            }
            if (productId != null) {
                return movementRepository.findByWarehouse_IdInAndProduct_IdOrderByMovementDateDesc(allow, productId);
            }
            return movementRepository.findByWarehouse_IdInOrderByMovementDateDesc(allow);
        }

        if (productId != null && warehouseId != null) {
            return movementRepository.findByProduct_IdAndWarehouse_IdOrderByMovementDateDesc(productId, warehouseId);
        }
        if (warehouseId != null) {
            return movementRepository.findByWarehouse_IdOrderByMovementDateDesc(warehouseId);
        }
        if (productId != null) {
            return movementRepository.findByProduct_IdOrderByMovementDateDesc(productId);
        }
        return movementRepository.findAll();
    }

    public record MovementRequest(
            @NotNull Long productId,
            @NotNull Long warehouseId,
            @NotNull MovementType type,
            int quantity,
            Long referenceId,
            String referenceType,
            BigDecimal unitCost,
            String notes) {}

    public record TransferRequest(
            @NotNull Long fromWarehouseId,
            @NotNull Long toWarehouseId,
            @NotNull Long productId,
            int quantity,
            String reason) {}
}
