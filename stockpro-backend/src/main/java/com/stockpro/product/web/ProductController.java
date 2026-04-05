package com.stockpro.product.web;

import com.stockpro.product.domain.Product;
import com.stockpro.product.service.ProductService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Product catalogue API (Inventory Manager primary; read for others).
 */
@RestController
@RequestMapping("/api/v1/products")
@SecurityRequirement(name = "bearer")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> list(@RequestParam(required = false) String search) {
        if (search == null || search.isBlank()) {
            return productService.listActive();
        }
        return productService.search(search);
    }

    @GetMapping("/{id}")
    public Product byId(@PathVariable Long id) {
        return productService.get(id);
    }

    @GetMapping("/barcode/{barcode}")
    public Product byBarcode(@PathVariable String barcode) {
        return productService.findByBarcode(barcode);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public Product create(@RequestBody CreateProductRequest body) {
        return productService.create(
                body.sku(), body.name(), body.description(), body.category(), body.brand(),
                body.unitOfMeasure(), body.costPrice(), body.sellingPrice(),
                body.reorderLevel(), body.maxStockLevel(), body.leadTimeDays(),
                body.imageUrl(), body.barcode());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public Product update(@PathVariable Long id, @RequestBody Product patch) {
        return productService.update(id, patch);
    }

    @PostMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public void deactivate(@PathVariable Long id) {
        productService.deactivate(id);
    }

    public record CreateProductRequest(
            @NotBlank String sku,
            @NotBlank String name,
            String description,
            String category,
            String brand,
            String unitOfMeasure,
            BigDecimal costPrice,
            BigDecimal sellingPrice,
            int reorderLevel,
            int maxStockLevel,
            int leadTimeDays,
            String imageUrl,
            String barcode) {}
}
