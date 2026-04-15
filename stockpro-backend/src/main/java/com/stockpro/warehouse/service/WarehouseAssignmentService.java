package com.stockpro.warehouse.service;

import com.stockpro.auth.domain.User;
import com.stockpro.auth.repository.UserRepository;
import com.stockpro.audit.service.AuditService;
import com.stockpro.warehouse.domain.UserWarehouseAssignment;
import com.stockpro.warehouse.domain.Warehouse;
import com.stockpro.warehouse.repository.UserWarehouseAssignmentRepository;
import com.stockpro.warehouse.repository.WarehouseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;

@Service
public class WarehouseAssignmentService {

    private final UserWarehouseAssignmentRepository assignmentRepository;
    private final UserRepository userRepository;
    private final WarehouseRepository warehouseRepository;
    private final AuditService auditService;

    public WarehouseAssignmentService(UserWarehouseAssignmentRepository assignmentRepository,
                                      UserRepository userRepository,
                                      WarehouseRepository warehouseRepository,
                                      AuditService auditService) {
        this.assignmentRepository = assignmentRepository;
        this.userRepository = userRepository;
        this.warehouseRepository = warehouseRepository;
        this.auditService = auditService;
    }

    public List<Long> getWarehouseIdsForUser(long userId) {
        return assignmentRepository.findWarehouseIdsByUser_Id(userId);
    }

    @Transactional
    public void replaceAssignments(long userId, List<Long> warehouseIds, long actorId) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        assignmentRepository.deleteByUser_Id(userId);
        var unique = new LinkedHashSet<>(warehouseIds);
        for (Long wid : unique) {
            Warehouse w = warehouseRepository.findById(wid)
                    .orElseThrow(() -> new IllegalArgumentException("Warehouse not found: " + wid));
            UserWarehouseAssignment row = new UserWarehouseAssignment();
            row.setUser(u);
            row.setWarehouse(w);
            assignmentRepository.save(row);
        }
        auditService.log(actorId, "USER_WH_ASSIGN", "User", String.valueOf(userId), unique.toString());
    }
}
