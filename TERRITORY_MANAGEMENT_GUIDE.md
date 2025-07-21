# 🗺️ Territory Management System Guide

## **Overview**

The Territory Management System provides a complete solution for managing multi-database territories with hierarchical relationships and dynamic database routing. This system allows you to:

- **Create and manage territories** with different levels (DISTRICT, CITY, PROVINCE, COUNTRY)
- **Configure database connections** for each territory
- **Establish hierarchical relationships** between territories
- **Route database connections dynamically** based on territory context
- **Manage user access** to different territories

---

## **🏗️ Architecture**

### **Territory Hierarchy**
```
COUNTRY (Canada)
└── PROVINCE (Ontario)
    └── CITY (London)
        ├── DISTRICT (N6B)
        ├── DISTRICT (N5V)
        └── DISTRICT (N7A)
```

### **Database Structure**
- **Each territory has its own database** with the same schema
- **Dynamic routing** based on territory context
- **Fallback to default database** if territory not found
- **Connection pooling** per territory

---

## **📋 API Endpoints**

### **Territory Management**

#### **Create Territory**
```http
POST /api/v1/territories
Authorization: Bearer {token}
Content-Type: application/json

{
  "code": "N8C",
  "name": "North 8C District",
  "level": "DISTRICT",
  "parentTerritoryCode": "LONDON",
  "databaseUrl": "jdbc:postgresql://localhost:5432/treadx_n8c",
  "databaseName": "treadx_n8c",
  "databaseUsername": "n8c_admin",
  "databasePassword": "password",
  "description": "North 8C district territory",
  "timezone": "America/Toronto",
  "currency": "CAD"
}
```

#### **Get Territory by Code**
```http
GET /api/v1/territories/{code}
Authorization: Bearer {token}
```

#### **Get Territory with Hierarchy**
```http
GET /api/v1/territories/{code}/hierarchy
Authorization: Bearer {token}
```

#### **Get All Territories**
```http
GET /api/v1/territories
Authorization: Bearer {token}
```

#### **Get Territories by Level**
```http
GET /api/v1/territories/level/{level}
Authorization: Bearer {token}
```

#### **Get Child Territories**
```http
GET /api/v1/territories/{code}/children
Authorization: Bearer {token}
```

#### **Update Territory**
```http
PUT /api/v1/territories/{code}
Authorization: Bearer {token}
Content-Type: application/json

{
  "name": "Updated Territory Name",
  "description": "Updated description"
}
```

#### **Activate Territory**
```http
POST /api/v1/territories/{code}/activate
Authorization: Bearer {token}
```

#### **Deactivate Territory**
```http
POST /api/v1/territories/{code}/deactivate
Authorization: Bearer {token}
```

### **Test Endpoints**

#### **Get Territory Context**
```http
GET /api/v1/test/territory/context
```

#### **Set Territory Context**
```http
POST /api/v1/test/territory/context/{territoryCode}
```

#### **Clear Territory Context**
```http
DELETE /api/v1/test/territory/context
```

---

## **🔧 Configuration**

### **Application Properties**
```yaml
# Dynamic DataSource Configuration
treadx:
  dynamic-datasource:
    enabled: true
    default-territory: N6B
```

### **Database Migration**
The system automatically creates the `territories` table with initial data:
- Canada (COUNTRY)
- Ontario (PROVINCE)
- London (CITY)
- N6B, N5V, N7A (DISTRICT)

---

## **🚀 Dynamic Database Routing**

### **How It Works**

1. **Request comes in** with territory context
2. **TerritoryContextFilter** determines territory code
3. **TerritoryContextHolder** stores territory code for thread
4. **DynamicDataSourceConfig** routes to correct database
5. **Request completes** and context is cleared

### **Territory Context Resolution Priority**

1. **Request Parameter**: `?territory=N6B`
2. **Request Header**: `X-Territory-Code: N6B`
3. **URL Path**: `/api/v1/leads/territories/N6B`
4. **User's Primary Territory**: Automatic routing

