-- V24: Add order_type column to orders table
-- CUSTOM_MANUFACTURING = đơn gia công (25-A...)
-- READY_MADE = đơn sản phẩm catalog (ORD-...)

ALTER TABLE orders ADD COLUMN IF NOT EXISTS order_type VARCHAR(30) NOT NULL DEFAULT 'CUSTOM_MANUFACTURING';

-- Backfill: ORD-... orders are product orders
UPDATE orders SET order_type = 'READY_MADE' WHERE order_code LIKE 'ORD-%';

