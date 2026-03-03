-- =============================================
-- V8: Update users table for JWT authentication
-- =============================================

-- Add full_name column for users
ALTER TABLE users ADD COLUMN IF NOT EXISTS full_name VARCHAR(200);

-- Ensure password_hash is NOT NULL for authentication
-- If existing data has NULL passwords, you may need to handle this differently
ALTER TABLE users ALTER COLUMN password_hash SET NOT NULL;

-- Ensure company_id relationship works (already exists, but adding FK if missing)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints 
        WHERE constraint_name = 'fk_user_company' 
        AND table_name = 'users'
    ) THEN
        ALTER TABLE users
            ADD CONSTRAINT fk_user_company
            FOREIGN KEY (company_id)
            REFERENCES companies(id)
            ON DELETE SET NULL;
    END IF;
END $$;

-- Ensure company table has new fields from VietQR
ALTER TABLE companies ADD COLUMN IF NOT EXISTS representative VARCHAR(200);
ALTER TABLE companies ADD COLUMN IF NOT EXISTS company_name VARCHAR(500);

-- If companies.name exists, migrate to company_name
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'companies' AND column_name = 'name'
    ) AND EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'companies' AND column_name = 'company_name'
    ) THEN
        -- Copy data from name to company_name
        UPDATE companies SET company_name = name WHERE company_name IS NULL;
        -- Drop old name column
        ALTER TABLE companies DROP COLUMN IF EXISTS name;
    END IF;
END $$;

-- Add indexes for better performance
CREATE INDEX IF NOT EXISTS idx_user_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_user_company ON users(company_id);
