# Geographical Access Control System - Flow Diagrams & Relationships

## 1. Database Entity Relationship Diagram

```mermaid
erDiagram
    USERS {
        bigint id PK
        string email
        string password
        bigint role_id FK
        string position
        timestamp created_at
        timestamp updated_at
    }
    
    ROLES {
        bigint id PK
        string name
        string description
        boolean is_active
        boolean is_system
        timestamp created_at
        timestamp updated_at
    }
    
    USER_TERRITORIES {
        bigint id PK
        bigint user_id FK
        enum level
        bigint city_id FK
        bigint province_id FK
        bigint country_id FK
        boolean is_active
        timestamp created_at
        timestamp updated_at
    }
    
    SYSTEM_CITY {
        bigint id PK
        string city_unique_id
        string city
        bigint system_province_id FK
        bigint system_country_id FK
    }
    
    SYSTEM_PROVINCE {
        bigint id PK
        string province_unique_id
        string province
        bigint system_country_id FK
    }
    
    SYSTEM_COUNTRY {
        bigint id PK
        string country_unique_id
        string country
    }
    
    LEADS {
        bigint id PK
        string business_name
        string business_email
        string phone_number
        bigint address_id FK
        enum source
        enum status
        string notes
        bigint dealer_id FK
        bigint created_by FK
        timestamp created_at
        timestamp updated_at
    }
    
    DEALER {
        bigint id PK
        string name
        string email
        string phone
        bigint address_id FK
        enum status
        integer access_count
        string dealer_unique_id
        bigint created_by FK
        timestamp created_at
        timestamp updated_at
    }
    
    ADDRESS {
        bigint id PK
        string street_name
        string street_number
        string unit_number
        bigint city_id FK
        bigint province_id FK
        bigint country_id FK
        string postal_code
        string special_instructions
        timestamp created_at
        timestamp updated_at
    }
    
    DEALER_CONTACT {
        bigint id PK
        string name
        string email
        string phone
        enum status
        bigint dealer_id FK
        bigint created_by FK
        timestamp created_at
        timestamp updated_at
    }

    USERS ||--o{ USER_TERRITORIES : "has territories"
    USERS }o--|| ROLES : "has role"
    
    USER_TERRITORIES }o--|| SYSTEM_CITY : "assigned to city"
    USER_TERRITORIES }o--|| SYSTEM_PROVINCE : "assigned to province"
    USER_TERRITORIES }o--|| SYSTEM_COUNTRY : "assigned to country"
    
    SYSTEM_CITY }o--|| SYSTEM_PROVINCE : "belongs to"
    SYSTEM_PROVINCE }o--|| SYSTEM_COUNTRY : "belongs to"
    
    LEADS }o--|| ADDRESS : "has address"
    DEALER }o--|| ADDRESS : "has address"
    DEALER_CONTACT }o--|| DEALER : "belongs to"
    
    ADDRESS }o--|| SYSTEM_CITY : "located in"
    ADDRESS }o--|| SYSTEM_PROVINCE : "located in"
    ADDRESS }o--|| SYSTEM_COUNTRY : "located in"
    
    LEADS }o--|| DEALER : "associated with"
    LEADS }o--|| USERS : "created by"
    DEALER }o--|| USERS : "created by"
    DEALER_CONTACT }o--|| USERS : "created by"
```

## 2. User Territory Assignment Flow

```mermaid
flowchart TD
    A[Admin Login] --> B[Create New User]
    B --> C[Assign Role]
    C --> D{User Role?}
    
    D -->|SALES_AGENT| E[Assign City Territories Only]
    D -->|SALES_MANAGER| F[Assign City/Province/Country Territories]
    D -->|PLATFORM_ADMIN| G[No Territory Restrictions]
    
    E --> H[Select Cities from Dropdown]
    F --> I[Select Cities/Provinces/Countries from Dropdown]
    
    H --> J[Save User Territory Assignment]
    I --> J
    G --> K[User Created Successfully]
    J --> K
    
    K --> L[User Can Access System]
    L --> M[Geographical Access Control Active]
```

## 3. Lead Creation Authorization Flow

```mermaid
flowchart TD
    A[User Login] --> B[Navigate to Create Lead]
    B --> C[Fill Lead Information]
    C --> D[Select Address Location]
    D --> E[Submit Lead Creation]
    
    E --> F[System Validates Access]
    F --> G{Check User Role & Territories}
    
    G --> H{User has access to location?}
    H -->|Yes| I[Create Lead Successfully]
    H -->|No| J[Return Access Denied Error]
    
    I --> K[Lead Associated with User]
    J --> L[Display Error Message]
    
    K --> M[Lead Available in User's Dashboard]
    L --> N[User Must Select Different Location]
```

## 4. Data Access Control Flow

```mermaid
flowchart TD
    A[User Requests Data] --> B[System Identifies User]
    B --> C[Check User Role]
    
    C --> D{User Role?}
    D -->|PLATFORM_ADMIN| E[Return All Data]
    D -->|SALES_MANAGER| F[Apply Territory Filters - All Leads in Territory]
    D -->|SALES_AGENT| G[Apply Territory Filters - Only Own Leads]
    
    F --> H[Query All Data for Assigned Territories]
    G --> I[Query Own Data for Assigned Cities]
    
    H --> J[Return All Results in Territory]
    I --> J
    E --> K[Return All Results]
    
    J --> L[Display Results to User]
    K --> L
```

