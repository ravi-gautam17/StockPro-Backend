package com.stockpro.report.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class InventorySnapshotResponseDTO {

    private Long productId;
    private Long warehouseId;
    private int quantity;
    private BigDecimal stockValue;
    private LocalDate snapshotDate;

}