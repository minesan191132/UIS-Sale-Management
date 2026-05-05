-- V40: Bảng theo dõi sequence cho mã đơn hàng tự sinh (UIS-PH-YY-XXXX / UIS-GC-YY-XXXX)
CREATE TABLE order_sequence_tracker (
    id            BIGSERIAL PRIMARY KEY,
    prefix_type   VARCHAR(10) NOT NULL,
    year          INT         NOT NULL,
    current_value INT         NOT NULL DEFAULT 0,
    CONSTRAINT uk_prefix_year UNIQUE (prefix_type, year)
);

COMMENT ON COLUMN order_sequence_tracker.prefix_type IS 'Loại mã: UIS-PH hoặc UIS-GC';
COMMENT ON COLUMN order_sequence_tracker.year IS 'Năm (ví dụ: 2026)';
COMMENT ON COLUMN order_sequence_tracker.current_value IS 'Giá trị đếm hiện tại';
