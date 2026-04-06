package com.stockpro.supplier.web;

import com.stockpro.supplier.domain.Supplier;
import com.stockpro.supplier.service.SupplierService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/suppliers")
@SecurityRequirement(name = "bearer")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping
    public List<Supplier> list(@RequestParam(required = false) String q,
                               @RequestParam(required = false) String city,
                               @RequestParam(required = false) String country) {
        return supplierService.search(q, city, country);
    }

    @GetMapping("/{id}")
    public Supplier byId(@PathVariable Long id) {
        return supplierService.get(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('OFFICER','ADMIN')")
    public Supplier create(@RequestBody Supplier body) {
        return supplierService.create(body);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OFFICER','ADMIN')")
    public Supplier update(@PathVariable Long id, @RequestBody Supplier patch) {
        return supplierService.update(id, patch);
    }

    @PostMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('OFFICER','ADMIN')")
    public void deactivate(@PathVariable Long id) {
        supplierService.deactivate(id);
    }

    @PostMapping("/{id}/rating")
    @PreAuthorize("hasAnyRole('OFFICER','ADMIN')")
    public void rate(@PathVariable Long id, @RequestParam BigDecimal score) {
        supplierService.updateRating(id, score);
    }
}
