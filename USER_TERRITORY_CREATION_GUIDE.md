# User and Territory Creation Guide

## Overview

Now that we have implemented the geographical access control system with proper layered architecture, here are the different ways to create users and assign them to territories.

## **Method 1: Separate Operations (Recommended for Flexibility)**

### **Step 1: Create User**
```bash
POST /api/v1/users
Content-Type: application/json
Authorization: Bearer {your-jwt-token}

{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "password": "SecurePassword123!",
  "position": "Sales Agent",
  "roleId": 3
}
```

**Response:**
```json
{
  "id": 123,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "position": "Sales Agent",
  "role": {
    "id": 3,
    "name": "SALES_AGENT"
  }
}
```

### **Step 2: Assign Territories**
```bash
# For Sales Agent (City level only)
POST /api/user-territories/users/123/territories?level=CITY&cityId=1
Authorization: Bearer {your-jwt-token}

# For Sales Manager (Multiple levels possible)
POST /api/user-territories/users/123/territories?level=PROVINCE&provinceId=5
POST /api/user-territories/users/123/territories?level=COUNTRY&countryId=1
```

**Response:**
```json
{
  "id": 456,
  "userId": 123,
  "level": "CITY",
  "city": {
    "id": 1,
    "name": "Los Angeles"
  },
  "active": true,
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

## **Method 2: Combined Operation (Better UX)**

### **Create User with Territories in One Request**
```bash
POST /api/v1/users/with-territories
Content-Type: application/json
Authorization: Bearer {your-jwt-token}

{
  "firstName": "Sarah",
  "lastName": "Manager",
  "email": "sarah.manager@example.com",
  "password": "SecurePassword123!",
  "position": "Sales Manager",
  "roleId": 2,
  "territories": [
    {
      "level": "CITY",
      "cityId": 1
    },
    {
      "level": "PROVINCE",
      "provinceId": 5
    }
  ]
}
```

**Response:**
```json
{
  "user": {
    "id": 124,
    "firstName": "Sarah",
    "lastName": "Manager",
    "email": "sarah.manager@example.com",
    "position": "Sales Manager",
    "role": {
      "id": 2,
      "name": "SALES_MANAGER"
    }
  },
  "territories": [
    {
      "id": 457,
      "userId": 124,
      "level": "CITY",
      "city": {
        "id": 1,
        "name": "Los Angeles"
      },
      "active": true
    },
    {
      "id": 458,
      "userId": 124,
      "level": "PROVINCE",
      "province": {
        "id": 5,
        "name": "California"
      },
      "active": true
    }
  ],
  "message": "User created successfully with territory assignments"
}
```

## **Territory Level Examples**

### **Sales Agent Territory Assignment**
```bash
# Sales Agent can only be assigned to cities
POST /api/user-territories/users/{userId}/territories?level=CITY&cityId=1
POST /api/user-territories/users/{userId}/territories?level=CITY&cityId=2
```

### **Sales Manager Territory Assignment**
```bash
# Sales Manager can be assigned to cities, provinces, or countries
POST /api/user-territories/users/{userId}/territories?level=CITY&cityId=1
POST /api/user-territories/users/{userId}/territories?level=PROVINCE&provinceId=5
POST /api/user-territories/users/{userId}/territories?level=COUNTRY&countryId=1
```

### **Platform Admin**
```bash
# Platform Admin doesn't need territory assignments (global access)
POST /api/v1/users
{
  "firstName": "Admin",
  "lastName": "User",
  "email": "admin@example.com",
  "password": "SecurePassword123!",
  "position": "Platform Administrator",
  "roleId": 1
}
```

## **Available API Endpoints**

### **User Management**
- `POST /api/v1/users` - Create user
- `POST /api/v1/users/with-territories` - Create user with territories
- `GET /api/v1/users` - Get all users
- `GET /api/v1/users/{id}` - Get user by ID
- `PUT /api/v1/users/{id}` - Update user
- `DELETE /api/v1/users/{id}` - Delete user

### **Territory Management**
- `POST /api/user-territories/users/{userId}/territories` - Assign territory
- `GET /api/user-territories/users/{userId}/territories` - Get user territories
- `GET /api/user-territories/my-territories` - Get current user territories
- `DELETE /api/user-territories/{territoryId}` - Deactivate territory
- `GET /api/user-territories/accessible-cities` - Get accessible cities
- `POST /api/user-territories/check-access` - Check location access
- `GET /api/user-territories/users/{userId}/territories/level/{level}` - Get territories by level
- `GET /api/user-territories/users/{userId}/has-territories` - Check if user has territories

## **Role-Based Territory Rules**

### **SALES_AGENT**
- **Territory Level**: CITY only
- **Data Access**: Only own leads in assigned cities
- **Example**: Assigned to Los Angeles → Can only see leads they created in LA

### **SALES_MANAGER**
- **Territory Level**: CITY, PROVINCE, or COUNTRY
- **Data Access**: ALL leads in assigned territories
- **Examples**:
  - Assigned to Los Angeles (City) → Can see ALL leads in LA
  - Assigned to California (Province) → Can see ALL leads in CA
  - Assigned to United States (Country) → Can see ALL leads in US

### **PLATFORM_ADMIN**
- **Territory Level**: No restrictions
- **Data Access**: Global access to all data

## **Validation Rules**

### **Territory Level Validation**
```sql
-- Database constraint ensures only one geographical entity is set based on level
CONSTRAINT check_territory_level CHECK (
    (level = 'CITY' AND city_id IS NOT NULL AND province_id IS NULL AND country_id IS NULL) OR
    (level = 'PROVINCE' AND city_id IS NULL AND province_id IS NOT NULL AND country_id IS NULL) OR
    (level = 'COUNTRY' AND city_id IS NULL AND province_id IS NULL AND country_id IS NOT NULL)
)
```

### **Role-Based Validation**
- Sales Agent can only be assigned to CITY level
- Sales Manager can be assigned to any level
- Platform Admin doesn't need territory assignments

## **Error Handling**

### **Common Error Responses**
```json
{
  "error": "User not found with id: 123",
  "status": 404
}
```

```json
{
  "error": "No access to this geographical area",
  "status": 403
}
```

```json
{
  "error": "Invalid territory level for Sales Agent role",
  "status": 400
}
```

## **Testing Examples**

### **Create Sales Agent with City Territory**
```bash
# 1. Create user
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{
    "firstName": "John",
    "lastName": "Agent",
    "email": "john.agent@example.com",
    "password": "Password123!",
    "position": "Sales Agent",
    "roleId": 3
  }'

