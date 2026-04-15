package com.stockpro.report.mapper;

import com.stockpro.report.domain.InventorySnapshot;
import com.stockpro.report.dto.InventorySnapshotResponseDTO;
import com.stockpro.report.dto.ValuationResponseDTO;

import java.math.BigDecimal;

public class InventorySnapshotMapper {

    private InventorySnapshotMapper() {}

    public static InventorySnapshotResponseDTO toDTO(InventorySnapshot entity) {
        InventorySnapshotResponseDTO dto = new InventorySnapshotResponseDTO();

        dto.setProductId(entity.getProduct().getId());
        dto.setWarehouseId(entity.getWarehouse().getId());
        dto.setQuantity(entity.getQuantity());
        dto.setStockValue(entity.getStockValue());
        dto.setSnapshotDate(entity.getSnapshotDate());

        return dto;
    }

    public static ValuationResponseDTO toValuationDTO(BigDecimal value) {
        return new ValuationResponseDTO(value);
    }
}