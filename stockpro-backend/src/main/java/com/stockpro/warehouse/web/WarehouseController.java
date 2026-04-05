package com.stockpro.warehouse.web;

import com.stockpro.auth.service.CurrentUserService;
import com.stockpro.warehouse.domain.Warehouse;
import com.stockpro.warehouse.service.WarehouseService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/warehouses")
@SecurityRequirement(name = "bearer")
public class
WarehouseController {

    private final WarehouseService warehouseService;
    private final CurrentUserService currentUserService;

    public WarehouseController(WarehouseService warehouseService, CurrentUserService currentUserService) {
        this.warehouseService = warehouseService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public List<Warehouse> list() {
        return warehouseService.listActiveVisibleTo(currentUserService.requireCurrentUser());
    }

    @GetMapping("/{id}")
    public Warehouse byId(@PathVariable Long id) {
        return warehouseService.getVisibleOrThrow(currentUserService.requireCurrentUser(), id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Warehouse create(@RequestBody Warehouse body) {
        Long mgrId = body.getManager() != null ? body.getManager().getId() : null;
        return warehouseService.create(body.getName(), body.getLocation(), body.getAddress(),
                mgrId, body.getCapacity(), body.getPhone());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public Warehouse update(@PathVariable Long id, @RequestBody Warehouse patch) {
        return warehouseService.update(id, patch);
    }

    @GetMapping("/{id}/stock")
    public List<com.stockpro.warehouse.domain.StockLevel> stock(@PathVariable Long id) {
        return warehouseService.stockForActor(currentUserService.requireCurrentUser(), id);
    }
}
