-- Fix platform admin user role from SALES_AGENT to PLATFORM_ADMIN
-- This migration fixes the issue where the platform admin user was assigned the wrong role

-- First, get the correct role IDs
-- PLATFORM_ADMIN should be role ID 1 (created first)
-- SALES_AGENT should be role ID 3 (created third)

-- Update the platform admin user (admin@gmail.com) to have PLATFORM_ADMIN role
UPDATE "user" 
SET role_id = (SELECT id FROM roles WHERE name = 'PLATFORM_ADMIN' LIMIT 1)
WHERE email = 'admin@gmail.com' 
AND role_id = (SELECT id FROM roles WHERE name = 'SALES_AGENT' LIMIT 1);

-- Also ensure the super admin user (wasee.tenbakji@gmail.com) has PLATFORM_ADMIN role
UPDATE "user" 
SET role_id = (SELECT id FROM roles WHERE name = 'PLATFORM_ADMIN' LIMIT 1)
WHERE email = 'wasee.tenbakji@gmail.com' 
AND role_id != (SELECT id FROM roles WHERE name = 'PLATFORM_ADMIN' LIMIT 1); 