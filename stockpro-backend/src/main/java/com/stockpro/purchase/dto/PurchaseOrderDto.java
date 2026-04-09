package com.stockpro.purchase.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * RESPONSE DTO
 */
@Getter
@Setter
public class PurchaseOrderDto {

    private Long id;
    private Long supplierId;
    private String supplierName;

    private Long warehouseId;
    private String warehouseName;

    private String status;
    private BigDecimal totalAmount;

    private LocalDate orderDate;
    private LocalDate expectedDate;
    private LocalDate receivedDate;

    private String notes;

    private List<POLineItemDto> lineItems;

    // getters & setters
}