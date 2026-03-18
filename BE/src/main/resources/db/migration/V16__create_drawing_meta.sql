-- Drawing metadata (weight, stock) per drawing_number
CREATE TABLE IF NOT EXISTS drawing_meta (
    id BIGSERIAL PRIMARY KEY,
    drawing_number VARCHAR(100) NOT NULL UNIQUE,
    weight DECIMAL(10, 2),
    stock INTEGER DEFAULT 0,
    default_unit_price DECIMAL(15, 2),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_drawing_meta_number ON drawing_meta(drawing_number);
