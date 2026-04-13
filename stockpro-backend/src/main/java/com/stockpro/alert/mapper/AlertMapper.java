package com.stockpro.alert.mapper;

import com.stockpro.alert.domain.Alert;
import com.stockpro.alert.dto.AlertResponseDTO;

public class AlertMapper {

    public static AlertResponseDTO toDTO(Alert alert) {
        if (alert == null) return null;

        return new AlertResponseDTO(
                alert.getId(),
                alert.getAlertType(),
                alert.getSeverity(),
                alert.getTitle(),
                alert.getMessage(),
                alert.getRelatedProductId(),
                alert.getRelatedWarehouseId(),
                alert.getRelatedPoId(),
                alert.getChannel(),
                alert.isOpened(),
                alert.isAcknowledged(),
                alert.getCreatedAt()
        );
    }
}