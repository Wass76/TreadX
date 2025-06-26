# Architectural Improvements Discussion

## Current Issue Analysis

The problem you encountered is a classic architectural design issue where:
- `GeographicalAuthorizationService` extends `BaseSecurityService`
- `LeadsService` tries to access `getCurrentUser()` through the geographical service
- This violates encapsulation and creates tight coupling

## Better Architectural Approaches

### **Option 1: Add Current User ID Method (Recommended) ✅**

**What I implemented:**
```java
// In GeographicalAuthorizationService
public Long getCurrentUserId() {
    return getCurrentUser().getId();
}

// In LeadsService - use this instead:
geographicalAuthService.getCurrentUserId()
```

**Benefits:**
- ✅ Maintains encapsulation
- ✅ Clean API design
- ✅ Single responsibility principle
- ✅ Easy to test and mock

### **Option 2: Composition Over Inheritance (Better Long-term)**

**Current Structure:**
```java
GeographicalAuthorizationService extends BaseSecurityService
```

**Better Structure:**
```java
@Service
public class GeographicalAuthorizationService {
    private final BaseSecurityService securityService;
    private final UserTerritoryRepository userTerritoryRepository;
    
    public GeographicalAuthorizationService(
            BaseSecurityService securityService,
            UserTerritoryRepository userTerritoryRepository) {
        this.securityService = securityService;
        this.userTerritoryRepository = userTerritoryRepository;
    }
    
    public Long getCurrentUserId() {
        return securityService.getCurrentUser().getId();
    }
    
    public boolean hasRole(String role) {
        return securityService.hasRole(role);
    }
}
```

**Benefits:**
- ✅ Better separation of concerns
- ✅ Easier to test (can mock BaseSecurityService)
- ✅ More flexible and maintainable
- ✅ Follows composition over inheritance principle

### **Option 3: Service Facade Pattern**

**Create a unified service facade:**
```java
@Service
public class BusinessAuthorizationService {
    private final BaseSecurityService securityService;
    private final GeographicalAuthorizationService geoAuthService;
    
    public boolean canAccessLead(Long leadId) {
        // Combine security and geographical logic
    }
    
    public List<Long> getAccessibleTerritories() {
        // Return accessible territories
    }
}
```

### **Option 4: Context Pattern**

**Create a user context:**
```java
@Component
public class UserContext {
    private final ThreadLocal<User> currentUser = new ThreadLocal<>();
    
    public User getCurrentUser() {
        return currentUser.get();
    }
    
    public Long getCurrentUserId() {
        return currentUser.get().getId();
    }
}
```

## Recommended Implementation Strategy

### **Phase 1: Quick Fix (Current)**
Use the `getCurrentUserId()` method I added to `GeographicalAuthorizationService`.

### **Phase 2: Refactor to Composition (Recommended)**
Refactor `GeographicalAuthorizationService` to use composition instead of inheritance.

### **Phase 3: Add Caching Layer**
```java
@Service
public class CachedGeographicalAuthorizationService {
    private final GeographicalAuthorizationService geoAuthService;
    private final RedisTemplate<String, Object> redisTemplate;
    
    @Cacheable("user-territories")
    public List<Long> getAccessibleCityIds() {
        return geoAuthService.getAccessibleCityIds();
    }
}
```

## Code Quality Improvements

### **1. Dependency Injection Best Practices**
```java
// Instead of @Autowired fields
@Service
public class LeadsService {
    private final LeadsRepository leadsRepository;
    private final GeographicalAuthorizationService geoAuthService;
    
    public LeadsService(LeadsRepository leadsRepository, 
                       GeographicalAuthorizationService geoAuthService) {
        this.leadsRepository = leadsRepository;
        this.geoAuthService = geoAuthService;
    }
}
```

### **2. Method Extraction for Readability**
```java
public Page<LeadsResponseDTO> getAllLeads(Pageable pageable) {
    List<Long> accessibleCityIds = geoAuthService.getAccessibleCityIds();
    
    if (accessibleCityIds.isEmpty()) {
        return getAllLeadsForAdmin(pageable);
    }
    
    return getFilteredLeads(accessibleCityIds, pageable);
}

private Page<LeadsResponseDTO> getAllLeadsForAdmin(Pageable pageable) {
    return leadsRepository.findAll(pageable)
            .map(leadsMapper::toResponse);
}

private Page<LeadsResponseDTO> getFilteredLeads(List<Long> cityIds, Pageable pageable) {
    if (geoAuthService.canOnlyAccessOwnLeads()) {
        return getOwnLeadsInCities(cityIds, pageable);
    }
    return getAllLeadsInCities(cityIds, pageable);
}
```

### **3. Exception Handling**
```java
public class GeographicalAccessException extends RuntimeException {
    public GeographicalAccessException(String message) {
        super(message);
    }
}

// In service
if (!geoAuthService.hasAccessToLocation(city, province, country)) {
    throw new GeographicalAccessException(
        String.format("No access to location: %s, %s, %s", 
                     city.getName(), province.getName(), country.getName()));
}
```

