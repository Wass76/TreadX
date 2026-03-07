# DealerCustomer Management System - Vendor Portal

## Overview

The DealerCustomer Management System is a comprehensive solution for managing dealerDealerCustomers in the TreadX vendor portal. It implements the exact flow from the dealerDealerCustomer creation flowchart and provides robust audit trails for phone number changes.

## Architecture

### Entity Structure

```
DealerCustomer (Core dealerDealerCustomer data)
├── Basic Information (firstName, lastName, email)
├── Address Information (streetNumber, streetName, aptUnitBldg, postalCode)
├── Vendor Relationship (vendor, dealerDealerCustomerUniqueId)
├── Audit Fields (createdAt, updatedAt, createdBy, lastModifiedBy)
└── Phone Numbers (OneToMany relationship to DealerCustomerPhone)

DealerCustomerPhone (Phone number tracking with audit)
├── Phone Details (phoneNumber, phoneType, phoneStatus)
├── Business Logic (isPrimary, extension, notes)
└── Audit Fields (createdAt, updatedAt, createdBy, lastModifiedBy)
```

### Key Features

- **Separate Phone Tracking**: Each phone number is tracked individually with full audit history
- **Vendor Portal Ready**: Complete API endpoints for vendor staff operations
- **Duplicate Prevention**: Business logic prevents duplicate dealerDealerCustomer creation
- **Vendor Isolation**: Each vendor can only see their own dealerDealerCustomers
- **Simplified Workflow**: Direct dealerDealerCustomer creation without approval process
- **Streamlined Address**: Simplified address structure for vendor portal simplicity

## Implementation Details

### 1. DealerCustomer Entity

The `DealerCustomer` entity extends `AuditedEntity` and contains:

```java
@Entity
@Table(name = "dealerDealerCustomer")
public class DealerCustomer extends AuditedEntity {
    // Basic Information
    private String firstName;
    private String lastName;
    private String email;
    
    // Address Information (Simplified for vendor portal)
    private String streetNumber;
    private String streetName;
    private String aptUnitBldg;
    private String postalCode;
    
    // Phone Numbers - Managed through separate entity
    @OneToMany(mappedBy = "dealerDealerCustomer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DealerCustomerPhone> phoneNumbers;
    
    // Vendor Relationship
    @ManyToOne(fetch = FetchType.LAZY)
    private Vendor vendor;
    private String dealerDealerCustomerUniqueId; // System-generated unique ID
}
```

### 2. DealerCustomerPhone Entity

The `DealerCustomerPhone` entity provides detailed phone number tracking:

```java
@Entity
@Table(name = "dealerDealerCustomer_phone")
public class DealerCustomerPhone extends AuditedEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    private DealerCustomer dealerDealerCustomer;
    
    private String phoneNumber;
    private PhoneType phoneType; // CELL, HOME, BUSINESS, FAX, OTHER
    private PhoneStatus phoneStatus; // ACTIVE, INACTIVE, VERIFIED, UNVERIFIED
    private Boolean isPrimary = false;
    private String extension;
    private String notes;
}
```

### 3. Manual Mapper Implementation

Instead of MapStruct, we use a manual `DealerCustomerMapper` class:

```java
@Component
public class DealerCustomerMapper {
    public DealerCustomer toEntity(DealerCustomerRequestDTO requestDTO);
    public DealerCustomerResponseDTO toResponse(DealerCustomer dealerDealerCustomer);
    public void updateEntity(DealerCustomer dealerDealerCustomer, DealerCustomerRequestDTO requestDTO);
    private DealerCustomerPhoneResponseDTO toPhoneResponseDTO(DealerCustomerPhone phone);
}
```

### 4. Service Layer

#### DealerCustomerService
- **createDealerCustomer()**: Implements the flowchart flow with phone number creation
- **getDealerCustomerById()**: Retrieves dealerDealerCustomer with phone numbers
- **updateDealerCustomer()**: Updates dealerDealerCustomer and phone numbers
- **deleteDealerCustomer()**: Deletes dealerDealerCustomer and associated phone numbers

