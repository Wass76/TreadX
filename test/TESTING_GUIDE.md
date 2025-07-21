# 🧪 TreadX Hybrid Approach Testing Guide

## **Overview**
This guide will help you test the hybrid territory access implementation that provides both automatic and explicit territory resolution.

## **Prerequisites**
1. **Database Setup**: Ensure the migration scripts have been executed
2. **Application Running**: Start the Spring Boot application
3. **Postman**: Import the provided collection for easy testing

## **Test Data Setup**

### **Users Created:**
- **Platform Admin**: `admin@treadx.com` / `password`
- **Sales Manager**: `manager@treadx.com` / `password`
- **Sales Agent**: `agent@treadx.com` / `password`
- **Vendor Owner**: `vendor1@treadx.com` / `password`
- **Vendor Manager**: `vendor2@treadx.com` / `password`
- **Vendor Mechanic**: `vendor3@treadx.com` / `password`

### **Territory Access:**
- **Platform Admin**: Access to N6B, N5V, N7A (ADMIN level)
- **Sales Manager**: Access to N6B (WRITE level)
- **Sales Agent**: Access to N6B (READ level)

### **Sample Leads:**
- 5 test leads with different statuses (PENDING, APPROVED, REJECTED)

## **Testing Steps**

### **Step 1: Authentication Testing**

#### **1.1 Login as Platform Admin**
```bash
POST http://localhost:8080/api/v1/auth/login
Content-Type: application/json

{
  "email": "admin@treadx.com",
  "password": "password"
}
```

**Expected Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "user": {
    "id": 1,
    "email": "admin@treadx.com",
    "firstName": "Platform",
    "lastName": "Admin"
  }
}
```

#### **1.2 Login as Sales Manager**
```bash
POST http://localhost:8080/api/v1/auth/login
Content-Type: application/json

{
  "email": "manager@treadx.com",
  "password": "password"
}
```

#### **1.3 Login as Sales Agent**
```bash
POST http://localhost:8080/api/v1/auth/login
Content-Type: application/json

