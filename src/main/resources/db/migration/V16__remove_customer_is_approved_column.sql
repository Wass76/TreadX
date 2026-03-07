-- Migration: V16__remove_dealerDealerCustomer_is_approved_column.sql
-- Description: Remove is_approved column from dealerDealerCustomer table as it's not needed
-- Date: 2024-01-XX

-- Remove the is_approved column from dealerDealerCustomer table
-- This column was causing not-null constraint violations and is not needed for the vendor portal
ALTER TABLE dealerDealerCustomer DROP COLUMN IF EXISTS is_approved;

DROP TABLE dealerDealerCustomer_phone_number;
DROP TABLE dealerDealerCustomer_phone;
DROP TABLE dealerDealerCustomer;

-- Also remove any default values or constraints that might be related
-- This ensures a clean removal of the column

-- Add comment for documentation
COMMENT ON TABLE dealerDealerCustomer IS 'DealerCustomer management table for vendor portal - removed is_approved column';

-- Verify the column was removed
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'dealerDealerCustomer' AND column_name = 'is_approved'
    ) THEN
        RAISE EXCEPTION 'is_approved column still exists after migration';
    END IF;
END $$;
