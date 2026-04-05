package com.stockpro.warehouse.repository;

import com.stockpro.warehouse.domain.UserWarehouseAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UseWarehouseAssignmentRepository extends JpaRepository<UserWarehouseAssignment, Long> {

    long countByUser_Id(Long userId);

    boolean existsByUser_IdAndWarehouse_Id(Long userId, Long warehouseId);

    void deleteByUser_Id(Long userId);

    @Query("SELECT a.warehouse.id FROM UserWarehouseAssignment a WHERE a.user.id = :uid")
    List<Long> findWarehouseIdsByUser_Id(@Param("uid") Long userId);
}
