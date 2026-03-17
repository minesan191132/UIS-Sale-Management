-- V17: Ensure customer_id column exists on orders table
-- V9 may not have run on all environments, so we add IF NOT EXISTS here

ALTER TABLE orders
    ADD COLUMN IF NOT EXISTS customer_id BIGINT;

-- Add FK constraint if not already exists
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE constraint_name = 'fk_orders_customer'
        AND table_name = 'orders'
    ) THEN
        ALTER TABLE orders
            ADD CONSTRAINT fk_orders_customer
            FOREIGN KEY (customer_id) REFERENCES users(id)
            ON DELETE SET NULL;
    END IF;
END $$;

-- Add index if not exists
CREATE INDEX IF NOT EXISTS idx_orders_customer_id ON orders(customer_id);

COMMENT ON COLUMN orders.customer_id IS 'ID of the customer (user) who created this order';
