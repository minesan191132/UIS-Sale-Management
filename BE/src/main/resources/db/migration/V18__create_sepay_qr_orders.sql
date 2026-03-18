-- =============================================
-- V16: Create sepay_qr_orders table
-- Bảng lưu đơn QR thanh toán SePay (luồng độc lập,
-- khác với payments/deposit cũ)
-- =============================================

CREATE TABLE IF NOT EXISTS sepay_qr_orders (
    id                   BIGSERIAL PRIMARY KEY,
    order_id             BIGINT NOT NULL,
    amount               NUMERIC(15, 2) NOT NULL,
    status               VARCHAR(20) NOT NULL DEFAULT 'pending',
    paid_amount          NUMERIC(15, 2),
    sepay_transaction_id BIGINT,
    created_at           TIMESTAMP NOT NULL DEFAULT NOW(),
    paid_at              TIMESTAMP
);

-- Unique constraint để chống duplicate webhook
CREATE UNIQUE INDEX IF NOT EXISTS idx_sepay_qr_orders_txn_id
    ON sepay_qr_orders (sepay_transaction_id)
    WHERE sepay_transaction_id IS NOT NULL;

-- Index để query nhanh theo order_id
CREATE INDEX IF NOT EXISTS idx_sepay_qr_orders_order_id
    ON sepay_qr_orders (order_id);

COMMENT ON TABLE sepay_qr_orders IS 'SePay QR payment records - standalone payment flow (PAY-{orderId})';
COMMENT ON COLUMN sepay_qr_orders.status IS 'pending | paid';
COMMENT ON COLUMN sepay_qr_orders.sepay_transaction_id IS 'SePay webhook transaction ID - prevents duplicate processing';