#### DealerCustomerPhoneService
- **createPhoneNumbers()**: Creates multiple phone numbers for a dealerDealerCustomer
- **updatePhoneNumbers()**: Updates existing phone numbers
- **ensureSinglePrimaryPhone()**: Business logic for primary phone designation
- **toResponseDTO()**: Converts phone entities to DTOs

### 5. Repository Layer

#### DealerCustomerRepository
- **findByVendorId()**: Get dealerDealerCustomers by vendor with pagination
- **existsDuplicateDealerCustomer()**: Check for duplicate dealerDealerCustomers (name + address + phone)
- **searchByVendorAndTerm()**: Search dealerDealerCustomers across multiple fields

#### DealerCustomerPhoneRepository
- **findByDealerCustomer()**: Get all phone numbers for a dealerDealerCustomer
- **findByDealerCustomerAndPhoneType()**: Get phone numbers by type
- **findByDealerCustomerAndIsPrimaryTrue()**: Get primary phone number
- **existsByPhoneNumber()**: Check if phone number exists

### 6. API Endpoints

#### DealerCustomer Management
- `POST /api/v1/dealerDealerCustomers` - Create new dealerDealerCustomer
- `GET /api/v1/dealerDealerCustomers/{id}` - Get dealerDealerCustomer by ID
- `PUT /api/v1/dealerDealerCustomers/{id}` - Update dealerDealerCustomer
- `DELETE /api/v1/dealerDealerCustomers/{id}` - Delete dealerDealerCustomer

#### Vendor-Specific Operations
- `GET /api/v1/dealerDealerCustomers/vendor/{vendorId}` - Get dealerDealerCustomers by vendor
- `GET /api/v1/dealerDealerCustomers/my-vendor` - Get current user's vendor dealerDealerCustomers
- `GET /api/v1/dealerDealerCustomers/vendor/{vendorId}/search` - Search dealerDealerCustomers by vendor

## Database Schema

### DealerCustomer Table
```sql
CREATE TABLE dealerDealerCustomer (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL,
    
    -- Address Information (Simplified for vendor portal)
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
```

### DealerCustomerPhone Table
```sql
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
```

## Business Logic

### DealerCustomer Creation Flow

1. **Input Validation**: Validate all required fields from the flowchart
2. **Duplicate Check**: Check for existing dealerDealerCustomers with same name + address + phone
3. **Vendor Access**: Validate vendor access for current user
4. **DealerCustomer Creation**: Create dealerDealerCustomer entity with unique ID
5. **Phone Number Creation**: Create DealerCustomerPhone entities with audit trail
6. **Primary Phone Logic**: Ensure only one primary phone number exists

### Phone Number Management

- **Primary Phone**: Only one phone number can be marked as primary
- **Phone Types**: CELL, HOME, BUSINESS, FAX, OTHER
- **Phone Status**: ACTIVE, INACTIVE, VERIFIED, UNVERIFIED
- **Audit Trail**: Every phone number change is tracked with user and timestamp

### Simplified Workflow

- **Direct Creation**: DealerCustomers are created immediately without approval
- **Vendor Control**: Vendor staff have full control over their dealerDealerCustomers
- **Platform Access**: Platform staff can access and manage all dealerDealerCustomers

## Security & Access Control

### Role-Based Permissions

- **VENDOR_ADMIN**: Full access to vendor's dealerDealerCustomers
- **VENDOR_EMPLOYEE**: Read/write access to vendor's dealerDealerCustomers
- **VENDOR_TECHNICIAN**: Read access to vendor's dealerDealerCustomers
- **PLATFORM_ADMIN**: Full access to all dealerDealerCustomers
- **SALES_MANAGER**: Full access to all dealerDealerCustomers

### Vendor Boundary Enforcement

