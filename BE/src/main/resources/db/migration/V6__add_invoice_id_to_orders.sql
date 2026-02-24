-- =============================================
-- V6: Add invoice_id to orders table
-- =============================================

-- Add invoice_id column to orders table
-- This links orders to invoices (needed by InvoiceService)
ALTER TABLE orders
    ADD COLUMN invoice_id BIGINT;

-- Add foreign key constraint if invoices table exists
-- Note: Adjust table name if invoices table is named differently
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'invoices') THEN
        ALTER TABLE orders
            ADD CONSTRAINT fk_order_invoice
            FOREIGN KEY (invoice_id)
            REFERENCES invoices(id)
            ON DELETE SET NULL;
    END IF;
END $$;

-- Add index for better query performance
CREATE INDEX idx_order_invoice ON orders(invoice_id);
