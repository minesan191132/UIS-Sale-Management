-- =============================================
-- V1: B2B Foundation - Companies & Users
-- =============================================

-- Companies table (B2B Customers with Tax ID)
CREATE TABLE companies (
    id BIGSERIAL PRIMARY KEY,
    
    -- Unique identifier (Tax Code)
    tax_code VARCHAR(50) UNIQUE NOT NULL,
    
    -- Company information
    name VARCHAR(200) NOT NULL,
    address TEXT,
    phone VARCHAR(20),
    email VARCHAR(100),
    
    -- KYC verification
    verification_status VARCHAR(20) DEFAULT 'PENDING',
    business_license_url TEXT,
    verified_at TIMESTAMP,
    
    -- Metadata
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_company_tax_code ON companies(tax_code);

-- Users table (SSO-integrated users belonging to companies)
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    
    -- B2B relationship
    company_id BIGINT REFERENCES companies(id) ON DELETE CASCADE,
    
    -- Authentication
    username VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255),
    
    -- User details
    full_name VARCHAR(100),
    
    -- Authorization
    role VARCHAR(20) DEFAULT 'CUSTOMER',
    is_active BOOLEAN DEFAULT TRUE,
    
    -- Metadata
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP
);

CREATE INDEX idx_user_company ON users(company_id);
CREATE INDEX idx_user_email ON users(email);
