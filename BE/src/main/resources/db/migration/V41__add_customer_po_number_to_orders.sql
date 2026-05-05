-- V41__add_customer_po_number_to_orders.sql

-- 1. Add customer_po_number column to orders table
ALTER TABLE orders
ADD COLUMN customer_po_number VARCHAR(100) NULL;

-- 2. Add unique constraint to prevent the same company from importing the same PO number multiple times
ALTER TABLE orders
ADD CONSTRAINT uc_orders_company_customer_po UNIQUE (company_id, customer_po_number);
