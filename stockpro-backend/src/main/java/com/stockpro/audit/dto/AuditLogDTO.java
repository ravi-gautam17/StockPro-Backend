package com.stockpro.audit.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class AuditLogDTO {

    private Long id;
    private Long actorId;
    private String actionType;
    private String entityType;
    private String entityId;
    private String detailJson;
    private Instant createdAt;
}