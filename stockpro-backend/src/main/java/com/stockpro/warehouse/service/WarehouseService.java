package com.stockpro.warehouse.service;

import com.stockpro.auth.domain.User;
import com.stockpro.auth.repository.UserRepository;
import com.stockpro.warehouse.domain.StockLevel;
import com.stockpro.warehouse.domain.Warehouse;
import com.stockpro.warehouse.repository.StockLevelRepository;
import com.stockpro.warehouse.repository.WarehouseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final StockLevelRepository stockLevelRepository;
    private final UserRepository userRepository;
    private final WarehouseAccessService warehouseAccessService;

    public WarehouseService(WarehouseRepository warehouseRepository,
                            StockLevelRepository stockLevelRepository,
                            UserRepository userRepository,
                            WarehouseAccessService warehouseAccessService) {
        this.warehouseRepository = warehouseRepository;
        this.stockLevelRepository = stockLevelRepository;
        this.userRepository = userRepository;
        this.warehouseAccessService = warehouseAccessService;
    }

    public List<Warehouse> listActive() {
        return warehouseRepository.findByActiveTrue();
    }

    /**
     * Staff with explicit assignments see only those warehouses; everyone else sees all active.
     */
    public List<Warehouse> listActiveVisibleTo(User viewer) {
        var restricted = warehouseAccessService.restrictedWarehouseIdsFor(viewer);
        if (restricted.isEmpty()) {
            return warehouseRepository.findByActiveTrue();
        }
        return warehouseRepository.findByActiveTrueAndIdIn(restricted.get());
    }

    public Warehouse get(Long id) {
        return warehouseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Warehouse not found"));
    }

    public Warehouse getVisibleOrThrow(User viewer, Long id) {
        Warehouse w = get(id);
        warehouseAccessService.requireWarehouseAccess(viewer, id);
        return w;
    }

    @Transactional
    public Warehouse create(String name, String location, String address, Long managerId, Integer capacity, String phone) {
        Warehouse w = new Warehouse();
        w.setName(name);
        w.setLocation(location);
        w.setAddress(address);
        if (managerId != null) {
            User m = userRepository.findById(managerId).orElseThrow(() -> new IllegalArgumentException("Manager not found"));
            w.setManager(m);
        }
        w.setCapacity(capacity);
        w.setPhone(phone);
        w.setActive(true);
        w.setUsedCapacity(0);
        return warehouseRepository.save(w);
    }

    @Transactional
    public Warehouse update(Long id, Warehouse patch) {
        Warehouse w = get(id);
        if (patch.getName() != null) {
            w.setName(patch.getName());
        }
        if (patch.getLocation() != null) {
            w.setLocation(patch.getLocation());
        }
        if (patch.getAddress() != null) {
            w.setAddress(patch.getAddress());
        }
        if (patch.getCapacity() != null) {
            w.setCapacity(patch.getCapacity());
        }
        if (patch.getPhone() != null) {
            w.setPhone(patch.getPhone());
        }
        return warehouseRepository.save(w);
    }

    public List<StockLevel> stockForWarehouse(Long warehouseId) {
        get(warehouseId);
        return stockLevelRepository.findByWarehouse_Id(warehouseId);
    }

    public List<StockLevel> stockForActor(User viewer, Long warehouseId) {
        warehouseAccessService.requireWarehouseAccess(viewer, warehouseId);
        return stockLevelRepository.findByWarehouse_Id(warehouseId);
    }
}
