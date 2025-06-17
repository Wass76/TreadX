-- Add base address columns to user table for sales agents
ALTER TABLE "user" 
ADD COLUMN base_country_id BIGINT,
ADD COLUMN base_state_id BIGINT,
ADD COLUMN base_city_id BIGINT;

-- Add foreign key constraints
ALTER TABLE "user" 
ADD CONSTRAINT fk_user_base_country 
FOREIGN KEY (base_country_id) REFERENCES system_country(id);

ALTER TABLE "user" 
ADD CONSTRAINT fk_user_base_state 
FOREIGN KEY (base_state_id) REFERENCES system_province(id);

ALTER TABLE "user" 
ADD CONSTRAINT fk_user_base_city 
FOREIGN KEY (base_city_id) REFERENCES system_city(id);

-- Add indexes for better performance
CREATE INDEX idx_user_base_country ON "user"(base_country_id);
CREATE INDEX idx_user_base_state ON "user"(base_state_id);
CREATE INDEX idx_user_base_city ON "user"(base_city_id); 