-- V9: Add columns for new Order Management System
-- Maps new Order entity fields to database schema

-- Add customer_id (reference to user who created order)
ALTER TABLE orders
ADD COLUMN customer_id BIGINT;

-- Add deposit amount (70% of total for payment)
ALTER TABLE orders
ADD COLUMN deposit_amount DECIMAL(15, 2);

-- Add payment QR URL
ALTER TABLE orders
ADD COLUMN payment_qr_url VARCHAR(500);

-- Add paid timestamp
ALTER TABLE orders
ADD COLUMN paid_at TIMESTAMP;

-- Add notes field (combines customer_note + internal_note)
ALTER TABLE orders
ADD COLUMN notes TEXT;

-- Add foreign key constraint for customer
ALTER TABLE orders
ADD CONSTRAINT fk_orders_customer
FOREIGN KEY (customer_id) REFERENCES users(id)
ON DELETE SET NULL;

-- Add index for faster queries
CREATE INDEX idx_orders_customer_id ON orders(customer_id);

-- Add comments
COMMENT ON COLUMN orders.customer_id IS 'ID of the customer (user) who created this order';
COMMENT ON COLUMN orders.deposit_amount IS 'Deposit amount required (typically 70% of total)';
COMMENT ON COLUMN orders.payment_qr_url IS 'QR code URL for payment';
COMMENT ON COLUMN orders.paid_at IS 'Timestamp when payment was confirmed';
COMMENT ON COLUMN orders.notes IS 'Order notes (customer + admin)';

