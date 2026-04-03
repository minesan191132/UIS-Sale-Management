-- Increase length of status column in orders table to accommodate AWAITING_REMAINING_PAYMENT (26 chars)
ALTER TABLE orders ALTER COLUMN status TYPE VARCHAR(30);
