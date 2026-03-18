-- =============================================
-- V16: Create user_addresses table
-- =============================================

CREATE TABLE IF NOT EXISTS user_addresses (
    id          BIGSERIAL       PRIMARY KEY,
    user_id     BIGINT          NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    full_name   VARCHAR(200)    NOT NULL,
    phone       VARCHAR(20)     NOT NULL,
    province    VARCHAR(100)    NOT NULL,
    district    VARCHAR(100)    NOT NULL,
    ward        VARCHAR(100)    NOT NULL,
    detail      VARCHAR(500)    NOT NULL,
    is_default  BOOLEAN         DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_user_addresses_user_id ON user_addresses(user_id);
