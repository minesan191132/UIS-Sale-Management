CREATE TABLE order_import_batches (
    id BIGSERIAL PRIMARY KEY,
    import_code VARCHAR(50) NOT NULL,
    source_type VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING_APPROVAL',
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    company_id BIGINT NOT NULL REFERENCES companies(id) ON DELETE RESTRICT,
    approved_order_id BIGINT REFERENCES orders(id) ON DELETE SET NULL,
    original_filename VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_order_import_batches_status ON order_import_batches(status);
CREATE INDEX idx_order_import_batches_user ON order_import_batches(user_id);
CREATE INDEX idx_order_import_batches_company ON order_import_batches(company_id);
CREATE INDEX idx_order_import_batches_code ON order_import_batches(import_code);

CREATE TABLE order_import_items (
    id BIGSERIAL PRIMARY KEY,
    batch_id BIGINT NOT NULL REFERENCES order_import_batches(id) ON DELETE CASCADE,
    item_code VARCHAR(50),
    drawing_number VARCHAR(100),
    part_name VARCHAR(200),
    specification VARCHAR(200),
    material_type VARCHAR(100),
    quantity INT NOT NULL,
    unit VARCHAR(50),
    notes TEXT,
    delivery_date DATE
);

CREATE INDEX idx_order_import_items_batch ON order_import_items(batch_id);
CREATE INDEX idx_order_import_items_drawing ON order_import_items(drawing_number);
