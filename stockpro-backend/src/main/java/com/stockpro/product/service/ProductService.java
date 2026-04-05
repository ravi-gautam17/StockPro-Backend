package com.stockpro.product.service;

import com.stockpro.product.domain.Product;
import com.stockpro.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> listActive() {
        return productRepository.findByActiveTrue();
    }

    public Product get(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }

    public Product findByBarcode(String barcode) {
        return productRepository.findByBarcode(barcode)
                .orElseThrow(() -> new IllegalArgumentException("Product not found for barcode"));
    }

    @Transactional
    public Product create(String sku,
                          String name,
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
                          String barcode) {
        if (productRepository.findBySku(sku).isPresent()) {
            throw new IllegalArgumentException("SKU already exists");
        }
        Product p = new Product();
        p.setSku(sku);
        p.setName(name);
        p.setDescription(description);
        p.setCategory(category);
        p.setBrand(brand);
        p.setUnitOfMeasure(unitOfMeasure);
        p.setCostPrice(costPrice != null ? costPrice : BigDecimal.ZERO);
        p.setSellingPrice(sellingPrice != null ? sellingPrice : BigDecimal.ZERO);
        p.setReorderLevel(reorderLevel);
        p.setMaxStockLevel(maxStockLevel);
        p.setLeadTimeDays(leadTimeDays);
        p.setImageUrl(imageUrl);
        p.setBarcode(barcode);
        p.setActive(true);
        return productRepository.save(p);
    }

    @Transactional
    public Product update(Long id, Product patch) {
        Product p = get(id);
        if (patch.getName() != null) {
            p.setName(patch.getName());
        }
        if (patch.getDescription() != null) {
            p.setDescription(patch.getDescription());
        }
        if (patch.getCategory() != null) {
            p.setCategory(patch.getCategory());
        }
        if (patch.getBrand() != null) {
            p.setBrand(patch.getBrand());
        }
        if (patch.getCostPrice() != null) {
            p.setCostPrice(patch.getCostPrice());
        }
        if (patch.getSellingPrice() != null) {
            p.setSellingPrice(patch.getSellingPrice());
        }
        p.setReorderLevel(patch.getReorderLevel());
        p.setMaxStockLevel(patch.getMaxStockLevel());
        p.setLeadTimeDays(patch.getLeadTimeDays());
        if (patch.getImageUrl() != null) {
            p.setImageUrl(patch.getImageUrl());
        }
        if (patch.getBarcode() != null) {
            p.setBarcode(patch.getBarcode());
        }
        return productRepository.save(p);
    }

    @Transactional
    public void deactivate(Long id) {
        Product p = get(id);
        p.setActive(false);
        productRepository.save(p);
    }

    public List<Product> search(String q) {
        if (q == null || q.isBlank()) {
            return listActive();
        }
        return productRepository.searchActive(q.trim());
    }
}
