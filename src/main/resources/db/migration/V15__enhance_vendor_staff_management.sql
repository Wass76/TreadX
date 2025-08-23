-- Migration: V15__enhance_vendor_staff_management.sql
-- Description: Enhance vendor staff management system for vendor portal
-- Date: 2024-12-XX

-- Update vendor_staff table to use proper audit fields
ALTER TABLE vendor_staff 
ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
ADD COLUMN IF NOT EXISTS created_by BIGINT,
ADD COLUMN IF NOT EXISTS last_modified_by BIGINT;

-- Add indexes for better performance
CREATE INDEX IF NOT EXISTS idx_vendor_staff_vendor_id ON vendor_staff(vendor_id);
CREATE INDEX IF NOT EXISTS idx_vendor_staff_user_email ON vendor_staff(user_id);
CREATE INDEX IF NOT EXISTS idx_vendor_staff_access_level ON vendor_staff(access_level);

-- Insert sample vendor staff data for testing
INSERT INTO vendor_staff (user_id, vendor_id, district_code, access_level, created_at, updated_at, created_by) 
SELECT 
    u.id, 
    1, 
    'DEFAULT', 
    CASE 
        WHEN u.role_id = (SELECT id FROM roles WHERE name = 'VENDOR_ADMIN') THEN 'OWNER'
        WHEN u.role_id = (SELECT id FROM roles WHERE name = 'VENDOR_EMPLOYEE') THEN 'MANAGER'
        WHEN u.role_id = (SELECT id FROM roles WHERE name = 'VENDOR_TECHNICIAN') THEN 'MECHANIC'
        ELSE 'VIEWER'
    END,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    1
FROM users u 
WHERE u.role_id IN (
    SELECT id FROM roles WHERE name IN ('VENDOR_ADMIN', 'VENDOR_EMPLOYEE', 'VENDOR_TECHNICIAN')
)
AND NOT EXISTS (
    SELECT 1 FROM vendor_staff vs WHERE vs.user_id = u.id
);

-- Create sequence for vendor_staff if not exists
CREATE SEQUENCE IF NOT EXISTS vendor_staff_id_seq START WITH 1 INCREMENT BY 1;

-- Add comments for documentation
COMMENT ON TABLE vendor_staff IS 'Vendor staff management table for vendor portal access control';
COMMENT ON COLUMN vendor_staff.access_level IS 'Access level: OWNER (full access), MANAGER (customer/employee management), MECHANIC (tire operations), VIEWER (read-only)';
COMMENT ON COLUMN vendor_staff.district_code IS 'Territory/district code for multi-tenant support';
