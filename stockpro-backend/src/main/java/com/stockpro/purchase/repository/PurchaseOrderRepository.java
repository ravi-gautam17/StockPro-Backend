package com.stockpro.purchase.repository;

import com.stockpro.purchase.domain.PurchaseOrder;
import com.stockpro.purchase.domain.PurchaseOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    List<PurchaseOrder> findBySupplier_Id(Long supplierId);

    List<PurchaseOrder> findByWarehouse_Id(Long warehouseId);

    List<PurchaseOrder> findByStatus(PurchaseOrderStatus status);

    List<PurchaseOrder> findByOrderDateBetween(LocalDate from, LocalDate to);

    List<PurchaseOrder> findByStatusAndExpectedDateBefore(PurchaseOrderStatus status, LocalDate date);

    List<PurchaseOrder> findByStatusInAndExpectedDateBefore(Collection<PurchaseOrderStatus> statuses, LocalDate date);

    List<PurchaseOrder> findByWarehouse_IdIn(Collection<Long> warehouseIds);

    List<PurchaseOrder> findByStatusAndWarehouse_IdIn(PurchaseOrderStatus status, Collection<Long> warehouseIds);
}