### **Excluded Paths**
The following paths don't require territory context:
- `/actuator/`
- `/v3/api-docs`
- `/swagger-ui/`
- `/api/v1/auth/`
- `/api/v1/territories/`
- `/api/v1/users/`
- `/api/v1/test/`

---

## **👥 User Access Control**

### **Role-Based Access**

#### **Platform Admin**
- ✅ Create, update, delete territories
- ✅ Activate/deactivate territories
- ✅ Access all territories
- ✅ Manage territory hierarchy

#### **Sales Manager**
- ✅ View territory information
- ✅ Access assigned territories
- ✅ View territory hierarchy
- ❌ Cannot modify territories

#### **Sales Agent**
- ✅ Access assigned district only
- ❌ Cannot view territory management

---

## **📊 Territory Levels**

### **DISTRICT Level**
- **Examples**: N6B, N5V, N7A
- **Parent**: Must have CITY parent
- **Purpose**: Sales operations, lead management
- **Database**: Contains district-specific data

### **CITY Level**
- **Examples**: London, Toronto
- **Parent**: Must have PROVINCE parent
- **Purpose**: City-wide management, cross-district operations
- **Database**: Contains city-level data and reports

### **PROVINCE Level**
- **Examples**: Ontario, Quebec
- **Parent**: Must have COUNTRY parent
- **Purpose**: Province-wide management, cross-city operations
- **Database**: Contains province-level data and reports

### **COUNTRY Level**
- **Examples**: Canada, USA
- **Parent**: No parent (top level)
- **Purpose**: Country-wide management, cross-province operations
- **Database**: Contains country-level data and reports

---

## **🔍 Testing the System**

### **1. Test Territory Context**
```bash
# Get current territory context
curl -X GET http://localhost:8080/api/v1/test/territory/context

# Set territory context
curl -X POST http://localhost:8080/api/v1/test/territory/context/N6B

# Clear territory context
curl -X DELETE http://localhost:8080/api/v1/test/territory/context
```

### **2. Test Territory Management**
```bash
# Get all territories
curl -X GET http://localhost:8080/api/v1/territories

# Get territories by level
curl -X GET http://localhost:8080/api/v1/territories/level/DISTRICT

# Get territory with hierarchy
curl -X GET http://localhost:8080/api/v1/territories/LONDON/hierarchy

# Get child territories
curl -X GET http://localhost:8080/api/v1/territories/LONDON/children
```

### **3. Test Dynamic Routing**
```bash
# Test with territory parameter
curl -X GET "http://localhost:8080/api/v1/leads/my-leads?territory=N6B" \
  -H "Authorization: Bearer {token}"

# Test with territory header
curl -X GET http://localhost:8080/api/v1/leads/my-leads \
  -H "Authorization: Bearer {token}" \
  -H "X-Territory-Code: N6B"

# Test with URL path
curl -X GET http://localhost:8080/api/v1/leads/territories/N6B \
  -H "Authorization: Bearer {token}"
```

---

## **⚠️ Important Notes**

### **Database Requirements**
- Each territory database must have the same schema
- Database must be accessible from the application
- Connection pool settings are configurable per territory

### **Security Considerations**
- Database passwords should be encrypted in production
- Territory access should be validated against user permissions
- Consider using environment variables for sensitive data

### **Performance Considerations**
- Connection pooling is configured per territory
- Consider caching territory configuration
- Monitor database connection usage

### **Error Handling**
- System falls back to default database if territory not found
- Invalid territory codes are logged and handled gracefully
- Database connection failures are handled with fallback

---

## **🔄 Next Steps**

### **Immediate**
1. **Test the territory management endpoints**
2. **Verify database routing works correctly**
3. **Test with different territory contexts**

### **Future Enhancements**
1. **Integrate with SecurityContextService** for automatic user territory resolution
2. **Add territory-specific configuration** (timezone, currency, etc.)
3. **Implement territory data synchronization**
4. **Add territory analytics and reporting**
5. **Create territory management UI**

---

## **📞 Support**

For questions or issues with the Territory Management System:
1. Check the logs for detailed error messages
2. Verify territory configuration in the database
3. Test with the provided test endpoints
4. Review the territory hierarchy relationships 