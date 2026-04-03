-- Normalize order/import codes to avoid VN1 vs vn1 duplicates.
UPDATE orders
SET order_code = UPPER(TRIM(order_code))
WHERE order_code IS NOT NULL;

UPDATE order_import_batches
SET import_code = UPPER(TRIM(import_code))
WHERE import_code IS NOT NULL;

ALTER TABLE orders
    ADD COLUMN IF NOT EXISTS current_revision_no INT NOT NULL DEFAULT 1;

-- Keep only the newest pending import for each (company, import code).
WITH ranked AS (
    SELECT id,
           ROW_NUMBER() OVER (
               PARTITION BY company_id, LOWER(import_code)
               ORDER BY created_at DESC, id DESC
           ) AS rn
    FROM order_import_batches
    WHERE status = 'PENDING_APPROVAL'
)
UPDATE order_import_batches b
SET status = 'REJECTED',
    updated_at = CURRENT_TIMESTAMP
FROM ranked r
WHERE b.id = r.id
  AND r.rn > 1;

CREATE UNIQUE INDEX IF NOT EXISTS uk_order_import_batches_pending_company_code
    ON order_import_batches (company_id, LOWER(import_code))
    WHERE status = 'PENDING_APPROVAL';

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM (
            SELECT LOWER(order_code) AS normalized_code, COUNT(*) AS total
            FROM orders
            GROUP BY LOWER(order_code)
            HAVING COUNT(*) > 1
        ) duplicates
    ) THEN
        RAISE EXCEPTION 'Duplicate order_code values ignoring case exist. Please clean data before migration V33.';
    END IF;
END $$;

CREATE UNIQUE INDEX IF NOT EXISTS uk_orders_order_code_lower
    ON orders (LOWER(order_code));

CREATE TABLE IF NOT EXISTS order_revisions (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    revision_no INT NOT NULL,
    source_type VARCHAR(30) NOT NULL,
    import_batch_id BIGINT REFERENCES order_import_batches(id) ON DELETE SET NULL,
    snapshot_json TEXT NOT NULL,
    created_by_user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_order_revisions_order_revision UNIQUE (order_id, revision_no)
);

CREATE INDEX IF NOT EXISTS idx_order_revisions_order_created
    ON order_revisions(order_id, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_order_revisions_import_batch
    ON order_revisions(import_batch_id);

CREATE TABLE IF NOT EXISTS order_events (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    revision_no INT,
    event_type VARCHAR(40) NOT NULL,
    from_status VARCHAR(30),
    to_status VARCHAR(30),
    actor_user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    actor_role VARCHAR(20),
    note TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_order_events_order_created
    ON order_events(order_id, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_order_events_order_revision
    ON order_events(order_id, revision_no);
