-- Add phone number field to users table
ALTER TABLE users ADD COLUMN IF NOT EXISTS phone VARCHAR(20);
