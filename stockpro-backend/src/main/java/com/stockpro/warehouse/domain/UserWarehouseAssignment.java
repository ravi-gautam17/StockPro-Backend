package com.stockpro.warehouse.domain;

import com.stockpro.auth.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Links {@link UserRole#STAFF} (and optionally others) to warehouses they may operate in.
 * If a user has <b>no</b> rows here, access checks treat them as unrestricted (backward compatible).
 */
@Setter
@Getter
@Entity
@Table(name = "user_warehouse_assignments",
        uniqueConstraints = @UniqueConstraint(name = "uk_user_wh", columnNames = {"user_id", "warehouse_id"}))
public class UserWarehouseAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

}
