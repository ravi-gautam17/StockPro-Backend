package com.stockpro.audit.service;

import com.stockpro.audit.domain.AuditLog;
import com.stockpro.audit.repository.AuditLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Cross-cutting audit trail (case study: PO changes, stock movements, user admin).
 * Call from domain services after successful commits — keeps monolith observability in one table.
 */
@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public void log(Long actorId, String actionType, String entityType, String entityId, String detailJson) {
        AuditLog row = new AuditLog();
        row.setActorId(actorId);
        row.setActionType(actionType);
        row.setEntityType(entityType);
        row.setEntityId(entityId);
        row.setDetailJson(detailJson);
        auditLogRepository.save(row);
    }
}
