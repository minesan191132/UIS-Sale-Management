-- =============================================
-- V3: Orders - Unified for both Catalog + Custom
-- =============================================

-- Orders table
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    
    -- Unique identifier
    order_code VARCHAR(50) UNIQUE NOT NULL,
    
    -- B2B relationship
    company_id BIGINT NOT NULL REFERENCES companies(id) ON DELETE RESTRICT,
    created_by_user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    
    -- Order type (để phân biệt nguồn)
    order_type VARCHAR(20) DEFAULT 'CUSTOM',
    
    -- Delivery information
    delivery_address TEXT,
    phone_contact VARCHAR(20),
    order_date DATE DEFAULT CURRENT_DATE,
    delivery_date DATE,
    
    -- Financial
    total_amount DECIMAL(15, 2) DEFAULT 0,
    
    -- Status workflow
    status VARCHAR(20) DEFAULT 'PENDING_QUOTE',
    
    -- Production linkage (sẽ add constraint sau khi tạo production_batches)
    production_batch_id BIGINT,
    
    -- Notes
    customer_note TEXT,
    internal_note TEXT,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_order_company ON orders(company_id);
CREATE INDEX idx_order_status ON orders(status);
CREATE INDEX idx_order_delivery_date ON orders(delivery_date);
CREATE INDEX idx_order_batch ON orders(production_batch_id);

-- Order Items table (supports both catalog + custom)
CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    
    -- Parent order
    order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    
    -- Link to catalog product (nullable for custom orders)
    product_id BIGINT REFERENCES products(id) ON DELETE SET NULL,
    
    -- Snapshot data (ALWAYS filled)
    item_code VARCHAR(50),
    part_name VARCHAR(200),
    
    -- TECHNICAL SPECS (CRITICAL for Production Matrix)
    drawing_number VARCHAR(100) NOT NULL,
    material_type VARCHAR(100),
    specification VARCHAR(200),
    
    -- Quantity & Pricing
    quantity INT NOT NULL,
    unit_price DECIMAL(15, 2),
    total_price DECIMAL(15, 2),
    
    -- Production tracking
    is_finished BOOLEAN DEFAULT FALSE,
    finished_at TIMESTAMP,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_order_item_order ON order_items(order_id);
CREATE INDEX idx_order_item_product ON order_items(product_id);
CREATE INDEX idx_order_item_drawing ON order_items(drawing_number);
