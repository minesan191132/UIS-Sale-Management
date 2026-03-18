-- V21: Add delivery_date column to order_items for partial shipment support
ALTER TABLE order_items ADD COLUMN IF NOT EXISTS delivery_date DATE;

CREATE INDEX IF NOT EXISTS idx_order_item_delivery_date ON order_items(delivery_date);
