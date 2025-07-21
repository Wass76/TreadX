-- Create user_territory_access table
CREATE TABLE user_territory_access (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    territory_code VARCHAR(10) NOT NULL,
    access_level VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (created_by) REFERENCES users(id),
    UNIQUE KEY uk_user_territory (user_id, territory_code)
);

-- Create vendor_staff table
CREATE TABLE vendor_staff (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    vendor_id BIGINT NOT NULL,
    district_code VARCHAR(10) NOT NULL,
    access_level VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    UNIQUE KEY uk_user_vendor_district (user_id, vendor_id, district_code)
);

-- Insert sample territory access data
INSERT INTO user_territory_access (user_id, territory_code, access_level, created_by) VALUES
(1, 'N6B', 'ADMIN', 1),  -- Platform Admin has access to N6B
(1, 'N5V', 'ADMIN', 1),  -- Platform Admin has access to N5V
(1, 'N7A', 'ADMIN', 1),  -- Platform Admin has access to N7A
(2, 'N6B', 'WRITE', 1),  -- Sales Manager has write access to N6B
(3, 'N6B', 'READ', 1);   -- Sales Agent has read access to N6B

-- Insert sample vendor staff data
INSERT INTO vendor_staff (user_id, vendor_id, district_code, access_level) VALUES
(4, 1, 'N6B', 'OWNER'),    -- Vendor Owner
(5, 1, 'N6B', 'MANAGER'),  -- Vendor Manager
(6, 1, 'N6B', 'MECHANIC'); -- Vendor Mechanic 