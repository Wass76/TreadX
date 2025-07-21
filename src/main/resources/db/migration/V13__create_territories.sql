-- Create territories table for managing territory configuration and database connections
CREATE TABLE IF NOT EXISTS territories (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(10) UNIQUE NOT NULL, -- N6B, N5V, LONDON, ONTARIO, etc.
    name VARCHAR(100) NOT NULL, -- North 6B District, London City, etc.
    level VARCHAR(20) NOT NULL CHECK (level IN ('DISTRICT', 'CITY', 'PROVINCE', 'COUNTRY')),
    parent_territory_code VARCHAR(10) REFERENCES territories(code),
    database_url VARCHAR(255) NOT NULL, -- jdbc:postgresql://localhost:5432/treadx_n6b
    database_name VARCHAR(50) NOT NULL, -- treadx_n6b
    database_username VARCHAR(50) NOT NULL, -- n6b_admin
    database_password VARCHAR(255) NOT NULL, -- encrypted password
    is_active BOOLEAN NOT NULL DEFAULT true,
    description VARCHAR(500),
    timezone VARCHAR(50), -- America/Toronto
    currency VARCHAR(3), -- CAD, USD, etc.
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT REFERENCES users(id),
    last_modified_by BIGINT REFERENCES users(id),
    
    -- Ensure proper hierarchy constraints
    CONSTRAINT check_territory_hierarchy CHECK (
        (level = 'DISTRICT' AND parent_territory_code IS NOT NULL) OR
        (level = 'CITY' AND parent_territory_code IS NOT NULL) OR
        (level = 'PROVINCE' AND parent_territory_code IS NOT NULL) OR
        (level = 'COUNTRY' AND parent_territory_code IS NULL)
    )
);

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_territories_code ON territories (code);
CREATE INDEX IF NOT EXISTS idx_territories_active ON territories (is_active);
CREATE INDEX IF NOT EXISTS idx_territories_level ON territories (level);
CREATE INDEX IF NOT EXISTS idx_territories_parent ON territories (parent_territory_code);
CREATE INDEX IF NOT EXISTS idx_territories_level_active ON territories (level, is_active);
CREATE INDEX IF NOT EXISTS idx_territories_parent_active ON territories (parent_territory_code, is_active);

-- Create composite indexes for hierarchical queries
CREATE INDEX IF NOT EXISTS idx_territories_hierarchy ON territories (level, parent_territory_code, is_active);

-- Add comments for documentation
COMMENT ON TABLE territories IS 'Stores territory configuration including database connection information';
COMMENT ON COLUMN territories.code IS 'Unique territory code (e.g., N6B, LONDON, ONTARIO)';
COMMENT ON COLUMN territories.name IS 'Human-readable territory name';
COMMENT ON COLUMN territories.level IS 'Territory level in hierarchy: DISTRICT, CITY, PROVINCE, COUNTRY';
COMMENT ON COLUMN territories.parent_territory_code IS 'Parent territory code for hierarchical relationships';
COMMENT ON COLUMN territories.database_url IS 'JDBC URL for territory-specific database';
COMMENT ON COLUMN territories.database_name IS 'Database name for the territory';
COMMENT ON COLUMN territories.database_username IS 'Database username for the territory';
COMMENT ON COLUMN territories.database_password IS 'Encrypted database password for the territory';
COMMENT ON COLUMN territories.is_active IS 'Whether the territory is currently active';
COMMENT ON COLUMN territories.description IS 'Optional description of the territory';
COMMENT ON COLUMN territories.timezone IS 'Timezone for the territory (e.g., America/Toronto)';
COMMENT ON COLUMN territories.currency IS 'Currency code for the territory (e.g., CAD, USD)';

-- Insert initial territories for testing
INSERT INTO territories (code, name, level, parent_territory_code, database_url, database_name, database_username, database_password, description, timezone, currency) VALUES
('CANADA', 'Canada', 'COUNTRY', NULL, 'jdbc:postgresql://localhost:5432/treadx_canada', 'treadx_canada', 'canada_admin', 'password', 'Canada country territory', 'America/Toronto', 'CAD'),
('ONTARIO', 'Ontario', 'PROVINCE', 'CANADA', 'jdbc:postgresql://localhost:5432/treadx_ontario', 'treadx_ontario', 'ontario_admin', 'password', 'Ontario province territory', 'America/Toronto', 'CAD'),
('LONDON', 'London', 'CITY', 'ONTARIO', 'jdbc:postgresql://localhost:5432/treadx_london', 'treadx_london', 'london_admin', 'password', 'London city territory', 'America/Toronto', 'CAD'),
('N6B', 'North 6B District', 'DISTRICT', 'LONDON', 'jdbc:postgresql://localhost:5432/treadx_n6b', 'treadx_n6b', 'n6b_admin', 'password', 'North 6B district territory', 'America/Toronto', 'CAD'),
('N5V', 'North 5V District', 'DISTRICT', 'LONDON', 'jdbc:postgresql://localhost:5432/treadx_n5v', 'treadx_n5v', 'n5v_admin', 'password', 'North 5V district territory', 'America/Toronto', 'CAD'),
('N7A', 'North 7A District', 'DISTRICT', 'LONDON', 'jdbc:postgresql://localhost:5432/treadx_n7a', 'treadx_n7a', 'n7a_admin', 'password', 'North 7A district territory', 'America/Toronto', 'CAD')
ON CONFLICT (code) DO NOTHING; 