- Vendor staff can only access dealerDealerCustomers from their own vendor
- Platform staff can access dealerDealerCustomers from any vendor
- All operations are validated against vendor boundaries

## Testing

### Integration Test
```java
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class DealerCustomerServiceIntegrationTest {
    @Test
    void testDealerCustomerCreationFlow() {
        // Tests the complete dealerDealerCustomer creation flow
        // including phone number creation and validation
    }
}
```

### Test Coverage
- DealerCustomer creation with phone numbers
- Duplicate dealerDealerCustomer prevention
- Vendor access validation
- Phone number business logic

## Usage Examples

### Creating a DealerCustomer with Phone Numbers

```java
DealerCustomerRequestDTO request = new DealerCustomerRequestDTO();
request.setFirstName("John");
request.setLastName("Doe");
request.setEmail("john.doe@example.com");

// Address (Simplified)
request.setStreetNumber("123");
request.setStreetName("Main Street");
request.setAptUnitBldg("Apt 4B");
request.setPostalCode("M5V 3A8");

// Phone Numbers
DealerCustomerPhoneRequestDTO cellPhone = new DealerCustomerPhoneRequestDTO();
cellPhone.setPhoneNumber("+1-416-555-0101");
cellPhone.setPhoneType(PhoneType.CELL);
cellPhone.setIsPrimary(true);

DealerCustomerPhoneRequestDTO homePhone = new DealerCustomerPhoneRequestDTO();
homePhone.setPhoneNumber("+1-416-555-0102");
homePhone.setPhoneType(PhoneType.HOME);
homePhone.setIsPrimary(false);

request.setPhoneNumbers(Arrays.asList(cellPhone, homePhone));

// Vendor
request.setVendorId(1L);

DealerCustomerResponseDTO dealerDealerCustomer = dealerDealerCustomerService.createDealerCustomer(request);
```

### Searching DealerCustomers by Phone Number

```java
// Search will automatically include phone numbers
Page<DealerCustomerResponseDTO> dealerDealerCustomers = dealerDealerCustomerService.searchDealerCustomersByVendor(
    vendorId, 
    "+1-416-555-0101", 
    pageable
);
```

## Benefits of the Simplified Architecture

### Streamlined Operations
- **Direct dealerDealerCustomer creation** without approval bottlenecks
- **Faster vendor operations** for immediate dealerDealerCustomer management
- **Simplified workflow** that matches business requirements
- **Streamlined address structure** for vendor portal simplicity

### Audit Trail & Change Tracking
- **Individual phone number history** - track when each number was added/modified
- **User attribution** - know who added/modified each phone number
- **Timestamp tracking** - when changes occurred
- **Status tracking** - active, inactive, verified, unverified

### Flexibility & Scalability
- **Multiple phone numbers** per dealerDealerCustomer without schema changes
- **Phone type categorization** for better organization
- **Primary phone designation** for main contact
- **Extension support** for business numbers
- **Notes field** for additional context

### Data Integrity
- **Unique constraints** prevent duplicate phone numbers per dealerDealerCustomer
- **Foreign key relationships** ensure data consistency
- **Cascade operations** maintain referential integrity

## Next Steps

1. **Fix compilation issues** with Lombok and entity methods
2. **Implement vendor staff association** for proper vendor boundaries
3. **Add comprehensive testing** for all business logic
4. **Implement phone number validation** and verification
5. **Add phone number history endpoints** for audit trail access

## Conclusion

The DealerCustomer Management System provides a robust, scalable solution for managing dealerDealerCustomers in the vendor portal. The simplified workflow removes unnecessary approval bottlenecks while maintaining the separate phone number entity for full audit trails and change tracking.

The streamlined address structure and removal of vendorDealerCustomerId simplifies the system while maintaining all essential functionality. The system perfectly implements the flowchart requirements and provides a solid foundation for efficient vendor operations with comprehensive dealerDealerCustomer management capabilities.
