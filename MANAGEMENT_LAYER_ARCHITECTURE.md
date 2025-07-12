# Management Layer Architecture - Single System with Role-Based Access

## **🏗️ Management Layer: Single System, Multiple Roles**

### **The Reality: One Management Service, Multiple Access Levels**

Based on your requirements, we need **ONE Management Service** that handles different roles and access levels, NOT separate systems.

---

## **1. Management Service Architecture**

### **Single Management Service with Role-Based Access**

```
┌─────────────────────────────────────────────────────────────┐
│                    Management Service                       │
│                                                             │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐         │
│  │   Country   │ │   Province  │ │    City     │         │
│  │   Manager   │ │   Manager   │ │   Manager   │         │
│  │   Role      │ │   Role      │ │   Role      │         │
│  │             │ │             │ │             │         │
│  │ • All       │ │ • Province  │ │ • City      │         │
│  │   Access    │ │   Access    │ │   Access    │         │
│  │ • Strategic │ │ • District  │ │ • District  │         │
│  │   Reports   │ │   Reports   │ │   Reports   │         │
│  └─────────────┘ └─────────────┘ └─────────────┘         │
│                                                             │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │              Admin Role                                 │ │
│  │                                                         │ │
│  │ • System Administration                                │ │
│  │ • User Management                                      │ │
│  │ • Database Management                                  │ │
│  │ • Role Assignment                                      │ │
│  │ • System Configuration                                 │ │
│  └─────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

### **Role Hierarchy & Access Levels**

#### **1.1 Country Manager Role**
- **Access**: All provinces, cities, districts in the country
- **Capabilities**:
  - View all data across the country
  - Strategic reports and analytics
  - Country-level performance metrics
  - Add new provinces
  - Assign province managers
  - Country-level policies and standards
- **Example**: Country manager for Canada can see all provinces (Ontario, Quebec, etc.)

#### **1.2 Province Manager Role**
- **Access**: All cities and districts within their province
- **Capabilities**:
  - View all data in their province
  - Province-level reports and analytics
  - Add new cities within their province
  - Assign city managers
  - Province-level performance monitoring
  - Cross-city coordination
- **Example**: Ontario manager can see all cities (Toronto, London, Ottawa, etc.)

#### **1.3 City Manager Role**
- **Access**: All districts within their city
- **Capabilities**:
  - View all data in their city
  - City-level reports and analytics
  - Add new districts within their city
  - Assign district managers
  - City-level performance monitoring
  - Cross-district coordination
- **Example**: London manager can see all districts (N6B, N5V, N7A, etc.)

#### **1.4 Admin Role**
- **Access**: System administration across all levels
- **Capabilities**:
  - User management and role assignment
  - Database creation and management
  - System configuration
  - Security management
  - Performance monitoring
  - Backup and recovery
  - Add new provinces, cities, districts
  - Assign managers at any level

---

## **2. Database Architecture for Management Layer**

### **Single Management Database with Multi-Tenant Design**

```sql
-- Management Service Database Schema

-- 1. Geographic Hierarchy Table
CREATE TABLE geographic_hierarchy (
    id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL, -- COUNTRY, PROVINCE, CITY, DISTRICT
    parent_id VARCHAR(20) NULL,
    manager_id VARCHAR(50) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (parent_id) REFERENCES geographic_hierarchy(id),
    INDEX idx_type_parent (type, parent_id)
);

-- 2. User Roles Table
CREATE TABLE user_roles (
    user_id VARCHAR(50) PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    role_type VARCHAR(20) NOT NULL, -- ADMIN, COUNTRY_MANAGER, PROVINCE_MANAGER, CITY_MANAGER
    assigned_geography_id VARCHAR(20) NULL,
    permissions JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (assigned_geography_id) REFERENCES geographic_hierarchy(id)
);

