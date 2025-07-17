# Hierarchical Multi-Database Architecture with Modular Monolith

## **Overview**

This architecture creates a **modular monolith system** where each territory has its own database, with a single application managing all territories through role-based access control. The system supports **dynamic territory addition** where you can start with any number of territories and add new territories as needed.

```
┌─────────────────────────────────────────────────────────────┐
│                    Modular Monolith Application            │
│                                                             │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐         │
│  │   District  │ │   Storage   │ │ Management  │         │
│  │   Module    │ │   Module    │ │   Module    │         │
│  │             │ │             │ │             │         │
│  │ • N6B DB    │ │ • Storage   │ │ • User DB   │         │
│  │ • N5V DB    │ │   DB        │ │ • Geo DB    │         │
│  │ • N7A DB    │ │ • Location  │ │ • Reports   │         │
│  │ • etc...    │ │   DB        │ │ • Analytics │         │
│  └─────────────┘ └─────────────┘ └─────────────┘         │
│                                                             │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐         │
│  │     User    │ │   Security  │ │   Data      │         │
│  │ Management  │ │   & Auth    │ │   Sync      │         │
│  │   Module    │ │   Module    │ │   Module    │         │
│  │             │ │             │ │             │         │
│  │ • Auth      │ │ • JWT       │ │ • Cross-DB  │         │
│  │ • Roles     │ │ • Rate      │ │   Sync      │         │
│  │ • Sessions  │ │   Limiting  │ │ • Conflict  │         │
│  │             │ │ • CORS      │ │   Resolution│         │
│  └─────────────┘ └─────────────┘ └─────────────┘         │
└─────────────────────────────────────────────────────────────┘
```

## **1. Database Structure**

### **Each Territory Database Contains:**

#### **Sales Management System**
- **Users** (sales team, admin, sales manager, vendor staff)
- **Leads** (potential customers)
- **Vendors** (dealers - one per territory)

#### **Tire Management System**
- **Vendor Customers** (customers of vendors)
- **Vendor Employees** (staff working for vendors)
- **Vehicles** (belonging to customers)
- **Tires** (belonging to vehicles)
- **Tire Transactions** (tire operations like installation, replacement)

### **Example: N6B Database**
```sql
-- N6B Database contains:
├── users (N6B sales team, N6B vendors, N6B admin)
├── leads (N6B potential customers)
├── vendors (N6B dealers only)
├── vendor_customers (N6B vendor customers)
├── vendor_employees (N6B vendor staff)
├── vehicles (N6B customer vehicles)
├── tires (N6B customer tires)
└── tire_transactions (N6B tire operations)
```

## **2. Territory Levels**

### **District Level (N6B, N5V, N7A)**
- **Owns**: All business data for that district
- **Contains**: Users, leads, vendors, customers, vehicles, tires
- **Scope**: Single district operations

### **City Level (London, Toronto)**
- **Owns**: City-wide reporting and cross-district operations
- **Contains**: City vendors (branches), cross-district reports
- **Scope**: City management and coordination

### **Province Level (Ontario)**
- **Owns**: Province-wide reporting and cross-city operations
- **Contains**: Province vendors, cross-city reports
- **Scope**: Province management and coordination

## **3. User Access Control**

### **Vendor Staff Access Levels**
```java
public enum VendorAccessLevel {
    OWNER,      // Full access to vendor data
    MANAGER,    // Manage customers, employees, basic operations
    MECHANIC,   // Tire operations, vehicle management
    CASHIER,    // Customer transactions, basic customer data
    VIEWER      // Read-only access to vendor data
}
```

### **Vendor Staff Permissions**
```java
@Component
public class VendorAccessControl {
    
    public boolean canAccessVendorData(Long vendorId, String accessLevel, String operation) {
        User currentUser = getCurrentUser();
        
        // Check if user is vendor staff for this vendor
        if (!isVendorStaff(currentUser, vendorId)) {
            return false;
        }
        
        // Check access level permissions
        switch (accessLevel) {
            case "OWNER":
                return true; // Full access
                
            case "MANAGER":
                return Arrays.asList("READ", "WRITE_CUSTOMERS", "WRITE_EMPLOYEES", "WRITE_BASIC").contains(operation);
                
            case "MECHANIC":
                return Arrays.asList("READ", "WRITE_TIRES", "WRITE_VEHICLES", "WRITE_TRANSACTIONS").contains(operation);
                
            case "CASHIER":
                return Arrays.asList("READ", "WRITE_TRANSACTIONS", "READ_CUSTOMERS").contains(operation);
                
            case "VIEWER":
                return "READ".equals(operation);
                
            default:
                return false;
        }
    }
}
```

