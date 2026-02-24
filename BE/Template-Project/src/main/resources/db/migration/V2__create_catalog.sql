-- =============================================
-- V2: Public Catalog - Categories & Products
-- =============================================

-- Categories table (Product grouping with shared images)
CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    
    -- Display info
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(100) UNIQUE,
    description TEXT,
    
    -- Default image for entire category (KEY FEATURE for lazy photography!)
    default_image_url TEXT,
    
    -- Hierarchy (optional - for nested categories)
    parent_id BIGINT REFERENCES categories(id) ON DELETE SET NULL,
    
    -- Display order
    display_order INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Products table (Pre-manufactured items for sale)
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    
    -- Categorization
    category_id BIGINT REFERENCES categories(id) ON DELETE SET NULL,
    
    -- Unique identifier (must match Excel Item Code)
    sku VARCHAR(50) UNIQUE NOT NULL,
    
    -- Display information
    name VARCHAR(200) NOT NULL,
    slug VARCHAR(200) UNIQUE,
    description TEXT,
    
    -- Pricing
    price DECIMAL(15, 2) DEFAULT 0,
    
    -- Technical specifications (TEMPLATE for auto-fill)
    default_drawing_number VARCHAR(100),
    default_material VARCHAR(100),
    default_specification VARCHAR(200),
    
    -- Media (optional - if null, use category.default_image_url)
    image_url TEXT,
    
    -- Stock (optional)
    stock_quantity INT DEFAULT 0,
    
    -- Status
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_product_sku ON products(sku);
CREATE INDEX idx_product_category ON products(category_id);
CREATE INDEX idx_product_active ON products(is_active);
