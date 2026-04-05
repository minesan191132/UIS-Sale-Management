ALTER TABLE order_import_batches
    ADD COLUMN IF NOT EXISTS rejection_reason VARCHAR(500),
    ADD COLUMN IF NOT EXISTS rejected_by_role VARCHAR(20);
