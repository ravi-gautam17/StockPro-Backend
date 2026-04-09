package com.stockpro.purchase.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CreatePoRequest(
        Long supplierId,
        Long warehouseId,
        LocalDate orderDate,
        LocalDate expectedDate,
        String notes,
        List<LineReq> lines
) {
    public record LineReq(Long productId, int quantity, BigDecimal unitCost) {}
}