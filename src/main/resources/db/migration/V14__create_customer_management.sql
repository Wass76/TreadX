-- Migration: V14__create_dealerDealerCustomer_management.sql
-- Description: Create dealerDealerCustomer management tables for vendor portal
-- Date: 2024-01-XX

-- Create dealerDealerCustomer table
CREATE TABLE dealerDealerCustomer (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL,
    
    -- Address Information (Embedded for vendor portal simplicity)
    street_number VARCHAR(20),
    street_name VARCHAR(255),
    apt_unit_bldg VARCHAR(50),
    postal_code VARCHAR(20),
    
    -- Vendor Relationship
    vendor_id BIGINT NOT NULL,
    dealerDealerCustomer_unique_id VARCHAR(100) UNIQUE,
    
    -- Audit fields (inherited from AuditedEntity)
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by BIGINT NOT NULL,
    last_modified_by BIGINT,
    created_by_user_type VARCHAR(50),
    last_modified_by_user_type VARCHAR(50)
);

-- Create dealerDealerCustomer_phone table
CREATE TABLE dealerDealerCustomer_phone (
    id BIGSERIAL PRIMARY KEY,
    dealerDealerCustomer_id BIGINT NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    phone_type VARCHAR(20) NOT NULL,
    phone_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    extension VARCHAR(10),
    notes TEXT,
    
    -- Audit fields (inherited from AuditedEntity)
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by BIGINT NOT NULL,
    last_modified_by BIGINT,
    created_by_user_type VARCHAR(50),
    last_modified_by_user_type VARCHAR(50)
);

-- Add foreign key constraints
ALTER TABLE dealerDealerCustomer 
ADD CONSTRAINT fk_dealerDealerCustomer_vendor 
FOREIGN KEY (vendor_id) REFERENCES vendor(id) ON DELETE CASCADE;

ALTER TABLE dealerDealerCustomer_phone 
ADD CONSTRAINT fk_dealerDealerCustomer_phone_dealerDealerCustomer 
FOREIGN KEY (dealerDealerCustomer_id) REFERENCES dealerDealerCustomer(id) ON DELETE CASCADE;

-- Add indexes for performance
CREATE INDEX idx_dealerDealerCustomer_vendor_id ON dealerDealerCustomer(vendor_id);
CREATE INDEX idx_dealerDealerCustomer_email ON dealerDealerCustomer(email);
CREATE INDEX idx_dealerDealerCustomer_unique_id ON dealerDealerCustomer(dealerDealerCustomer_unique_id);
CREATE INDEX idx_dealerDealerCustomer_name ON dealerDealerCustomer(first_name, last_name);
CREATE INDEX idx_dealerDealerCustomer_address ON dealerDealerCustomer(street_number, street_name, postal_code);

CREATE INDEX idx_dealerDealerCustomer_phone_dealerDealerCustomer_id ON dealerDealerCustomer_phone(dealerDealerCustomer_id);
CREATE INDEX idx_dealerDealerCustomer_phone_number ON dealerDealerCustomer_phone(phone_number);
CREATE INDEX idx_dealerDealerCustomer_phone_type ON dealerDealerCustomer_phone(phone_type);
CREATE INDEX idx_dealerDealerCustomer_phone_status ON dealerDealerCustomer_phone(phone_status);
CREATE INDEX idx_dealerDealerCustomer_phone_primary ON dealerDealerCustomer_phone(dealerDealerCustomer_id, is_primary);

-- Create sequences for ID generation
CREATE SEQUENCE IF NOT EXISTS dealerDealerCustomer_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS dealerDealerCustomer_phone_id_seq START WITH 1 INCREMENT BY 1;

-- Insert sample data for testing
INSERT INTO dealerDealerCustomer (
    first_name, last_name, email, 
    street_number, street_name, apt_unit_bldg, postal_code,
    vendor_id, dealerDealerCustomer_unique_id,
    created_by, created_by_user_type
) VALUES 
('John', 'Doe', 'john.doe@example.com', 
 '123', 'Main Street', 'Apt 4B', 'M5V 3A8',
 1, 'CUST' || nextval('dealerDealerCustomer_id_seq'),
 1, 'VENDOR_STAFF'),
 
('Jane', 'Smith', 'jane.smith@example.com', 
 '456', 'Oak Avenue', 'Unit 2C', 'V6B 1A1',
 1, 'CUST' || nextval('dealerDealerCustomer_id_seq'),
 1, 'VENDOR_STAFF');

-- Insert sample phone numbers
INSERT INTO dealerDealerCustomer_phone (
    dealerDealerCustomer_id, phone_number, phone_type, phone_status, is_primary, extension, notes,
    created_by, created_by_user_type
) VALUES 
(1, '+1-416-555-0101', 'CELL', 'ACTIVE', true, NULL, 'Primary contact number',
 1, 'VENDOR_STAFF'),
 
(1, '+1-416-555-0102', 'HOME', 'ACTIVE', false, NULL, 'Home phone number',
 1, 'VENDOR_STAFF'),
 
(2, '+1-604-555-0201', 'CELL', 'ACTIVE', true, NULL, 'Primary contact number',
 1, 'VENDOR_STAFF'),
 
(2, '+1-604-555-0202', 'BUSINESS', 'ACTIVE', false, '101', 'Office extension',
 1, 'VENDOR_STAFF');