### **4. Configuration-Based Approach**
```java
@Configuration
public class GeographicalConfig {
    @Value("${app.geographical.cache-enabled:true}")
    private boolean cacheEnabled;
    
    @Value("${app.geographical.default-territory-level:CITY}")
    private TerritoryLevel defaultTerritoryLevel;
}
```

## Performance Considerations

### **1. Caching Strategy**
```java
@Cacheable(value = "user-territories", key = "#userId")
public List<UserTerritory> getUserTerritories(Long userId) {
    return userTerritoryRepository.findByUser_IdAndIsActiveTrue(userId);
}

@CacheEvict(value = "user-territories", key = "#userId")
public void updateUserTerritories(Long userId) {
    // Update logic
}
```

### **2. Database Query Optimization**
```java
// Use projections for better performance
@Query("SELECT ut.city.id FROM UserTerritory ut WHERE ut.user.id = :userId AND ut.isActive = true")
List<Long> findAccessibleCityIds(@Param("userId") Long userId);
```

### **3. Batch Operations**
```java
public List<LeadsResponseDTO> getLeadsForMultipleDealers(List<Long> dealerIds) {
    // Single query instead of multiple
    return leadsRepository.findByDealerIdInAndAddressCityIdIn(
        dealerIds, accessibleCityIds)
        .stream()
        .map(leadsMapper::toResponse)
        .collect(Collectors.toList());
}
```

## Testing Strategy

### **1. Unit Tests**
```java
@ExtendWith(MockitoExtension.class)
class LeadsServiceTest {
    @Mock private GeographicalAuthorizationService geoAuthService;
    @Mock private LeadsRepository leadsRepository;
    
    @Test
    void getAllLeads_WhenSalesAgent_ShouldReturnOwnLeadsOnly() {
        when(geoAuthService.getCurrentUserId()).thenReturn(1L);
        when(geoAuthService.canOnlyAccessOwnLeads()).thenReturn(true);
        when(geoAuthService.getAccessibleCityIds()).thenReturn(List.of(1L, 2L));
        
        // Test implementation
    }
}
```

### **2. Integration Tests**
```java
@SpringBootTest
@Transactional
class GeographicalAccessIntegrationTest {
    @Test
    void testSalesAgentTerritoryAccess() {
        // Test with real database
    }
}
```

## Summary of Recommendations

### **Immediate Actions:**
1. ✅ Use `getCurrentUserId()` method (already implemented)
2. ✅ Fix the compilation error in LeadsService

### **Short-term Improvements:**
1. Refactor to composition over inheritance
2. Add proper exception handling
3. Implement caching layer

### **Long-term Architecture:**
1. Consider service facade pattern
2. Add comprehensive testing
3. Implement performance monitoring
4. Add configuration management

## Benefits of These Improvements:

✅ **Maintainability**: Clean separation of concerns  
✅ **Testability**: Easy to mock and test components  
✅ **Scalability**: Efficient caching and query optimization  
✅ **Readability**: Clear method names and structure  
✅ **Flexibility**: Easy to extend and modify  
✅ **Performance**: Optimized database queries and caching  

The current approach with `getCurrentUserId()` is a good immediate solution, but I recommend moving toward the composition-based architecture for better long-term maintainability. 

## User Territory Assignment & Access Control (2024-Update)

### Overview
- Introduced unified, scalable logic for territory assignment and access control.
- Authorization is handled in the service layer, with controller-level role checks using @PreAuthorize.
- OpenAPI documentation is provided for all UserTerritoryController endpoints.

### Key Patterns
- **Service Layer Authorization:** All sensitive logic (who can assign/check/modify territories) is enforced in the service, not just the controller.
- **Role-Based Access:** PLATFORM_ADMIN and SALES_MANAGER have broad access, with fine-grained checks for managers (can only check/assign within their managed area).
- **Flexible Territory Model:** Supports city, province, and country levels, and can be extended for more.
- **OpenAPI/Swagger:** All endpoints are documented for clarity and maintainability.

### Flows
- **Assign Territory:** Only admins/managers can assign; service checks access to requested territory.
- **Check Access:** Uses a find-only approach to avoid side effects; checks if user has access to a location.
- **Check Assignments:** Admins can check any user; managers can check if the user is in their managed area; others can only check their own.

See also: `USER_TERRITORY_CREATION_GUIDE.md`, `UNIFIED_GEOGRAPHICAL_SERVICE_IMPLEMENTATION.md`. 

## System User Pattern (2024)

### Purpose
- The System User (ID 1) represents system-initiated actions (e.g., seeding, background jobs).
- It is not a real user and cannot log in.
- Used for audit fields (createdBy/modifiedBy) when the system performs an action.

### Implementation
- Seeded with ID 1, email system@treadx.com, isSystem=true, isActive=false.
- Login is disabled for this user in authentication logic.
- SuperAdmin is seeded as user #2 (ID 2), with full admin privileges.

### Why?
- Ensures a clear audit trail and prevents confusion between system and human actions.
- Only one system user exists; all system actions are attributed to it.

### Usage
- Use ID 1 for createdBy/modifiedBy when the system performs an action.
- Never assign a password or allow login for this user.
- Documented in onboarding and architecture docs. 