-- 3. District Database Registry
CREATE TABLE district_databases (
    district_id VARCHAR(20) PRIMARY KEY,
    district_name VARCHAR(100) NOT NULL,
    city_id VARCHAR(20) NOT NULL,
    database_url VARCHAR(200) NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (city_id) REFERENCES geographic_hierarchy(id)
);
```

### **Example Data Structure**

```sql
-- Geographic Hierarchy Data
INSERT INTO geographic_hierarchy VALUES
('CANADA', 'Canada', 'COUNTRY', NULL, 'USER_001', '2024-01-01'),
('ONTARIO', 'Ontario', 'PROVINCE', 'CANADA', 'USER_002', '2024-01-01'),
('TORONTO', 'Toronto', 'CITY', 'ONTARIO', 'USER_003', '2024-01-01'),
('LONDON', 'London', 'CITY', 'ONTARIO', 'USER_004', '2024-01-01'),
('N6B', 'N6B District', 'DISTRICT', 'LONDON', 'USER_005', '2024-01-01'),
('N5V', 'N5V District', 'DISTRICT', 'LONDON', 'USER_006', '2024-01-01');

-- User Roles Data
INSERT INTO user_roles VALUES
('USER_001', 'country_manager', 'country@company.com', 'COUNTRY_MANAGER', 'CANADA', '{"can_add_provinces": true, "can_view_all": true}'),
('USER_002', 'ontario_manager', 'ontario@company.com', 'PROVINCE_MANAGER', 'ONTARIO', '{"can_add_cities": true, "can_view_province": true}'),
('USER_003', 'toronto_manager', 'toronto@company.com', 'CITY_MANAGER', 'TORONTO', '{"can_add_districts": true, "can_view_city": true}'),
('USER_004', 'london_manager', 'london@company.com', 'CITY_MANAGER', 'LONDON', '{"can_add_districts": true, "can_view_city": true}'),
('USER_005', 'admin_user', 'admin@company.com', 'ADMIN', NULL, '{"can_manage_all": true}');

-- District Databases
INSERT INTO district_databases VALUES
('N6B', 'N6B District', 'LONDON', 'jdbc:postgresql://n6b-server:5432/n6b_db', 'ACTIVE'),
('N5V', 'N5V District', 'LONDON', 'jdbc:postgresql://n5v-server:5432/n5v_db', 'ACTIVE');
```

---

## **3. Management Service Features**

### **3.1 Role-Based Access Control**

#### **Country Manager Access**
```java
// Country Manager can access all districts in the country
@GetMapping("/api/country/{countryId}/districts")
public List<DistrictData> getAllDistrictsInCountry(@PathVariable String countryId) {
    // Returns all districts under this country
    return managementService.getDistrictsByCountry(countryId);
}

@PostMapping("/api/country/{countryId}/provinces")
public ProvinceData addNewProvince(@PathVariable String countryId, @RequestBody ProvinceRequest request) {
    // Country manager can add new provinces
    return managementService.createProvince(countryId, request);
}
```

#### **Province Manager Access**
```java
// Province Manager can access all districts in their province
@GetMapping("/api/province/{provinceId}/districts")
public List<DistrictData> getAllDistrictsInProvince(@PathVariable String provinceId) {
    // Returns all districts under this province
    return managementService.getDistrictsByProvince(provinceId);
}

@PostMapping("/api/province/{provinceId}/cities")
public CityData addNewCity(@PathVariable String provinceId, @RequestBody CityRequest request) {
    // Province manager can add new cities
    return managementService.createCity(provinceId, request);
}
```

#### **City Manager Access**
```java
// City Manager can access all districts in their city
@GetMapping("/api/city/{cityId}/districts")
public List<DistrictData> getAllDistrictsInCity(@PathVariable String cityId) {
    // Returns all districts under this city
    return managementService.getDistrictsByCity(cityId);
}

@PostMapping("/api/city/{cityId}/districts")
public DistrictData addNewDistrict(@PathVariable String cityId, @RequestBody DistrictRequest request) {
    // City manager can add new districts
    return managementService.createDistrict(cityId, request);
}
```

### **3.2 Reporting and Analytics**

#### **Multi-Level Reporting**
```java
// Country-level reports
@GetMapping("/api/country/{countryId}/reports/revenue")
public CountryRevenueReport getCountryRevenueReport(@PathVariable String countryId) {
    // Aggregates data from all provinces in the country
    return managementService.generateCountryRevenueReport(countryId);
}

