-- Staff ↔ warehouse assignments (case study: operators only work assigned facilities).
-- Backfill used_capacity as sum of on-hand quantities per warehouse (KPI “utilisation” baseline).

CREATE TABLE user_warehouse_assignments (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id       BIGINT NOT NULL,
    warehouse_id  BIGINT NOT NULL,
    UNIQUE KEY uk_user_wh (user_id, warehouse_id),
    CONSTRAINT fk_uwa_user FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    CONSTRAINT fk_uwa_wh FOREIGN KEY (warehouse_id) REFERENCES warehouses (warehouse_id) ON DELETE CASCADE,
    INDEX idx_uwa_user (user_id)
);

UPDATE warehouses w
LEFT JOIN (
    SELECT warehouse_id, COALESCE(SUM(quantity), 0) AS sq
    FROM stock_levels
    GROUP BY warehouse_id
) s ON w.warehouse_id = s.warehouse_id
SET w.used_capacity = COALESCE(s.sq, 0);
