package com.stockpro.movement.repository;

import com.stockpro.movement.domain.MovementType;
import com.stockpro.movement.domain.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    List<StockMovement> findByProduct_IdOrderByMovementDateDesc(Long productId);

    List<StockMovement> findByWarehouse_IdOrderByMovementDateDesc(Long warehouseId);

    List<StockMovement> findByMovementType(MovementType type);

    List<StockMovement> findByMovementDateBetweenOrderByMovementDateDesc(Instant from, Instant to);

    List<StockMovement> findByProduct_IdAndWarehouse_IdOrderByMovementDateDesc(Long productId, Long warehouseId);

    List<StockMovement> findByWarehouse_IdInOrderByMovementDateDesc(Collection<Long> warehouseIds);

    List<StockMovement> findByWarehouse_IdInAndProduct_IdOrderByMovementDateDesc(Collection<Long> warehouseIds,
                                                                                 Long productId);
}