## 5. Territory Hierarchy Structure

```mermaid
graph TD
    A[Country Level] --> B[Province Level]
    B --> C[City Level]
    
    A1[United States] --> B1[California]
    A1 --> B2[Texas]
    A1 --> B3[New York]
    
    B1 --> C1[Los Angeles]
    B1 --> C2[San Francisco]
    B1 --> C3[San Diego]
    
    B2 --> C4[Houston]
    B2 --> C5[Dallas]
    B2 --> C6[Austin]
    
    B3 --> C7[New York City]
    B3 --> C8[Buffalo]
    B3 --> C9[Rochester]
    
    style A fill:#e1f5fe
    style B fill:#f3e5f5
    style C fill:#e8f5e8
```

## 6. User Territory Assignment Examples

```mermaid
graph LR
    subgraph "User Territory Assignments"
        U1[Sales Agent John]
        U2[Sales Manager Sarah]
        U3[Sales Manager Mike]
        U4[Platform Admin Admin]
        
        T1[City: Los Angeles]
        T2[City: San Francisco]
        T3[City: Houston]
        T4[Province: California]
        T5[Country: United States]
        
        U1 --> T1
        U1 --> T2
        U2 --> T3
        U3 --> T4
        U4 --> T5
    end
    
    subgraph "Access Levels"
        L1[City Level - Own Leads Only]
        L2[City Level - All Leads]
        L3[Province Level - All Leads]
        L4[Country Level - All Leads]
        L5[Global Access]
        
        T1 --> L1
        T2 --> L1
        T3 --> L2
        T4 --> L3
        T5 --> L4
        U4 --> L5
    end
```

## 7. API Request Flow with Authorization

```mermaid
sequenceDiagram
    participant Client
    participant AuthFilter
    participant AuthService
    participant TerritoryService
    participant LeadsService
    participant Database
    
    Client->>AuthFilter: POST /api/leads
    AuthFilter->>AuthService: Validate JWT Token
    AuthService->>AuthFilter: User authenticated
    
    AuthFilter->>TerritoryService: Check geographical access
    TerritoryService->>Database: Query user territories
    Database->>TerritoryService: Return territories
    
    TerritoryService->>AuthFilter: Access granted/denied
    
    alt Access Granted
        AuthFilter->>LeadsService: Process request
        LeadsService->>Database: Create lead
        Database->>LeadsService: Lead created
        LeadsService->>Client: Success response
    else Access Denied
        AuthFilter->>Client: 403 Forbidden
    end
```

## 8. Performance Optimization Strategy

```mermaid
graph TD
    A[User Request] --> B{Cache Hit?}
    
    B -->|Yes| C[Return Cached Data]
    B -->|No| D[Query Database]
    
    D --> E[Apply Geographical Filters]
    E --> F[Execute Query with Indexes]
    F --> G[Cache Results]
    G --> H[Return Data]
    
    C --> I[Response to User]
    H --> I
    
    subgraph "Caching Strategy"
        J[Redis Cache]
        K[User Territories]
        L[Geographical Hierarchies]
        M[Frequently Accessed Data]
    end
    
    subgraph "Database Optimization"
        N[Composite Indexes]
        O[Geographical Partitioning]
        P[Query Optimization]
    end
```

## 9. Implementation Phases Timeline

```mermaid
gantt
    title Geographical Access Control Implementation Timeline
    dateFormat  YYYY-MM-DD
    section Phase 1
    Database Schema Changes    :2024-01-01, 7d
    Entity Creation           :2024-01-08, 5d
    Basic Authorization       :2024-01-13, 7d
    
    section Phase 2
    Service Layer Updates     :2024-01-20, 10d
    Controller Modifications  :2024-01-30, 5d
    API Testing              :2024-02-04, 7d
    
    section Phase 3
    Caching Implementation    :2024-02-11, 7d
    Performance Optimization  :2024-02-18, 5d
    Load Testing             :2024-02-23, 5d
    
    section Phase 4
    UI Updates               :2024-02-28, 10d
    User Training            :2024-03-10, 5d
    Production Deployment    :2024-03-15, 3d
```

## 10. Data Flow Summary

### Key Relationships:
1. **User → Territories**: One user can have multiple territory assignments
2. **Territories → Geographical Entities**: Each territory links to city/province/country
3. **Geographical Hierarchy**: Country → Province → City (parent-child relationship)
4. **Data Access**: All business entities (leads, dealers, contacts) inherit geographical access from their addresses

### Access Control Rules:
1. **SALES_AGENT**: Can only access leads they created within assigned cities
2. **SALES_MANAGER**: Can access ALL leads within assigned territories (city/province/country)
3. **PLATFORM_ADMIN**: Has access to all data globally
4. **Territory Inheritance**: Higher-level territories include all lower-level territories within them

### Performance Considerations:
1. **Indexing**: Composite indexes on geographical columns
2. **Caching**: User territories and geographical hierarchies cached in Redis
3. **Query Optimization**: Efficient joins and filtering strategies
4. **Partitioning**: Optional database partitioning for very large datasets 