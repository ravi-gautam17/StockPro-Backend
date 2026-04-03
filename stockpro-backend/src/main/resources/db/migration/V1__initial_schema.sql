-- StockPro initial schema — single database for the monolith (all case-study domains).

CREATE TABLE users (
    user_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name     VARCHAR(255) NOT NULL,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    phone         VARCHAR(64),
    role          VARCHAR(32)  NOT NULL,
    department    VARCHAR(128),
    is_active     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP NULL
);

CREATE TABLE products (
    product_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    sku              VARCHAR(64) NOT NULL UNIQUE,
    name             VARCHAR(255) NOT NULL,
    description      TEXT,
    category         VARCHAR(128),
    brand            VARCHAR(128),
    unit_of_measure  VARCHAR(32),
    cost_price       DECIMAL(19,4) NOT NULL DEFAULT 0,
    selling_price    DECIMAL(19,4) NOT NULL DEFAULT 0,
    reorder_level    INT NOT NULL DEFAULT 0,
    max_stock_level  INT NOT NULL DEFAULT 0,
    lead_time_days   INT NOT NULL DEFAULT 0,
    image_url        VARCHAR(512),
    barcode          VARCHAR(128),
    is_active        BOOLEAN NOT NULL DEFAULT TRUE,
    created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE warehouses (
    warehouse_id  BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    location      VARCHAR(255),
    address       TEXT,
    manager_id    BIGINT,
    capacity      INT,
    used_capacity INT NOT NULL DEFAULT 0,
    phone         VARCHAR(64),
    is_active     BOOLEAN NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_warehouse_manager FOREIGN KEY (manager_id) REFERENCES users (user_id),
    INDEX idx_warehouse_manager (manager_id)
);

CREATE TABLE stock_levels (
    stock_id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    warehouse_id     BIGINT NOT NULL,
    product_id       BIGINT NOT NULL,
    quantity           INT NOT NULL DEFAULT 0,
    reserved_quantity  INT NOT NULL DEFAULT 0,
    bin_location       VARCHAR(128),
    version            BIGINT NOT NULL DEFAULT 0,
    last_updated       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_wh_product (warehouse_id, product_id),
    CONSTRAINT fk_stock_wh FOREIGN KEY (warehouse_id) REFERENCES warehouses (warehouse_id),
    CONSTRAINT fk_stock_product FOREIGN KEY (product_id) REFERENCES products (product_id)
);

CREATE TABLE suppliers (
    supplier_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    name           VARCHAR(255) NOT NULL,
    contact_person VARCHAR(255),
    email          VARCHAR(255),
    phone          VARCHAR(64),
    address        TEXT,
    city           VARCHAR(128),
    country        VARCHAR(128),
    tax_id         VARCHAR(64),
    payment_terms  VARCHAR(64),
    lead_time_days INT NOT NULL DEFAULT 0,
    rating         DECIMAL(3,2) NOT NULL DEFAULT 0,
    is_active      BOOLEAN NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE purchase_orders (
    po_id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    supplier_id      BIGINT NOT NULL,
    warehouse_id     BIGINT NOT NULL,
    created_by_id    BIGINT NOT NULL,
    status           VARCHAR(32) NOT NULL,
    total_amount     DECIMAL(19,4) NOT NULL DEFAULT 0,
    order_date       DATE NOT NULL,
    expected_date    DATE,
    received_date    DATE,
    notes            TEXT,
    reference_number VARCHAR(64),
    created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_po_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers (supplier_id),
    CONSTRAINT fk_po_wh FOREIGN KEY (warehouse_id) REFERENCES warehouses (warehouse_id),
    CONSTRAINT fk_po_user FOREIGN KEY (created_by_id) REFERENCES users (user_id),
    INDEX idx_po_status (status),
    INDEX idx_po_dates (order_date, expected_date)
);

CREATE TABLE po_line_items (
    line_item_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    po_id        BIGINT NOT NULL,
    product_id   BIGINT NOT NULL,
    quantity     INT NOT NULL,
    unit_cost    DECIMAL(19,4) NOT NULL,
    total_cost   DECIMAL(19,4) NOT NULL,
    received_qty INT NOT NULL DEFAULT 0,
    CONSTRAINT fk_line_po FOREIGN KEY (po_id) REFERENCES purchase_orders (po_id) ON DELETE CASCADE,
    CONSTRAINT fk_line_product FOREIGN KEY (product_id) REFERENCES products (product_id),
    INDEX idx_line_po (po_id)
);

CREATE TABLE stock_movements (
    movement_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id      BIGINT NOT NULL,
    warehouse_id    BIGINT NOT NULL,
    movement_type   VARCHAR(32) NOT NULL,
    quantity        INT NOT NULL,
    reference_id    BIGINT,
    reference_type  VARCHAR(64),
    unit_cost       DECIMAL(19,4),
    performed_by    BIGINT NOT NULL,
    notes           TEXT,
    movement_date   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    balance_after   INT NOT NULL,
    CONSTRAINT fk_mov_product FOREIGN KEY (product_id) REFERENCES products (product_id),
    CONSTRAINT fk_mov_wh FOREIGN KEY (warehouse_id) REFERENCES warehouses (warehouse_id),
    CONSTRAINT fk_mov_user FOREIGN KEY (performed_by) REFERENCES users (user_id),
    INDEX idx_mov_product_wh (product_id, warehouse_id),
    INDEX idx_mov_date (movement_date),
    INDEX idx_mov_type (movement_type)
);

CREATE TABLE alerts (
    alert_id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    recipient_id        BIGINT NOT NULL,
    alert_type          VARCHAR(32) NOT NULL,
    severity            VARCHAR(16) NOT NULL,
    title               VARCHAR(255) NOT NULL,
    message             TEXT,
    related_product_id  BIGINT,
    related_warehouse_id BIGINT,
    related_po_id       BIGINT,
    channel             VARCHAR(32) NOT NULL DEFAULT 'IN_APP',
    is_read             BOOLEAN NOT NULL DEFAULT FALSE,
    is_acknowledged     BOOLEAN NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_alert_user FOREIGN KEY (recipient_id) REFERENCES users (user_id),
    INDEX idx_alert_recipient (recipient_id, is_acknowledged)
);

CREATE TABLE inventory_snapshots (
    snapshot_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    warehouse_id  BIGINT NOT NULL,
    product_id    BIGINT NOT NULL,
    quantity      INT NOT NULL,
    stock_value   DECIMAL(19,4) NOT NULL,
    snapshot_date DATE NOT NULL,
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_snap_wh FOREIGN KEY (warehouse_id) REFERENCES warehouses (warehouse_id),
    CONSTRAINT fk_snap_product FOREIGN KEY (product_id) REFERENCES products (product_id),
    INDEX idx_snap_date (snapshot_date)
);

CREATE TABLE audit_logs (
    log_id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    actor_id      BIGINT,
    action_type   VARCHAR(64) NOT NULL,
    entity_type   VARCHAR(64) NOT NULL,
    entity_id     VARCHAR(64),
    detail_json   TEXT,
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_audit_actor (actor_id),
    INDEX idx_audit_created (created_at)
);

