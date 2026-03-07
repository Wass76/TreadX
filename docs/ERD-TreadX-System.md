# TreadX System – Entity Relationship Diagram

This document describes the database schema and entity relationships for the TreadX application (tire sales, vendor/dealer portal, territories, and subscriptions). Table names and structure are aligned with the current codebase.

---

## Full ERD (Mermaid)

```mermaid
erDiagram
    users ||--o{ user_permissions : "has"
    users ||--o{ dealer_staff : "is"
    users ||--o{ leads_history : "validates or assigned"
    users }o--|| roles : "has role"
    roles ||--o{ role_permissions : "has"
    permissions ||--o{ role_permissions : "in"
    permissions ||--o{ user_permissions : "in"
    leads }o--o| file : "references"
    leads ||--o{ leads_history : "has"
    leads_history }o--o| users : "assigned to"
    leads_history }o--o| leads : "belongs to"
    dealer ||--o{ dealer_dealerDealerCustomer : "has"
    dealer ||--o{ dealer_staff : "employs"
    dealer ||--o{ leads : "dealer for"
    dealer ||--o{ tire_history : "dealer"
    dealer ||--o{ subscription : "subscribes"

    dealer_dealerDealerCustomer ||--o{ vehicle : "owns"
    dealer_dealerDealerCustomer ||--o{ tire : "owns"
    dealer_dealerDealerCustomer ||--o{ tire_history : "dealerDealerCustomer"

    vehicle ||--o{ tire : "has"

    plan ||--o{ subscription : "plan"

    address }o--|| system_city : "city"
    address }o--|| system_state : "province"
    address }o--|| system_country : "country"

    system_country }o--|| countries : "countryEntity"
    system_state }o--|| system_country : "in"
    system_state }o--|| states : "provinceEntity"
    system_city }o--|| system_state : "in"
    system_city }o--|| system_country : "in"
    system_city }o--|| cities : "cityEntity"

    regions ||--o{ subregions : "contains"
    regions ||--o{ countries : "contains"
    subregions ||--o{ countries : "contains"
    countries ||--o{ states : "contains"
    states ||--o{ cities : "contains"
    cities ||--o| system_city : "systemCity"

    users {
        bigint id PK
        string firstName
        string lastName
        string email UK
        string password
        string position
        bigint role_id FK
        boolean isSystem
        boolean isActive
        datetime createdAt
        datetime updatedAt
        bigint createdBy
        bigint lastModifiedBy
        boolean isSystemGenerated
    }

    roles {
        bigint id PK
        string name UK
        string description
        boolean isActive
        boolean isSystem
        boolean isSystemGenerated
    }

    permissions {
        bigint id PK
        string name UK
        string description
        string resource
        string action
        boolean isActive
    }

    role_permissions {
        bigint role_id PK,FK
        bigint permission_id PK,FK
    }

    user_permissions {
        bigint user_id PK,FK
        bigint permission_id PK,FK
    }

    dealer {
        bigint id PK
        string legalName
        string businessName
        string streetNumber
        string streetName
        string aptUnitBldg
        string postalCode
        string email
        string phoneNumber
        string dealerStatus
        string dealerUniqueId
        int totalUsers
        text userRolesConfig
    }

    dealer_staff {
        bigint id PK
        bigint user_id FK
        bigint dealer_id FK
        string districtCode
        string accessLevel
    }

    dealer_customer {
        bigint id PK
        string firstName
        string lastName
        string email
        string phoneNumber
        string streetNumber
        string streetName
        string aptUnitBldg
        string postalCode
        bigint dealer_id FK
        string dealer_dealerDealerCustomerUniqueId UK
    }

    vehicle {
        bigint id PK
        bigint dealer_dealerDealerCustomer_id FK
        string make
        string model
        int year
        string plateNumber
        string vin
        string color
        long odometerKm
    }

    tire {
        bigint id PK
        string tireType
        double treadWidth
        double aspectRatio
        string construction
        string composition
        double diameter
        double mileage
        string treadCondition
        string status
        datetime addedDate
        datetime updatedDate
        bigint dealer_dealerDealerCustomer_id FK
        bigint vehicle_id FK
        string tireUniqueId UK
        string brand
        string model
        string size
        string description
    }

    tire_history {
        bigint id PK
        bigint tire_id FK
        bigint dealer_dealerDealerCustomer_id FK
        bigint dealer_id FK
        int quantity
        double totalAmount
        string transactionType
        string status
        datetime transactionDate
        string paymentMethod
        string notes
    }

    leads {
        bigint id PK
        string businessName
        string phoneNumber
        string streetNumber
        string streetName
        string aptUnitBldg
        string postalCode
        string source
        string sourceUrl
        string uploadedFile
        bigint file_id FK
        string status
        string notes
        string contactMethod
        string contactMethodDetails
        string extensionNumber
        string contactName
        string position
        bigint dealer_id FK
        string dealerUniqueId
        datetime validatedAt
        boolean flag
      
    }

    leads_history {
        bigint id PK
        bigint lead_id FK
        bigint validated_by_id FK
        boolean addedByManager
        bigint assigned_to_id FK
        datetime assignedAt
    }

    file {
        bigint id PK
        string filePath
        string originalFileName
        string storedFileName
        bigint fileSize
        string mimeType
        datetime uploadedAt
    }

    address {
        bigint id PK
        string streetName
        string streetNumber
        string unitNumber
        bigint city_id FK
        bigint province_id FK
        bigint country_id FK
        string postalCode
        string specialInstructions
    }

    plan {
        bigint id PK
        string planName UK
        text description
        decimal price
        string billingCycle
        int maxUsers
        int maxTireStorage
        boolean isActive
        text features
    }

    subscription {
        bigint id PK
        bigint dealer_id FK
        bigint plan_id FK
        datetime startDate
        datetime endDate
        string status
        decimal amountPaid
        boolean autoRenew
        datetime cancellationDate
        string cancellationReason
    }

    regions {
        bigint id PK
        string name
        text translations
        short flag
        string wikiDataId
    }

    subregions {
        bigint id PK
        string name
        text translations
        bigint region_id FK
        short flag
        string wikiDataId
    }

    countries {
        bigint id PK
        string name UK
        string iso3
        string iso2
        string phonecode
         string capital
        string currency
        bigint region_id FK
        bigint subregion_id FK
    }

    states {
        bigint id PK
        string name
        bigint country_id FK
        string country_code
    }

    cities {
        bigint id PK
        string name UK
        bigint state_id FK
        bigint country_id FK
        string state_code
        string country_code
        decimal latitude
        decimal longitude
    }

    system_country {
        bigint id PK
        string countryUniqueId UK
        string country
        bigint country_id FK
    }

    system_state {
        bigint id PK
        string provinceUniqueId UK
        string province
        bigint system_country_id FK
        bigint province_id FK
    }

    system_city {
        bigint id PK
        string cityUniqueId UK
        string city
        bigint cityEntity_id FK
        bigint system_province_id FK
        bigint system_country_id FK
    }
```

---
