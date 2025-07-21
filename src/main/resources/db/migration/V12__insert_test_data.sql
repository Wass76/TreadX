-- Insert test users for different roles
INSERT INTO user (email, password, first_name, last_name, phone_number, is_active, created_at, updated_at) VALUES
('admin@treadx.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Platform', 'Admin', '+1234567890', true, NOW(), NOW()),
('manager@treadx.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Sales', 'Manager', '+1234567891', true, NOW(), NOW()),
('agent@treadx.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Sales', 'Agent', '+1234567892', true, NOW(), NOW()),
('vendor1@treadx.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Vendor', 'Owner', '+1234567893', true, NOW(), NOW()),
('vendor2@treadx.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Vendor', 'Manager', '+1234567894', true, NOW(), NOW()),
('vendor3@treadx.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Vendor', 'Mechanic', '+1234567895', true, NOW(), NOW());

-- Insert sample leads for testing
INSERT INTO leads (business_name, contact_person, phone_number, email, street_number, street_name, apt_unit_bldg, postal_code, city, province, country, status, created_at, updated_at) VALUES
('Test Business 1', 'John Doe', '+1234567890', 'john@test1.com', '123', 'Main St', 'Apt 1', 'A1B2C3', 'Toronto', 'ON', 'Canada', 'PENDING', NOW(), NOW()),
('Test Business 2', 'Jane Smith', '+1234567891', 'jane@test2.com', '456', 'Oak Ave', 'Unit 2', 'D4E5F6', 'Vancouver', 'BC', 'Canada', 'APPROVED', NOW(), NOW()),
('Test Business 3', 'Bob Johnson', '+1234567892', 'bob@test3.com', '789', 'Pine Rd', 'Suite 3', 'G7H8I9', 'Montreal', 'QC', 'Canada', 'REJECTED', NOW(), NOW()),
('Test Business 4', 'Alice Brown', '+1234567893', 'alice@test4.com', '321', 'Elm St', 'Apt 4', 'J1K2L3', 'Calgary', 'AB', 'Canada', 'PENDING', NOW(), NOW()),
('Test Business 5', 'Charlie Wilson', '+1234567894', 'charlie@test5.com', '654', 'Maple Dr', 'Unit 5', 'M4N5O6', 'Edmonton', 'AB', 'Canada', 'APPROVED', NOW(), NOW());

-- Update territory access for the new test users
INSERT INTO user_territory_access (user_id, territory_code, access_level, created_by) VALUES
(1, 'N6B', 'ADMIN', 1),  -- Platform Admin has access to N6B
(1, 'N5V', 'ADMIN', 1),  -- Platform Admin has access to N5V
(1, 'N7A', 'ADMIN', 1),  -- Platform Admin has access to N7A
(2, 'N6B', 'WRITE', 1),  -- Sales Manager has write access to N6B
(3, 'N6B', 'READ', 1);   -- Sales Agent has read access to N6B

-- Update vendor staff for the new test users
INSERT INTO vendor_staff (user_id, vendor_id, district_code, access_level) VALUES
(4, 1, 'N6B', 'OWNER'),    -- Vendor Owner
(5, 1, 'N6B', 'MANAGER'),  -- Vendor Manager
(6, 1, 'N6B', 'MECHANIC'); -- Vendor Mechanic 