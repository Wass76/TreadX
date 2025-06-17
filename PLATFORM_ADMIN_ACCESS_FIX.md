# Platform Admin Access Fix

## Issue
Platform admin users were getting 403 (Forbidden) errors when trying to access leads, dealers, and contacts endpoints, even though they should have full access to all operations.

## Root Cause
The `@PreAuthorize` annotations in the controller classes were only checking for `SALES_MANAGER` and `SALES_AGENT` roles, but not including `PLATFORM_ADMIN`. While the `AuthorizationService` correctly handled platform admin access in its methods, the Spring Security `@PreAuthorize` annotations were blocking access before the service methods could be called.

## Solution
Updated all `@PreAuthorize` annotations in the following controllers to include `PLATFORM_ADMIN` role:

### 1. LeadsController (`src/main/java/com/TreadX/dealers/controller/LeadsController.java`)
- **GET** `/api/v1/leads` - Get all leads
- **GET** `/api/v1/leads/{id}` - Get lead by ID  
- **POST** `/api/v1/leads` - Create new lead
- **PUT** `/api/v1/leads/{id}` - Update lead
- **DELETE** `/api/v1/leads/{id}` - Delete lead
- **POST** `/api/v1/leads/{id}/convert-to-contact` - Convert lead to contact

### 2. DealerController (`src/main/java/com/TreadX/dealers/controller/DealerController.java`)
- **GET** `/api/v1/dealers` - Get all dealers
- **GET** `/api/v1/dealers/{id}` - Get dealer by ID
- **POST** `/api/v1/dealers` - Create new dealer
- **PUT** `/api/v1/dealers/{id}` - Update dealer
- **DELETE** `/api/v1/dealers/{id}` - Delete dealer
- **GET** `/api/v1/dealers/search` - Search dealers

### 3. DealerContactController (`src/main/java/com/TreadX/dealers/controller/DealerContactController.java`)
- **GET** `/api/v1/contacts` - Get all contacts
- **GET** `/api/v1/contacts/{id}` - Get contact by ID
- **GET** `/api/v1/contacts/dealer/{dealerId}` - Get contacts by dealer
- **POST** `/api/v1/contacts` - Create new contact
- **PUT** `/api/v1/contacts/{id}` - Update contact
- **DELETE** `/api/v1/contacts/{id}` - Delete contact
- **POST** `/api/v1/contacts/{id}/convert-to-dealer` - Convert contact to dealer

### 4. AuthorizationService (`src/main/java/com/TreadX/user/service/AuthorizationService.java`)
- Updated `hasContactConversionAccess()` method to include `PLATFORM_ADMIN`
- Updated `hasDealerConversionAccess()` method to include `PLATFORM_ADMIN`

## Changes Made

### Before:
```java
@PreAuthorize("hasRole('SALES_MANAGER') or hasRole('SALES_AGENT')")
```

### After:
```java
@PreAuthorize("hasRole('PLATFORM_ADMIN') or hasRole('SALES_MANAGER') or hasRole('SALES_AGENT')")
```

## Access Control Matrix

| Role | Leads | Dealers | Contacts | Notes |
|------|-------|---------|----------|-------|
| PLATFORM_ADMIN | ✅ Full Access | ✅ Full Access | ✅ Full Access | Can perform all operations |
| SALES_MANAGER | ✅ Full Access | ✅ Full Access | ✅ Full Access | Can perform all operations |
| SALES_AGENT | ✅ Own Leads Only | ❌ No Access | ✅ Own Contacts Only | Limited access based on ownership |

## Testing
After these changes, platform admin users should be able to:
1. Create, read, update, and delete leads
2. Create, read, update, and delete dealers  
3. Create, read, update, and delete contacts
4. Convert leads to contacts
5. Convert contacts to dealers
6. Access all search and listing endpoints

## Notes
- The `AuthorizationService` methods already correctly handled platform admin access
- The issue was purely in the Spring Security `@PreAuthorize` annotations
- All API documentation has been updated to reflect the new role requirements
- The fix maintains backward compatibility for existing SALES_MANAGER and SALES_AGENT users 