# 2. Assign territory
curl -X POST "http://localhost:8080/api/user-territories/users/123/territories?level=CITY&cityId=1" \
  -H "Authorization: Bearer {token}"
```

### **Create Sales Manager with Multiple Territories**
```bash
curl -X POST http://localhost:8080/api/v1/users/with-territories \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{
    "firstName": "Sarah",
    "lastName": "Manager",
    "email": "sarah.manager@example.com",
    "password": "Password123!",
    "position": "Sales Manager",
    "roleId": 2,
    "territories": [
      {
        "level": "CITY",
        "cityId": 1
      },
      {
        "level": "PROVINCE",
        "provinceId": 5
      }
    ]
  }'
```

## **Best Practices**

1. **Use Method 1** for maximum flexibility and control
2. **Use Method 2** for better user experience when creating multiple users
3. **Always validate territory assignments** against user roles
4. **Use proper error handling** for geographical access validation
5. **Test territory assignments** thoroughly before production deployment
6. **Monitor territory-based access** for performance optimization

This system provides a robust, scalable solution for managing user geographical access while maintaining clean architecture and proper separation of concerns.

## User Territory Assignment & Access Control (2024-Update)

### Flows
- **Assigning Territories:**
  - Endpoint: `POST /api/user-territories/users/{userId}/territories`
  - Only PLATFORM_ADMIN or SALES_MANAGER can assign.
  - Service checks if the user has access to the requested territory.
- **Checking Access:**
  - Endpoint: `POST /api/user-territories/check-access`
  - Uses a find-only approach to avoid creating new system entities.
  - Returns true/false if the user has access to the specified city/province/country.
- **Checking Assignments:**
  - Endpoint: `GET /api/user-territories/users/{userId}/has-territories`
  - Admins can check any user; managers can check if the user is in their managed area; others can only check their own.

### OpenAPI Documentation
- All endpoints in `UserTerritoryController` are annotated with `@Operation` and `@ApiResponses` for clear API docs.
- See Swagger UI for live documentation and try-it-out features.

### Service/Controller Separation
- Controllers handle HTTP and role-based access.
- Services enforce business and authorization logic.
- All sensitive checks (e.g., territory containment, role checks) are in the service layer for maintainability.

### Example Usage
```http
POST /api/user-territories/users/42/territories?level=CITY&cityId=123
```
- Assigns city 123 to user 42 (if allowed by role and access).

See also: `ARCHITECTURAL_IMPROVEMENTS_DISCUSSION.md`, `UNIFIED_GEOGRAPHICAL_SERVICE_IMPLEMENTATION.md`. 