// Province-level reports
@GetMapping("/api/province/{provinceId}/reports/revenue")
public ProvinceRevenueReport getProvinceRevenueReport(@PathVariable String provinceId) {
    // Aggregates data from all cities in the province
    return managementService.generateProvinceRevenueReport(provinceId);
}

// City-level reports
@GetMapping("/api/city/{cityId}/reports/revenue")
public CityRevenueReport getCityRevenueReport(@PathVariable String cityId) {
    // Aggregates data from all districts in the city
    return managementService.generateCityRevenueReport(cityId);
}
```

### **3.3 Database Management**

#### **Dynamic Database Creation**
```java
// Admin can create new district databases
@PostMapping("/api/admin/districts")
public DistrictData createNewDistrict(@RequestBody CreateDistrictRequest request) {
    // Creates new district database and configures it
    return managementService.createDistrictDatabase(request);
}

// City manager can add new districts (with admin approval)
@PostMapping("/api/city/{cityId}/districts/request")
public DistrictRequestData requestNewDistrict(@PathVariable String cityId, @RequestBody DistrictRequest request) {
    // Creates request for new district (requires admin approval)
    return managementService.requestNewDistrict(cityId, request);
}
```

---

## **4. Integration with Modular Monolith**

### **Management Module in Main Application**

The Management Service is implemented as a **module within the main modular monolith application**:

```
com.treadx/
├── district/                    # District Module
├── storage/                     # Storage Module
├── management/                  # Management Module ← This one
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── entity/
│   └── config/
│       └── ManagementDataSourceConfig.java
├── user/                       # User Management Module
├── security/                   # Security & Auth Module
└── sync/                       # Data Sync Module
```

### **Cross-Module Communication**

```java
// Management Module Service
@Service
public class ManagementService {
    
    @Autowired
    private DistrictService districtService; // From District Module
    
    @Autowired
    private StorageService storageService; // From Storage Module
    
    public CountryReport generateCountryReport(String countryId) {
        // Get all districts in country
        List<String> districtIds = getDistrictIdsByCountry(countryId);
        
        // Aggregate data from multiple district databases
        List<DistrictData> districtDataList = new ArrayList<>();
        for (String districtId : districtIds) {
            DistrictData data = districtService.getDistrictData(districtId);
            districtDataList.add(data);
        }
        
        // Get storage analytics
        StorageAnalytics storageAnalytics = storageService.getStorageAnalytics();
        
        // Generate country report
        return new CountryReport(districtDataList, storageAnalytics);
    }
}
```

### **Security Integration**

```java
// Security configuration for management endpoints
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .cors().and()
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/api/v1/management/**").hasAnyRole("MANAGER", "ADMIN")
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
```

---

## **5. Key Benefits of Single Management Service**

✅ **Simplified Architecture**: One service instead of three
✅ **Unified Access Control**: Single authentication and authorization
✅ **Consistent Reporting**: Same reporting engine for all levels
✅ **Easier Maintenance**: One codebase to maintain
✅ **Role Flexibility**: Easy to add new roles or modify permissions
✅ **Cost Effective**: Fewer servers and infrastructure
✅ **Modular Design**: Can be extracted as separate service later
✅ **No API Gateway Overhead**: Direct Spring Boot security and routing

---

## **6. Migration Path**

### **Phase 1: Modular Monolith (Current)**
- Management Service as a module within main application
- Single deployment with all modules
- Shared libraries and utilities
- Built-in Spring Boot security

### **Phase 2: Service Extraction (Future)**
- Extract Management Module as separate service
- Independent deployment and scaling
- API-based communication with other services
- Add API Gateway for service-to-service communication

This approach gives you the same functionality with a much simpler and more maintainable architecture! 