package com.stockpro.purchase.web;

import com.stockpro.auth.domain.User;
import com.stockpro.auth.service.CurrentUserService;
import com.stockpro.purchase.domain.PurchaseOrderStatus;
import com.stockpro.purchase.dto.CreatePoRequest;
import com.stockpro.purchase.dto.PurchaseOrderDto;
import com.stockpro.purchase.mapper.PurchaseOrderMapper;
import com.stockpro.purchase.service.PurchaseOrderService;
import com.stockpro.purchase.service.PurchaseOrderService.LineDraft;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/purchase-orders")
@SecurityRequirement(name = "bearer")
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;
    private final CurrentUserService currentUserService;

    public PurchaseOrderController(PurchaseOrderService purchaseOrderService,
                                   CurrentUserService currentUserService) {
        this.purchaseOrderService = purchaseOrderService;
        this.currentUserService = currentUserService;
    }

    // GET ALL (with optional status filter)
    @GetMapping
    public List<PurchaseOrderDto> list(@RequestParam(required = false) PurchaseOrderStatus status) {
        User user = currentUserService.requireCurrentUser();

        return purchaseOrderService.listForUser(user, status)
                .stream()
                .map(PurchaseOrderMapper::toDto)
                .toList();
    }

    //  GET BY ID
    @GetMapping("/{id}")
    public PurchaseOrderDto byId(@PathVariable Long id) {
        return PurchaseOrderMapper.toDto(
                purchaseOrderService.getPoForUser(id, currentUserService.requireCurrentUser())
        );
    }

    // CREATE PO (Draft)
    @PostMapping
    @PreAuthorize("hasAnyRole('OFFICER','ADMIN')")
    public PurchaseOrderDto create(@RequestBody CreatePoRequest body) {

        long userId = currentUserService.requireCurrentUser().getId();

        List<LineDraft> lines = body.lines().stream()
                .map(l -> new LineDraft(l.productId(), l.quantity(), l.unitCost()))
                .toList();

        return PurchaseOrderMapper.toDto(
                purchaseOrderService.createDraft(
                        body.supplierId(),
                        body.warehouseId(),
                        userId,
                        body.orderDate(),
                        body.expectedDate(),
                        body.notes(),
                        lines
                )
        );
    }

    //  SUBMIT FOR APPROVAL
    @PostMapping("/{id}/submit")
    @PreAuthorize("hasAnyRole('OFFICER','ADMIN')")
    public PurchaseOrderDto submit(@PathVariable Long id) {
        return PurchaseOrderMapper.toDto(
                purchaseOrderService.submitForApproval(
                        id,
                        currentUserService.requireCurrentUser().getId()
                )
        );
    }

    // ✅ APPROVE PO
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public PurchaseOrderDto approve(@PathVariable Long id) {
        return PurchaseOrderMapper.toDto(
                purchaseOrderService.approve(
                        id,
                        currentUserService.requireCurrentUser().getId()
                )
        );
    }

    //  REJECT PO
    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public PurchaseOrderDto reject(@PathVariable Long id) {
        return PurchaseOrderMapper.toDto(
                purchaseOrderService.reject(
                        id,
                        currentUserService.requireCurrentUser().getId()
                )
        );
    }

    // ✅ CANCEL PO
    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('OFFICER','ADMIN')")
    public PurchaseOrderDto cancel(@PathVariable Long id,
                                   @RequestParam String reason) {
        return PurchaseOrderMapper.toDto(
                purchaseOrderService.cancel(
                        id,
                        currentUserService.requireCurrentUser().getId(),
                        reason
                )
        );
    }

    // ✅ RECEIVE GOODS (Partial / Full)
    @PostMapping("/{id}/receive")
    @PreAuthorize("hasAnyRole('STAFF','MANAGER','ADMIN')")
    public PurchaseOrderDto receive(@PathVariable Long id,
                                    @RequestBody Map<Long, Integer> lineIdToQty) {
        return PurchaseOrderMapper.toDto(
                purchaseOrderService.receiveGoods(
                        id,
                        currentUserService.requireCurrentUser().getId(),
                        lineIdToQty
                )
        );
    }
}