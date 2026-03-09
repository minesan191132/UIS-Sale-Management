-- =============================================
-- V13: Add SePay transaction fields to payments
-- Thêm field lưu giao dịch SePay webhook
-- =============================================

-- Thêm sepay_transaction_id để tránh xử lý trùng webhook
ALTER TABLE payments
    ADD COLUMN IF NOT EXISTS sepay_transaction_id BIGINT;

-- Index để check trùng transaction nhanh hơn
CREATE UNIQUE INDEX IF NOT EXISTS idx_payment_sepay_txn
    ON payments(sepay_transaction_id)
    WHERE sepay_transaction_id IS NOT NULL;

-- Comment
COMMENT ON COLUMN payments.sepay_transaction_id IS 'SePay webhook transaction ID - used to prevent duplicate processing';
