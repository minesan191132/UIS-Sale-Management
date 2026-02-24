-- =============================================
-- V11: Cleanup Redundant Columns in Orders Table
-- Removes duplicate and unused fields
-- =============================================

-- Step 1: Rename customer_id to user_id (more accurate naming)
-- This is done in multiple steps to handle existing data safely

-- First, add the new column
ALTER TABLE orders ADD COLUMN user_id BIGINT;

-- Copy data from customer_id to user_id (if customer_id has data)
UPDATE orders SET user_id = customer_id WHERE customer_id IS NOT NULL;

-- Add foreign key constraint for user_id
ALTER TABLE orders
ADD CONSTRAINT fk_orders_user
FOREIGN KEY (user_id) REFERENCES users(id)
ON DELETE SET NULL;

-- Create index for better query performance
CREATE INDEX idx_orders_user_id ON orders(user_id);

-- Step 2: Drop redundant columns
-- These columns are no longer used or are duplicates

-- Drop created_by_user_id (never used in Java code)
ALTER TABLE orders DROP COLUMN IF EXISTS created_by_user_id;

-- Drop invoice_id (redundant - Invoice has order_id)
ALTER TABLE orders DROP COLUMN IF EXISTS invoice_id;

-- Drop customer_note and internal_note (replaced by notes column in V9)
ALTER TABLE orders DROP COLUMN IF EXISTS customer_note;
ALTER TABLE orders DROP COLUMN IF EXISTS internal_note;

-- Drop order_date (duplicate of created_at)
ALTER TABLE orders DROP COLUMN IF EXISTS order_date;

-- Step 3: Now we can safely drop customer_id
-- Drop the index first
DROP INDEX IF EXISTS idx_orders_customer_id;

-- Drop the foreign key constraint
ALTER TABLE orders DROP CONSTRAINT IF EXISTS fk_orders_customer;

-- Drop the column
ALTER TABLE orders DROP COLUMN IF EXISTS customer_id;

-- Step 4: Add comments for documentation
COMMENT ON COLUMN orders.user_id IS 'ID of the user who created this order (renamed from customer_id for clarity)';

-- Verification query (for manual check)
-- SELECT column_name, data_type FROM information_schema.columns WHERE table_name = 'orders' ORDER BY ordinal_position;
