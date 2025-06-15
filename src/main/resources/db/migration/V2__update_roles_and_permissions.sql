-- Update existing permissions
UPDATE permissions 
SET is_system_generated = true 
WHERE name IN (
    'USER_CREATE', 'USER_READ', 'USER_UPDATE', 'USER_DELETE',
    'LEAD_CREATE', 'LEAD_READ', 'LEAD_UPDATE', 'LEAD_DELETE',
    'CONTACT_CREATE', 'CONTACT_READ', 'CONTACT_UPDATE', 'CONTACT_DELETE',
    'DEALER_CREATE', 'DEALER_READ', 'DEALER_UPDATE', 'DEALER_DELETE'
);

-- Update existing roles
UPDATE roles 
SET is_system_generated = true 
WHERE name IN (
    'PLATFORM_ADMIN', 'SALES_MANAGER', 'SALES_AGENT', 'SUPPORT_AGENT',
    'DEALER_ADMIN', 'DEALER_EMPLOYEE', 'DEALER_TECHNICIAN'
);

-- Add new permissions if they don't exist
INSERT INTO permissions (name, description, resource, action, is_active, is_system_generated)
SELECT 'DEALER_CONTACT_CREATE', 'Create dealer contacts', 'DEALER_CONTACT', 'CREATE', true, true
WHERE NOT EXISTS (SELECT 1 FROM permissions WHERE name = 'DEALER_CONTACT_CREATE');

INSERT INTO permissions (name, description, resource, action, is_active, is_system_generated)
SELECT 'DEALER_CONTACT_READ', 'View dealer contacts', 'DEALER_CONTACT', 'READ', true, true
WHERE NOT EXISTS (SELECT 1 FROM permissions WHERE name = 'DEALER_CONTACT_READ');

INSERT INTO permissions (name, description, resource, action, is_active, is_system_generated)
SELECT 'DEALER_CONTACT_UPDATE', 'Update dealer contacts', 'DEALER_CONTACT', 'UPDATE', true, true
WHERE NOT EXISTS (SELECT 1 FROM permissions WHERE name = 'DEALER_CONTACT_UPDATE');

INSERT INTO permissions (name, description, resource, action, is_active, is_system_generated)
SELECT 'DEALER_CONTACT_DELETE', 'Delete dealer contacts', 'DEALER_CONTACT', 'DELETE', true, true
WHERE NOT EXISTS (SELECT 1 FROM permissions WHERE name = 'DEALER_CONTACT_DELETE');

-- Update role permissions
-- Platform Admin gets all permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE r.name = 'PLATFORM_ADMIN'
AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp 
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
);

-- Sales Manager permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE r.name = 'SALES_MANAGER'
AND p.name IN (
    'USER_CREATE', 'USER_READ',
    'LEAD_CREATE', 'LEAD_READ', 'LEAD_UPDATE',
    'CONTACT_CREATE', 'CONTACT_READ', 'CONTACT_UPDATE',
    'DEALER_READ', 'DEALER_CONTACT_CREATE', 'DEALER_CONTACT_READ', 'DEALER_CONTACT_UPDATE'
)
AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp 
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
);

-- Sales Agent permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE r.name = 'SALES_AGENT'
AND p.name IN (
    'LEAD_CREATE', 'LEAD_READ', 'LEAD_UPDATE',
    'CONTACT_CREATE', 'CONTACT_READ', 'CONTACT_UPDATE',
    'DEALER_READ', 'DEALER_CONTACT_CREATE', 'DEALER_CONTACT_READ', 'DEALER_CONTACT_UPDATE'
)
AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp 
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
);

-- Support Agent permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE r.name = 'SUPPORT_AGENT'
AND p.name IN (
    'LEAD_READ',
    'CONTACT_READ',
    'DEALER_READ', 'DEALER_CONTACT_READ'
)
AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp 
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
); 