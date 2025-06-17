# Sales Agent Base Address Feature

## Overview

This feature allows sales agents to have a base address (country, state, and city) that automatically populates when they create new leads or dealers. This reduces data entry and ensures consistency in address information.

## Features

### 1. User Base Address Management

Sales agents can now have base address information stored in their user profile:
- **Base Country**: The country where the sales agent primarily operates
- **Base State/Province**: The state or province where the sales agent primarily operates  
- **Base City**: The city where the sales agent primarily operates

### 2. Automatic Address Population

When a sales agent creates a new lead or dealer:
- If the address request is missing country, state, or city information
- The system automatically populates these fields from the sales agent's base address
- The user only needs to provide the remaining address details (street name, postal code, etc.)

### 3. API Endpoints

#### Update User Base Address
```
PUT /api/v1/users/{id}/base-address
```

**Request Body:**
```json
{
  "baseCountryId": 1,
  "baseStateId": 5,
  "baseCityId": 25
}
```

**Response:**
```json
{
  "id": 1,
  "email": "agent@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "position": "Sales Agent",
  "role": {
    "name": "SALES_AGENT",
    "description": "Sales Agent"
  },
  "baseCountryId": 1,
  "baseCountryName": "United States",
  "baseStateId": 5,
  "baseStateName": "California",
  "baseCityId": 25,
  "baseCityName": "Los Angeles"
}
```

## Implementation Details

### Database Changes

A new migration `V8__add_user_base_address.sql` adds the following columns to the `user` table:
- `base_country_id` - Foreign key to `system_country.id`
- `base_state_id` - Foreign key to `system_province.id`  
- `base_city_id` - Foreign key to `system_city.id`

### Code Changes

#### 1. User Entity
- Added base address fields with JPA relationships
- Added getter/setter methods for base address fields

#### 2. DTOs
- `UserRequestDTO`: Added base address fields
- `UserCreateRequestDTO`: Added base address fields  
- `UserResponseDTO`: Added base address fields with names

#### 3. Services
- `UserService`: Added methods to handle base address updates
- `AddressAutoPopulationService`: New service to handle automatic address population logic
- `LeadsService`: Updated to use automatic address population
- `DealerService`: Updated to use automatic address population

#### 4. Controllers
- `UserController`: Added endpoint to update user base address

## Usage Examples

### Setting Base Address for Sales Agent

1. **Create a sales agent with base address:**
```json
POST /api/v1/users
{
  "email": "agent@example.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe",
  "roleId": 3,
  "position": "Sales Agent",
  "baseCountryId": 1,
  "baseStateId": 5,
  "baseCityId": 25
}
```

2. **Update existing user's base address:**
```json
PUT /api/v1/users/1/base-address
{
  "baseCountryId": 1,
  "baseStateId": 5,
  "baseCityId": 25
}
```

### Creating Leads with Auto-Populated Address

When a sales agent creates a lead, they can provide minimal address information:

```json
POST /api/v1/leads
{
  "businessName": "ABC Company",
  "businessEmail": "contact@abc.com",
  "phoneNumber": "+1234567890",
  "address": {
    "streetName": "Main Street",
    "streetNumber": "123",
    "postalCode": "90210"
    // countryId, stateId, cityId will be auto-populated from user's base address
  }
}
```

The system will automatically populate:
- `countryId` from user's `baseCountryId`
- `stateId` from user's `baseStateId`  
- `cityId` from user's `baseCityId`

### Creating Dealers with Auto-Populated Address

Similarly for dealers:

```json
POST /api/v1/dealers
{
  "name": "XYZ Dealership",
  "email": "contact@xyz.com",
  "phone": "+1234567890",
  "address": {
    "streetName": "Business Ave",
    "streetNumber": "456",
    "postalCode": "90211"
    // countryId, stateId, cityId will be auto-populated from user's base address
  }
}
```

## Security

- Only users with `PLATFORM_ADMIN` role or the user themselves can update base address information
- The automatic population only works for users with `SALES_AGENT` role
- Address fields are only auto-populated if they are not already provided in the request

## Benefits

1. **Reduced Data Entry**: Sales agents don't need to repeatedly enter the same country, state, and city information
2. **Consistency**: Ensures all leads and dealers created by a sales agent have consistent address information
3. **Efficiency**: Speeds up the lead and dealer creation process
4. **Flexibility**: Users can still override the auto-populated fields if needed

## Future Enhancements

1. **Address Validation**: Add validation to ensure the selected city belongs to the selected state, and state belongs to the selected country
2. **Multiple Base Addresses**: Allow sales agents to have multiple base addresses for different regions
3. **Address Templates**: Allow creating address templates for common locations
4. **Bulk Operations**: Support for bulk updating base addresses for multiple users 