-- =============================================
-- V7: Seed default company for testing
-- =============================================

-- Insert a default company for development/testing
-- In production, this should be replaced with actual company data
-- NOTE: Using 'name' column here because V8 will rename it to 'company_name'
INSERT INTO companies (name, tax_code, address, phone, email, created_at)
VALUES 
    ('Default Company', 'TAX001', 'Hanoi, Vietnam', '0123456789', 'contact@company.com', CURRENT_TIMESTAMP)
ON CONFLICT (tax_code) DO NOTHING;

-- Note: The ID will be auto-generated (likely ID=1)
-- Frontend should use this company_id when calling /api/materials/import
