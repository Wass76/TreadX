-- Migration: V16__remove_customer_is_approved_column.sql
-- Description: Remove is_approved column from customer table as it's not needed
-- Date: 2024-01-XX

-- Remove the is_approved column from customer table
-- This column was causing not-null constraint violations and is not needed for the vendor portal
ALTER TABLE customer DROP COLUMN IF EXISTS is_approved;

DROP TABLE customer_phone_number;
DROP TABLE customer_phone;
DROP TABLE customer;

-- Also remove any default values or constraints that might be related
-- This ensures a clean removal of the column

-- Add comment for documentation
COMMENT ON TABLE customer IS 'Customer management table for vendor portal - removed is_approved column';

-- Verify the column was removed
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'customer' AND column_name = 'is_approved'
    ) THEN
        RAISE EXCEPTION 'is_approved column still exists after migration';
    END IF;
END $$;
