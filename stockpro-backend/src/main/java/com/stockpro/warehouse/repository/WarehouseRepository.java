package com.stockpro.warehouse.repository;

import com.stockpro.warehouse.domain.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    List<Warehouse> findByActiveTrue();

    List<Warehouse> findByActiveTrueAndIdIn(Collection<Long> ids);
}
