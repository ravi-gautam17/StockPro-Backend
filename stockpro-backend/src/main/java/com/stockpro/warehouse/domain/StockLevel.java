package com.stockpro.warehouse.domain;

import com.stockpro.product.domain.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * Per-warehouse stock. {@code availableQuantity} is derived: quantity − reservedQuantity
 * (exposed in DTOs; not a DB column per case study).
 */
@Setter
@Getter
@Entity
@Table(name = "stock_levels")
public class StockLevel{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "reserved_quantity", nullable = false)
    private int reservedQuantity;

    @Column(name = "bin_location", length = 128)
    private String binLocation;

    @Version
    @Column(nullable = false)
    private long version;

    @Column(name = "last_updated", nullable = false)
    private Instant lastUpdated = Instant.now();

    @PrePersist
    @PreUpdate
    void touch() {
        lastUpdated = Instant.now();
    }

    public int availableQuantity() {
        return Math.max(0, quantity - reservedQuantity);
    }

}
