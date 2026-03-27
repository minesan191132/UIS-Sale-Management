-- V27: Normalize legacy order_type values after enum rename
-- Older schemas/data may still store CUSTOM; current enum expects CUSTOM_MANUFACTURING.

UPDATE orders
SET order_type = 'CUSTOM_MANUFACTURING'
WHERE order_type = 'CUSTOM';

ALTER TABLE orders
    ALTER COLUMN order_type SET DEFAULT 'CUSTOM_MANUFACTURING';
