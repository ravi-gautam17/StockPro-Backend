package com.stockpro.audit.mapper;

import com.stockpro.audit.domain.AuditLog;
import com.stockpro.audit.dto.AuditLogDTO;

public class AuditLogMapper {

    public static AuditLogDTO toDTO(AuditLog entity) {
        return AuditLogDTO.builder()
                .id(entity.getId())
                .actorId(entity.getActorId())
                .actionType(entity.getActionType())
                .entityType(entity.getEntityType())
                .entityId(entity.getEntityId())
                .detailJson(entity.getDetailJson())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}