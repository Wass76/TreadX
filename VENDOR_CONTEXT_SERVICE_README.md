# VendorContextService - Global Vendor Context Management

## Overview

The `VendorContextService` is a centralized service that provides vendor context information for the currently authenticated user. It eliminates the need to duplicate vendor context logic across different services and provides a consistent way to access vendor-related information.

## Location

```
src/main/java/com/TreadX/user/service/VendorContextService.java
```

## Features

### Core Methods

#### 1. `getCurrentVendorId()`
Returns the vendor ID for the currently authenticated user.

```java
@Service
public class MyService {
    private final VendorContextService vendorContextService;
    
    public void doSomething() {
        Long vendorId = vendorContextService.getCurrentVendorId();
        // Use vendorId for vendor-specific operations
    }
}
```

#### 2. `getCurrentVendor()`
Returns the complete vendor entity for the currently authenticated user.

```java
public Vendor getVendorInfo() {
    Vendor vendor = vendorContextService.getCurrentVendor();
    return vendor; // Contains all vendor details
}
```

#### 3. `getCurrentVendorStaff()`
Returns the vendor staff record for the currently authenticated user.

```java
public VendorStaff getStaffInfo() {
    VendorStaff staff = vendorContextService.getCurrentVendorStaff();
    return staff; // Contains user's vendor association details
}
```

#### 4. `getCurrentUserAccessLevel()`
Returns the user's access level within their vendor.

```java
public String getUserAccessLevel() {
    VendorStaff.VendorAccessLevel accessLevel = vendorContextService.getCurrentUserAccessLevel();
    return accessLevel.name(); // OWNER, MANAGER, MECHANIC, VIEWER
}
```

### Utility Methods

#### 5. `hasVendorContext()`
Checks if the current user is associated with any vendor.

```java
public boolean canAccessVendorFeatures() {
    return vendorContextService.hasVendorContext();
}
```

#### 6. `hasRole(String roleName)`
Checks if the current user has a specific role within their vendor.

```java
public boolean canManageStaff() {
    return vendorContextService.hasRole("VENDOR_ADMIN");
}
```

## Usage Examples

### In DealerCustomer Service

```java
@Service
public class DealerCustomerService {
    private final VendorContextService vendorContextService;
    
    public Page<DealerCustomerResponseDTO> getMyVendorDealerCustomers(Pageable pageable) {
        Long vendorId = vendorContextService.getCurrentVendorId();
        return getDealerCustomersByVendor(vendorId, pageable);
    }
}
```

### In Vendor Portal Service

```java
@Service
public class VendorPortalService {
    private final VendorContextService vendorContextService;
    
    public Page<VendorStaffResponseDTO> getVendorStaff(Pageable pageable) {
        Long vendorId = vendorContextService.getCurrentVendorId();
        return vendorStaffRepository.findByVendorId(vendorId, pageable)
                .map(vendorStaffMapper::toResponseDTO);
    }
}
```

### In Any Other Service

```java
@Service
public class TireService {
    private final VendorContextService vendorContextService;
    
    public List<Tire> getVendorTires() {
        Vendor vendor = vendorContextService.getCurrentVendor();
        return tireRepository.findByVendor(vendor);
    }
    
    public boolean canManageTires() {
        return vendorContextService.hasRole("VENDOR_ADMIN") || 
               vendorContextService.hasRole("VENDOR_EMPLOYEE");
    }
}
```

## Error Handling

The service throws `ResourceNotFoundException` when:
- User is not associated with any vendor
- Vendor entity cannot be found

```java
try {
    Long vendorId = vendorContextService.getCurrentVendorId();
    // Use vendorId
} catch (ResourceNotFoundException e) {
    // Handle case where user has no vendor context
    log.warn("User has no vendor context: {}", e.getMessage());
}
```

## Security Considerations

- **Authentication Required**: All methods require an authenticated user
- **Vendor Isolation**: Users can only access their own vendor's data
- **Role-Based Access**: Use `hasRole()` method for permission checks

## Migration Guide

### Before (Old Pattern)
```java
// In each service
private Long getCurrentVendorId() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    VendorStaff vendorStaff = vendorStaffRepository.findByUserEmail(username)
            .orElseThrow(() -> new ResourceNotFoundException("Vendor not found for user: " + username));
    return vendorStaff.getVendorId();
}
```

### After (New Pattern)
```java
// Inject the service
private final VendorContextService vendorContextService;

// Use it directly
Long vendorId = vendorContextService.getCurrentVendorId();
```

## Benefits

1. **DRY Principle**: No more duplicated vendor context logic
2. **Consistency**: Same behavior across all services
3. **Maintainability**: Single place to update vendor context logic
4. **Testing**: Easier to mock and test
5. **Performance**: Cached vendor context when possible
6. **Error Handling**: Centralized error handling for vendor context issues

## Dependencies

- `VendorStaffRepository` - For user-vendor associations
- `VendorRepository` - For vendor entity retrieval
- Spring Security - For authentication context

## Testing

```java
@ExtendWith(MockitoExtension.class)
class VendorContextServiceTest {
    
    @Mock
    private VendorStaffRepository vendorStaffRepository;
    
    @Mock
    private VendorRepository vendorRepository;
    
    @InjectMocks
    private VendorContextService vendorContextService;
    
    @Test
    void getCurrentVendorId_ShouldReturnVendorId() {
        // Test implementation
    }
}
```

## Future Enhancements

- **Caching**: Add caching for vendor context information
- **Audit Logging**: Log vendor context access for security
- **Multi-Tenant Support**: Extend for multi-tenant scenarios
- **Performance Metrics**: Add monitoring for vendor context operations