{
  "email": "agent@treadx.com",
  "password": "password"
}
```

### **Step 2: Security Context Testing**

#### **2.1 Get Current User Info**
```bash
GET http://localhost:8080/api/v1/test/current-user
Authorization: Bearer {token}
```

**Expected Response (Admin):**
```json
{
  "userId": 1,
  "email": "admin@treadx.com",
  "firstName": "Platform",
  "lastName": "Admin",
  "accessibleTerritories": ["N6B", "N5V", "N7A"],
  "isPlatformAdmin": true,
  "isSalesManager": false,
  "isSalesAgent": false,
  "isVendorStaff": false
}
```

**Expected Response (Manager):**
```json
{
  "userId": 2,
  "email": "manager@treadx.com",
  "firstName": "Sales",
  "lastName": "Manager",
  "accessibleTerritories": ["N6B"],
  "isPlatformAdmin": false,
  "isSalesManager": true,
  "isSalesAgent": false,
  "isVendorStaff": false
}
```

#### **2.2 Test Territory Access**
```bash
GET http://localhost:8080/api/v1/test/territory-access/N6B
Authorization: Bearer {adminToken}
```

**Expected Response:**
```json
{
  "territoryCode": "N6B",
  "canAccess": true,
  "accessibleTerritories": ["N6B", "N5V", "N7A"]
}
```

#### **2.3 Test Territory Access (Unauthorized)**
```bash
GET http://localhost:8080/api/v1/test/territory-access/N5V
Authorization: Bearer {managerToken}
```

**Expected Response:**
```json
{
  "territoryCode": "N5V",
  "canAccess": false,
  "accessibleTerritories": ["N6B"]
}
```

#### **2.4 Get Primary Territory**
```bash
GET http://localhost:8080/api/v1/test/primary-territory
Authorization: Bearer {managerToken}
```

**Expected Response (Single Territory User):**
```json
{
  "primaryTerritory": "N6B",
  "accessibleTerritories": ["N6B"]
}
```

**Expected Response (Multi-Territory User - Error):**
```json
{
  "error": "User has access to multiple territories. Please specify territory.",
  "accessibleTerritories": ["N6B", "N5V", "N7A"]
}
```

### **Step 3: Leads API Testing**

#### **3.1 Get My Leads (Automatic)**
```bash
GET http://localhost:8080/api/v1/leads/my-leads
Authorization: Bearer {adminToken}
```

**Expected Response:**
```json
[
  {
    "id": 1,
    "businessName": "Test Business 1",
    "contactPerson": "John Doe",
    "phoneNumber": "+1234567890",
    "email": "john@test1.com",
    "status": "PENDING",
    "createdAt": "2024-01-15T10:00:00"
  },
  // ... more leads
]
```

#### **3.2 Get Leads by Territory (Explicit)**
```bash
GET http://localhost:8080/api/v1/leads/territories/N6B?page=0&size=10
Authorization: Bearer {adminToken}
```

**Expected Response:**
```json
{
  "content": [
    {
      "id": 1,
      "businessName": "Test Business 1",
      "contactPerson": "John Doe",
      "phoneNumber": "+1234567890",
      "email": "john@test1.com",
      "status": "PENDING",
      "createdAt": "2024-01-15T10:00:00"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 5,
  "totalPages": 1
}
```

#### **3.3 Get My Leads by Status (Automatic)**
```bash
GET http://localhost:8080/api/v1/leads/my-leads/status?status=PENDING
Authorization: Bearer {adminToken}
```

**Expected Response:**
```json
[
  {
    "id": 1,
    "businessName": "Test Business 1",
    "contactPerson": "John Doe",
    "phoneNumber": "+1234567890",
    "email": "john@test1.com",
    "status": "PENDING",
    "createdAt": "2024-01-15T10:00:00"
  },
  {
    "id": 4,
    "businessName": "Test Business 4",
    "contactPerson": "Alice Brown",
    "phoneNumber": "+1234567893",
    "email": "alice@test4.com",
    "status": "PENDING",
    "createdAt": "2024-01-15T10:00:00"
  }
]
```

#### **3.4 Get Leads by Territory and Status (Explicit)**
```bash
GET http://localhost:8080/api/v1/leads/territories/N6B/status?status=PENDING&page=0&size=10
Authorization: Bearer {adminToken}
```

## **Test Scenarios**

### **Scenario 1: Platform Admin Access**
1. **Login as Platform Admin**
2. **Test automatic leads access** - Should get leads from all territories
3. **Test explicit territory access** - Should be able to access any territory
4. **Test territory access validation** - Should have access to N6B, N5V, N7A

### **Scenario 2: Sales Manager Access**
1. **Login as Sales Manager**
2. **Test automatic leads access** - Should get leads from N6B only
3. **Test explicit territory access** - Should only access N6B
4. **Test unauthorized territory access** - Should be denied access to N5V

### **Scenario 3: Sales Agent Access**
1. **Login as Sales Agent**
2. **Test automatic leads access** - Should get leads from N6B only
3. **Test read-only access** - Should be able to read but not modify

### **Scenario 4: Multi-Territory User**
1. **Login as Platform Admin**
2. **Test primary territory** - Should get error about multiple territories
3. **Test explicit territory specification** - Should work correctly

## **Expected Behaviors**

### **Automatic Territory Resolution:**
- **Single Territory Users**: Automatically get data from their assigned territory
- **Multi-Territory Users**: Get combined data from all accessible territories
- **Platform Admin**: Get data from all territories

### **Explicit Territory Access:**
- **Valid Territory**: Return data from specified territory
- **Invalid Territory**: Return 403 Forbidden
- **Unauthorized Territory**: Return 403 Forbidden

### **Error Handling:**
- **No Territory Access**: Return 403 Forbidden with "User has no territory access"
- **Multiple Territories**: Return 400 Bad Request with "Please specify territory"
- **Invalid Territory**: Return 403 Forbidden with "Cannot access territory"

## **Troubleshooting**

### **Common Issues:**

1. **Authentication Failed**
   - Check if users exist in database
   - Verify password is correct
   - Check JWT configuration

2. **Territory Access Denied**
   - Verify user_territory_access table has correct data
   - Check territory codes match exactly
   - Verify user roles are properly assigned

3. **No Data Returned**
   - Check if leads exist in database
   - Verify territory codes in leads match user access
   - Check if database connection is working

4. **Compilation Errors**
   - Ensure all required classes are created
   - Check import statements
   - Verify Spring Boot version compatibility

### **Debug Endpoints:**
- `/api/v1/test/current-user` - Check current user info
- `/api/v1/test/territory-access/{territoryCode}` - Test territory access
- `/api/v1/test/primary-territory` - Get primary territory
- `/api/v1/test/all-territory-access` - View all territory access (Admin only)
- `/api/v1/test/all-vendor-staff` - View all vendor staff (Admin only)

## **Next Steps**

After successful testing:
1. **Implement District Database Connection Logic**
2. **Add Vendor Service Hybrid Approach**
3. **Implement Cross-Database Queries**
4. **Add Territory Management UI**

---

**Happy Testing! 🚀** 