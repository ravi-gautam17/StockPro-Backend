package com.stockpro.purchase.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class POLineItemDto {

    private Long id;
    private Long productId;
    private String productName;

    private int quantity;
    private int receivedQty;

    private BigDecimal unitCost;
    private BigDecimal totalCost;

    // getters & setters
}