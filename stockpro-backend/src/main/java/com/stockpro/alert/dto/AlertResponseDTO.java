package com.stockpro.alert.dto;

import com.stockpro.alert.domain.AlertSeverity;
import com.stockpro.alert.domain.AlertType;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class AlertResponseDTO {

    private Long id;
    private AlertType alertType;
    private AlertSeverity severity;
    private String title;
    private String message;

    private Long relatedProductId;
    private Long relatedWarehouseId;
    private Long relatedPoId;

    private String channel;
    private boolean opened;
    private boolean acknowledged;

    private Instant createdAt;

    public AlertResponseDTO() {}

    public AlertResponseDTO(Long id,
                            AlertType alertType,
                            AlertSeverity severity,
                            String title,
                            String message,
                            Long relatedProductId,
                            Long relatedWarehouseId,
                            Long relatedPoId,
                            String channel,
                            boolean opened,
                            boolean acknowledged,
                            Instant createdAt) {
        this.id = id;
        this.alertType = alertType;
        this.severity = severity;
        this.title = title;
        this.message = message;
        this.relatedProductId = relatedProductId;
        this.relatedWarehouseId = relatedWarehouseId;
        this.relatedPoId = relatedPoId;
        this.channel = channel;
        this.opened = opened;
        this.acknowledged = acknowledged;
        this.createdAt = createdAt;
    }

    // Getters and Setters
}