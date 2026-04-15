package com.stockpro.report.repository;

import com.stockpro.report.domain.InventorySnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface InventorySnapshotRepository extends JpaRepository<InventorySnapshot, Long> {

    List<InventorySnapshot> findBySnapshotDate(LocalDate date);

    List<InventorySnapshot> findBySnapshotDateBetween(LocalDate from, LocalDate to);
}