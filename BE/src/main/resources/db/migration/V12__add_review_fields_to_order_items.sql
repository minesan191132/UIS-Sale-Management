-- V12: Add review workflow fields to order_items
-- Admin reviews each item before quoting
-- Note: unit_price already exists from V3, reusing it for review pricing

ALTER TABLE order_items
ADD COLUMN review_status VARCHAR(20) DEFAULT 'PENDING_REVIEW';

ALTER TABLE order_items
ADD COLUMN admin_note TEXT;

-- Update existing items to PENDING_REVIEW
UPDATE order_items SET review_status = 'PENDING_REVIEW' WHERE review_status IS NULL;

COMMENT ON COLUMN order_items.review_status IS 'Item review status: PENDING_REVIEW, APPROVED, REJECTED, NEED_DISCUSSION';
COMMENT ON COLUMN order_items.admin_note IS 'Admin note: reason for rejection or discussion points';
