-- =============================================
-- V5: Production Matrix (Core Feature)
-- =============================================

-- Production Batches table
CREATE TABLE production_batches (
    id BIGSERIAL PRIMARY KEY,
    
    -- Unique identifier
    batch_code VARCHAR(50) UNIQUE NOT NULL,
    
    -- Batch information
    export_date DATE DEFAULT CURRENT_DATE,
    
    -- Creator
    created_by BIGINT NOT NULL REFERENCES users(id),
    
    -- Notes
    notes TEXT,
    
    -- Print tracking
    printed_at TIMESTAMP,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_batch_code ON production_batches(batch_code);
CREATE INDEX idx_batch_date ON production_batches(export_date);

-- Batch Items table (Aggregated by drawing_number)
CREATE TABLE batch_items (
    id BIGSERIAL PRIMARY KEY,
    
    -- Parent batch
    batch_id BIGINT NOT NULL REFERENCES production_batches(id) ON DELETE CASCADE,
    
    -- Grouping key (from order_items.drawing_number)
    drawing_number VARCHAR(100) NOT NULL,
    
    -- Aggregated technical specs
    part_name VARCHAR(200),
    material_type VARCHAR(100),
    specification VARCHAR(200),
    
    -- Aggregated quantity
    total_quantity INT NOT NULL,
    
    -- Order breakdown (JSON format)
    -- Example: {"ORD-001": 10, "ORD-002": 5}
    order_breakdown TEXT,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_batch_item_batch ON batch_items(batch_id);
CREATE INDEX idx_batch_item_drawing ON batch_items(drawing_number);

-- Add FK constraint to orders table
ALTER TABLE orders 
  ADD CONSTRAINT fk_order_batch 
  FOREIGN KEY (production_batch_id) 
  REFERENCES production_batches(id) ON DELETE SET NULL;