### **Sales Manager Access Control**
```java
@Component
public class SalesManagerAccessControl {
    
    public boolean canAccessTerritoryData(String userTerritoryLevel, String userTerritoryCode, 
                                        String targetTerritoryCode, String operation) {
        
        // Sales Manager can access data from child territories
        if (isChildTerritory(userTerritoryCode, targetTerritoryCode)) {
            return true;
        }
        
        // Sales Manager can access data from same level territories
        if (isSameLevelTerritory(userTerritoryCode, targetTerritoryCode)) {
            return true;
        }
        
        return false;
    }
    
    private boolean isChildTerritory(String parentTerritory, String childTerritory) {
        // Check if childTerritory is under parentTerritory in hierarchy
        // Example: London (parent) can access N6B, N5V, N7A (children)
        // Example: Ontario (parent) can access London, Toronto (children)
        return territoryHierarchyService.isChildOf(parentTerritory, childTerritory);
    }
    
    private boolean isSameLevelTerritory(String territory1, String territory2) {
        // Check if both territories are at same level
        return territoryHierarchyService.getLevel(territory1).equals(
               territoryHierarchyService.getLevel(territory2));
    }
}
```

### **User Access by Role**

#### **Sales Agent**
- **Access**: Only their assigned district database
- **Can Do**: Manage leads, customers, tires in their district
- **Cannot Do**: Access other districts

#### **Sales Manager**
- **Access**: All territories within their assigned level (city or province)
- **Can Do**: 
  - Manage sales across their assigned territory level
  - View territory reports
  - Access and manage lower-level data (districts within their city, cities within their province)
  - View hierarchical reports from all child territories
- **Cannot Do**: Access territories outside their assigned level

#### **Admin**
- **Access**: All territories and all levels
- **Can Do**: Everything across all territories
- **Cannot Do**: Nothing restricted

#### **Vendor Staff**
- **Access**: Only their vendor's district database
- **Can Do**: Manage their vendor's customers, vehicles, tires (based on access level)
- **Cannot Do**: Access other vendors or districts

## **4. Business Workflow**

### **Lead to Vendor Process**
```
1. Sales Agent creates lead in N6B database
2. Sales Agent contacts lead
3. Lead becomes vendor (vendor) in N6B database
4. Vendor gets customers in N6B database
5. Customers get vehicles in N6B database
6. Vehicles get tires in N6B database
7. Tire transactions tracked in N6B database
```

### **Cross-Territory Operations**
```
1. Vendor wants to expand to N5V district
2. Create new vendor record in N5V database
3. Link to London city-level vendor record
4. Set up vendor staff accounts in N5V
5. Vendor can now operate in both N6B and N5V
```

### **Sales Manager Operations**
```
1. City Sales Manager (London) can:
   - View all data from N6B, N5V, N7A districts
   - Manage sales across all London districts
   - Generate city-level reports

2. Province Sales Manager (Ontario) can:
   - View all data from London, Toronto cities
   - Manage sales across all Ontario cities
   - Generate province-level reports
   - Access district-level data through city hierarchy
```

## **5. Data Relationships**

### **Territory Isolation**
- **Each territory owns its data completely**
- **No shared data between territories**
- **Cross-territory operations via API calls**

### **Hierarchical Reporting**
- **London can see reports from N6B, N5V, N7A**
- **Ontario can see reports from London, Toronto**
- **Upper levels aggregate data from lower levels**
- **Sales Managers can access data from all child territories within their level**

### **Vendor Relationships**
```
N6B Database:
├── Vendor A (N6B branch)
└── Vendor B (N6B branch)

London Database:
├── Vendor A (London branch - manages N6B, N5V, N7A)
└── Vendor C (London branch)

Ontario Database:
├── Vendor A (Ontario branch - manages all London branches)
└── Vendor D (Ontario branch)
```

## **6. Simple Territory Management**

### **Territory Configuration Database**
```sql
-- Management database contains territory configurations
CREATE TABLE territories (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(10) UNIQUE NOT NULL, -- N6B, N5V, LONDON, ONTARIO
    name VARCHAR(100) NOT NULL,
    level VARCHAR(20) NOT NULL, -- DISTRICT, CITY, PROVINCE
    parent_territory_code VARCHAR(10) REFERENCES territories(code),
    database_url VARCHAR(255) NOT NULL,
    database_name VARCHAR(50) NOT NULL,
    database_username VARCHAR(50) NOT NULL,
    database_password VARCHAR(255) NOT NULL,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT REFERENCES users(id)
);

CREATE TABLE territory_relationships (
    id BIGSERIAL PRIMARY KEY,
    parent_territory_code VARCHAR(10) REFERENCES territories(code),
    child_territory_code VARCHAR(10) REFERENCES territories(code),
    relationship_type VARCHAR(20), -- MANAGES, REPORTS_TO
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### **Admin Territory Management Interface**
```java
@RestController
@RequestMapping("/api/v1/admin/territories")
@PreAuthorize("hasRole('ADMIN')")
public class TerritoryManagementController {
    
