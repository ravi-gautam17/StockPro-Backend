package com.stockpro.product.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Setter
@Getter
@Entity
@Table(name = "products")
public class Product{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String sku;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String category;
    private String brand;

    @Column(name = "unit_of_measure", length = 32)
    private String unitOfMeasure;

    @Column(name = "cost_price", nullable = false, precision = 19, scale = 4)
    private BigDecimal costPrice = BigDecimal.ZERO;

    @Column(name = "selling_price", nullable = false, precision = 19, scale = 4)
    private BigDecimal sellingPrice = BigDecimal.ZERO;

    @Column(name = "reorder_level", nullable = false)
    private int reorderLevel;

    @Column(name = "max_stock_level", nullable = false)
    private int maxStockLevel;

    @Column(name = "lead_time_days", nullable = false)
    private int leadTimeDays;

    @Column(name = "image_url", length = 512)
    private String imageUrl;

    @Column(length = 128)
    private String barcode;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;


    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @PrePersist
    void prePersist() {
        Instant now = Instant.now();
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }

}
