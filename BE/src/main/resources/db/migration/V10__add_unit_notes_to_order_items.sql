-- V10: Add missing columns to order_items for new Order Management System
-- Simple orders need unit and notes fields

ALTER TABLE order_items
ADD COLUMN unit VARCHAR(50);

ALTER TABLE order_items
ADD COLUMN notes TEXT;

ALTER TABLE order_items
ALTER COLUMN drawing_number DROP NOT NULL;

COMMENT ON COLUMN order_items.unit IS 'Unit of measurement (e.g., pcs, kg, m)';
COMMENT ON COLUMN order_items.notes IS 'Item-specific notes or delivery instructions';
