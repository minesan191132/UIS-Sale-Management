-- V25: Add order-level delivery_date to orders table
-- Used for: Cron Job (7-day remaining payment reminder) and UI display
ALTER TABLE orders ADD COLUMN IF NOT EXISTS delivery_date DATE;

CREATE INDEX IF NOT EXISTS idx_orders_delivery_date ON orders(delivery_date);
