package com.stockpro.audit.web;

import com.stockpro.audit.dto.AuditLogDTO;
import com.stockpro.audit.mapper.AuditLogMapper;
import com.stockpro.audit.repository.AuditLogRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/audit-logs")
@SecurityRequirement(name = "bearer")
public class AuditLogController {

    private final AuditLogRepository auditLogRepository;

    public AuditLogController(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<AuditLogDTO> range(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {

        return auditLogRepository
                .findByCreatedAtBetweenOrderByCreatedAtDesc(from, to)
                .stream()
                .map(AuditLogMapper::toDTO)
                .collect(Collectors.toList());
    }
}