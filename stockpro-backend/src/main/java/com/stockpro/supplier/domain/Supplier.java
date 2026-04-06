package com.stockpro.supplier.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

import lombok.*;

@Setter
@Getter
@Entity
@Table(name = "suppliers")
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "supplier_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "contact_person")
    private String contactPerson;

    private String email;
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String address;

    private String city;
    private String country;

    @Column(name = "tax_id", length = 64)
    private String taxId;

    @Column(name = "payment_terms", length = 64)
    private String paymentTerms;

    @Column(name = "lead_time_days", nullable = false)
    private int leadTimeDays;

    @Column(nullable = false, precision = 3, scale = 2)
    private BigDecimal rating = BigDecimal.ZERO;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

}
