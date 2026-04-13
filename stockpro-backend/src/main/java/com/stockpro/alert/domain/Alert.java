package com.stockpro.alert.domain;

import com.stockpro.auth.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "alerts")
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alert_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @Enumerated(EnumType.STRING)
    @Column(name = "alert_type", nullable = false, length = 32)
    private AlertType alertType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private AlertSeverity severity;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(name = "related_product_id")
    private Long relatedProductId;

    @Column(name = "related_warehouse_id")
    private Long relatedWarehouseId;

    @Column(name = "related_po_id")
    private Long relatedPoId;

    @Column(nullable = false, length = 32)
    private String channel = "IN_APP";

    @Column(name = "is_read", nullable = false)
    private boolean opened;

    @Column(name = "is_acknowledged", nullable = false)
    private boolean acknowledged;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