    @PostMapping("/add-district")
    public ResponseEntity<TerritoryResponseDTO> addDistrict(@RequestBody AddDistrictRequest request) {
        // 1. Validate district code (N6B, N5V, etc.)
        // 2. Create new database
        // 3. Run database migrations
        // 4. Create territory record
        // 5. Link to parent city
        return territoryService.addDistrict(request);
    }
    
    @GetMapping("/list")
    public ResponseEntity<List<TerritoryResponseDTO>> listTerritories() {
        return ResponseEntity.ok(territoryService.getAllTerritories());
    }
    
    @PostMapping("/{territoryCode}/activate")
    public ResponseEntity<Void> activateTerritory(@PathVariable String territoryCode) {
        territoryService.activateTerritory(territoryCode);
        return ResponseEntity.ok().build();
    }
}
```

## **7. System Deployment**

### **Single Application Deployment**
```
┌─────────────────────────────────────────────────────────────┐
│                    TreadX Application                      │
│                                                             │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐         │
│  │   District  │ │   Storage   │ │ Management  │         │
│  │   Module    │ │   Module    │ │   Module    │         │
│  │             │ │             │ │             │         │
│  │ • N6B DB    │ │ • Storage   │ │ • User DB   │         │
│  │ • N5V DB    │ │   DB        │ │ • Geo DB    │         │
│  │ • N7A DB    │ │ • Location  │ │ • Reports   │         │
│  │ • etc...    │ │   DB        │ │ • Analytics │         │
│  └─────────────┘ └─────────────┘ └─────────────┘         │
└─────────────────────────────────────────────────────────────┘
```

### **Dynamic Configuration Loading**
```java
@Configuration
public class DynamicTerritoryConfig {
    
    @Autowired
    private TerritoryRepository territoryRepository;
    
    @Bean
    @Primary
    public DataSource dataSource() {
        String territoryCode = System.getProperty("territory.code", "N6B");
        Territory territory = territoryRepository.findByCode(territoryCode)
            .orElseThrow(() -> new RuntimeException("Territory not found: " + territoryCode));
        
        return DataSourceBuilder.create()
            .url(territory.getDatabaseUrl())
            .username(territory.getDatabaseUsername())
            .password(territory.getDatabasePassword())
            .build();
    }
}
```

## **8. Key Benefits**

### **✅ Territory Isolation**
- Each district operates independently
- District failures don't affect others
- Independent scaling per territory

### **✅ Hierarchical Management**
- City can manage multiple districts
- Province can manage multiple cities
- Clear reporting hierarchy
- Sales Managers can access and manage data from all child territories within their assigned level

### **✅ Data Ownership**
- Each territory owns its data
- No data sharing conflicts
- Clear responsibility boundaries

### **✅ Scalability**
- Easy to add new territories
- Independent resource allocation
- Territory-specific optimization

### **✅ Simple Deployment**
- Single application to deploy
- Shared libraries and utilities
- Easier debugging and maintenance
- No API Gateway overhead

## **9. Implementation Steps**

### **Phase 1: Start with Initial Territories**
1. **Deploy modular monolith** with territory management
2. **Create initial territory databases** (any number)
3. **Configure territory connections**
4. **Test territory isolation**

### **Phase 2: Add New Districts**
1. **Admin uses API interface** to add new district
2. **System automatically creates database**
3. **System configures new territory**
4. **System links to parent city**

### **Phase 3: Add Cities and Provinces**
1. **Admin adds new city** (e.g., Toronto)
2. **System creates city database**
3. **System links to province**
4. **Admin can add districts to new city**

## **10. Example: Adding New District**

### **Admin Process:**
1. **Login to admin interface**
2. **Call API: POST /api/v1/admin/territories/add-district**
3. **Send request body:**
   ```json
   {
     "districtCode": "N7A",
     "districtName": "North 7A District",
     "parentCityCode": "LONDON"
   }
   ```

### **System Process:**
1. **Validates district code**
2. **Creates PostgreSQL database: treadx_n7a**
3. **Runs database migrations**
4. **Creates territory record**
5. **Links to London city**
6. **Returns success response**

### **Result:**
```
New Territory Added:
├── Database: treadx_n7a
├── System: TreadX Application (connects to new database)
├── Parent: London
└── Status: Active
```

This architecture provides **complete territory isolation** while maintaining **hierarchical management capabilities** and **clear data ownership** through a **single modular monolith application**. 