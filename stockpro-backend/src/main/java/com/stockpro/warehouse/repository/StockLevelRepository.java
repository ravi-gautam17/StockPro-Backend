package com.stockpro.warehouse.repository;

import com.stockpro.warehouse.domain.StockLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface StockLevelRepository extends JpaRepository<StockLevel, Long> {

    Optional<StockLevel> findByWarehouse_IdAndProduct_Id(Long warehouseId, Long productId);

    List<StockLevel> findByWarehouse_Id(Long warehouseId);

    List<StockLevel> findByProduct_Id(Long productId);

    @Query("SELECT sl FROM StockLevel sl JOIN sl.product p WHERE sl.warehouse.id = :whId AND p.active = true " +
            "AND sl.quantity < p.reorderLevel")
    List<StockLevel> findLowStockForWarehouse(@Param("whId") Long warehouseId);

    @Query("SELECT COALESCE(SUM(sl.quantity * p.costPrice), 0) FROM StockLevel sl JOIN sl.product p")
    BigDecimal sumStockValuation();

    @Query("SELECT COALESCE(SUM(sl.quantity), 0) FROM StockLevel sl WHERE sl.warehouse.id = :whId")
    Long sumQuantityByWarehouse_Id(@Param("whId") Long warehouseId);
}

