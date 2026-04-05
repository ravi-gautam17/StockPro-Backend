package com.stockpro.warehouse.service;

import com.stockpro.auth.domain.User;
import com.stockpro.auth.domain.UserRole;
import com.stockpro.warehouse.repository.UserWarehouseAssignmentRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Enforces “assigned warehouse” visibility for {@link UserRole#STAFF}. Other roles are unrestricted.
 * If a staff user has <b>no</b> assignment rows, behaviour matches the legacy “all warehouses” model.
 */
@Service
public class WarehouseAccessService {

    private final UserWarehouseAssignmentRepository assignmentRepository;

    public WarehouseAccessService(UserWarehouseAssignmentRepository assignmentRepository) {
        this.assignmentRepository = assignmentRepository;
    }

    /** Empty = treat as all warehouses visible; non-empty = only these IDs. */
    public Optional<List<Long>> restrictedWarehouseIdsFor(User user) {
        if (user.getRole() != UserRole.STAFF) {
            return Optional.empty();
        }
        if (assignmentRepository.countByUser_Id(user.getId()) == 0) {
            return Optional.empty();
        }
        return Optional.of(assignmentRepository.findWarehouseIdsByUser_Id(user.getId()));
    }

    public void requireWarehouseAccess(User user, Long warehouseId) {
        restrictedWarehouseIdsFor(user).ifPresent(ids -> {
            if (!ids.contains(warehouseId)) {
                throw new AccessDeniedException("No access to warehouse " + warehouseId);
            }
        });
    }
}
