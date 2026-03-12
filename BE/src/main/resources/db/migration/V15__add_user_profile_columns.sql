-- =============================================
-- V15: Add user profile columns (dob, gender, phone, last_login)
-- =============================================

ALTER TABLE users ADD COLUMN IF NOT EXISTS phone VARCHAR(20);
ALTER TABLE users ADD COLUMN IF NOT EXISTS gender VARCHAR(10);
ALTER TABLE users ADD COLUMN IF NOT EXISTS dob_day INTEGER;
ALTER TABLE users ADD COLUMN IF NOT EXISTS dob_month INTEGER;
ALTER TABLE users ADD COLUMN IF NOT EXISTS dob_year INTEGER;
ALTER TABLE users ADD COLUMN IF NOT EXISTS last_login TIMESTAMP;
