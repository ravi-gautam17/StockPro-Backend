package com.stockpro.warehouse.service;

import com.stockpro.warehouse.domain.Warehouse;
import com.stockpro.warehouse.repository.StockLevelRepository;
import com.stockpro.warehouse.repository.WarehouseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Keeps {@link Warehouse#getUsedCapacity()} aligned with Σ(stock_levels.quantity) for utilisation KPIs.
 */
@Service
public class WarehouseCapacityService {

    private final WarehouseRepository warehouseRepository;
    private final StockLevelRepository stockLevelRepository;

    public WarehouseCapacityService(WarehouseRepository warehouseRepository,
                                    StockLevelRepository stockLevelRepository) {
        this.warehouseRepository = warehouseRepository;
        this.stockLevelRepository = stockLevelRepository;
    }

    @Transactional
    public void refreshUsedCapacity(long warehouseId) {
        Warehouse w = warehouseRepository.findById(warehouseId).orElse(null);
        if (w == null) {
            return;
        }
        Long raw = stockLevelRepository.sumQuantityByWarehouse_Id(warehouseId);
        long sum = raw != null ? Math.max(0L, raw) : 0L;
        w.setUsedCapacity((int) Math.min(sum, Integer.MAX_VALUE));
        warehouseRepository.save(w);
    }
}
