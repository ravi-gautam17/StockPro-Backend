package com.stockpro.report.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ValuationResponseDTO {

    private BigDecimal totalStockValue;

    public ValuationResponseDTO() {}

    public ValuationResponseDTO(BigDecimal totalStockValue) {
        this.totalStockValue = totalStockValue;
    }

}