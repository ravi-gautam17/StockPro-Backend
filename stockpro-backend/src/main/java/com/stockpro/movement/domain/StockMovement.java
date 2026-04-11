package com.stockpro.movement.domain;

import com.stockpro.auth.domain.User;
import com.stockpro.product.domain.Product;
import com.stockpro.warehouse.domain.Warehouse;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name="stock_movements")
public class StockMovement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="movement_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name="product_id",nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name="warehouse_id",nullable = false)
    private Warehouse warehouse;

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false, length = 32)
    private MovementType movementType;

    @Column(nullable = false)
    private int quantity;

    @Column(name="reference_id")
    private Long referenceId;

    @Column(name="reference_type",length = 64)
    private String referenceType;

    @Column(name="unit_cost",precision = 19,scale = 4)
    private BigDecimal unitCost;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name="performed_by",nullable = false)
    private User performedBy;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name="movement_date",nullable = false)
    private Instant movementDate=Instant.now();

    @Column(name="balance_after",nullable = false)
    private int balanceAfter;

    @PrePersist
    void prePersist(){
        if(movementDate==null){
            movementDate=Instant.now();
        }
    }
}
