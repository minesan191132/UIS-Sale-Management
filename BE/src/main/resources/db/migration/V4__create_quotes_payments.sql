-- =============================================
-- V4: Quotation & Payment
-- =============================================

-- Quotes table
CREATE TABLE quotes (
    id BIGSERIAL PRIMARY KEY,
    
    -- One-to-One with Order
    order_id BIGINT UNIQUE NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    
    -- Pricing breakdown
    base_price DECIMAL(15, 2) NOT NULL,
    setup_cost DECIMAL(15, 2) DEFAULT 0,
    total_price DECIMAL(15, 2) NOT NULL,
    
    -- Deposit calculation
    deposit_percentage INT DEFAULT 30,
    deposit_required DECIMAL(15, 2) NOT NULL,
    
    -- Quote validity
    expires_at TIMESTAMP,
    
    -- Status
    status VARCHAR(20) DEFAULT 'PENDING',
    
    -- Notes
    notes TEXT,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    approved_at TIMESTAMP
);

CREATE INDEX idx_quote_order ON quotes(order_id);

-- Payments table
CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    
    -- Parent order
    order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE RESTRICT,
    
    -- Payment details
    amount DECIMAL(15, 2) NOT NULL,
    
    -- Type
    payment_type VARCHAR(20) NOT NULL,
    
    -- Method
    payment_method VARCHAR(50),
    
    -- Transaction reference
    transaction_ref VARCHAR(100),
    
    -- Verification (cho manual transfers)
    verified_at TIMESTAMP,
    verified_by BIGINT REFERENCES users(id),
    
    -- Notes
    notes TEXT,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_payment_order ON payments(order_id);
CREATE INDEX idx_payment_type ON payments(payment_type);
