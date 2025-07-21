# 🔧 Circular Dependency Fix Guide

## **Issue Summary**
The application has a circular dependency between:
- `DynamicDataSourceConfig` → `TerritoryService` → `TerritoryRepository` → DataSource

## **Solution Applied**

### **1. Created TerritoryDataSourceLookup**
- **Purpose**: JDBC-based lookup for territory database information
- **Avoids**: JPA repositories and services in DataSource configuration
- **Location**: `src/main/java/com/TreadX/config/TerritoryDataSourceLookup.java`

### **2. Updated DynamicDataSourceConfig**
- **Removed**: `TerritoryService` injection
- **Added**: `TerritoryDataSourceLookup` injection
- **Result**: No more circular dependency

### **3. Temporarily Disabled Dynamic DataSource**
- **Config**: `treadx.dynamic-datasource.enabled: false`
- **Reason**: To test territory management without dynamic routing

## **Testing Steps**

### **1. Start the Application**
```bash
mvn spring-boot:run
```

### **2. Test Territory Management (Basic)**
```bash
# Health check
curl -X GET http://localhost:8080/api/v1/test/territory-management/health

# Get all territories
curl -X GET http://localhost:8080/api/v1/test/territory-management/territories

# Get territories by level
curl -X GET http://localhost:8080/api/v1/test/territory-management/territories/level/DISTRICT

# Get territory by code
curl -X GET http://localhost:8080/api/v1/test/territory-management/territories/N6B
```

### **3. Test Territory Lookup**
```bash
# Get database info for territory
curl -X GET http://localhost:8080/api/v1/test/territory-management/lookup/N6B

# Check if territory is active
curl -X GET http://localhost:8080/api/v1/test/territory-management/lookup/N6B/active

# Get all active territory codes
curl -X GET http://localhost:8080/api/v1/test/territory-management/lookup/codes
```

## **Next Steps After Testing**

### **If Tests Pass:**
1. **Re-enable Dynamic DataSource**:
   ```yaml
   treadx:
     dynamic-datasource:
       enabled: true
   ```

2. **Test Dynamic Routing**:
   ```bash
   # Test with territory parameter
   curl -X GET "http://localhost:8080/api/v1/leads/my-leads?territory=N6B" \
     -H "Authorization: Bearer {token}"
   ```

### **If Tests Fail:**
1. **Check Lombok Configuration**: Ensure Lombok annotation processor is working
2. **Check Database Migration**: Ensure V13__create_territories.sql ran successfully
3. **Check Logs**: Look for specific error messages

## **Lombok Issues (If Any)**

If you see "cannot find symbol" errors for getter methods:

### **Option 1: Add Lombok to IDE**
- Install Lombok plugin in your IDE
- Enable annotation processing

### **Option 2: Manual Getters (Temporary)**
Add getter methods manually to Territory entity:

```java
public String getCode() { return code; }
public String getName() { return name; }
public TerritoryLevel getLevel() { return level; }
public String getParentTerritoryCode() { return parentTerritoryCode; }
public String getDatabaseUrl() { return databaseUrl; }
public String getDatabaseName() { return databaseName; }
public String getDatabaseUsername() { return databaseUsername; }
public String getDatabasePassword() { return databasePassword; }
public Boolean getIsActive() { return isActive; }
public String getDescription() { return description; }
public String getTimezone() { return timezone; }
public String getCurrency() { return currency; }
```

## **Expected Results**

### **Successful Test Output:**
```json
{
  "status": "OK",
  "message": "Territory management system is working",
  "timestamp": 1234567890
}
```

### **Territory List Output:**
```json
[
  {
    "id": 1,
    "code": "CANADA",
    "name": "Canada",
    "level": "COUNTRY",
    "databaseName": "treadx_canada",
    "isActive": true
  },
  {
    "id": 2,
    "code": "ONTARIO",
    "name": "Ontario",
    "level": "PROVINCE",
    "parentTerritoryCode": "CANADA",
    "databaseName": "treadx_ontario",
    "isActive": true
  }
]
```

## **Troubleshooting**

### **Common Issues:**

1. **"Table territories does not exist"**
   - Run database migrations: `mvn flyway:migrate`

2. **"No territory found"**
   - Check if V13__create_territories.sql ran successfully
   - Verify data in database: `SELECT * FROM territories;`

3. **"Circular dependency"**
   - Ensure DynamicDataSourceConfig is not injecting TerritoryService
   - Verify TerritoryDataSourceLookup is being used instead

4. **"Cannot find symbol"**
   - Check Lombok configuration
   - Rebuild project: `mvn clean compile`

## **Success Criteria**

✅ Application starts without circular dependency error  
✅ Territory management endpoints return data  
✅ Territory lookup endpoints work  
✅ Database migration V13 runs successfully  
✅ All test endpoints return expected responses  

---

**Once these tests pass, you can re-enable dynamic datasource and test the full territory routing system!** 🚀 