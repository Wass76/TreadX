# Simplified Geographical Access Control Flow

## Core System Architecture

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   USER          │    │  USER_TERRITORY  │    │  GEOGRAPHICAL   │
│                 │    │                  │    │  ENTITIES       │
│ • id            │◄──►│ • user_id        │◄──►│ • SystemCity    │
│ • email         │    │ • level          │    │ • SystemProvince│
│ • role_id       │    │ • city_id        │    │ • SystemCountry │
│ • position      │    │ • province_id    │    │                 │
│                 │    │ • country_id     │    │                 │
└─────────────────┘    │ • is_active      │    └─────────────────┘
                       └──────────────────┘
                                │
                                ▼
                       ┌──────────────────┐
                       │  BUSINESS DATA   │
                       │                  │
                       │ • Leads          │
                       │ • Dealers        │
                       │ • Contacts       │
                       │                  │
                       │ All linked to    │
                       │ Address entities │
                       └──────────────────┘
```

## Access Control Flow

```
1. USER LOGIN
   │
   ▼
2. SYSTEM CHECKS USER ROLE & TERRITORIES
   │
   ▼
3. APPLY GEOGRAPHICAL FILTERS
   │
   ├── SALES_AGENT: City-level access (own leads only)
   ├── SALES_MANAGER: City/Province/Country-level access (all leads)
   └── PLATFORM_ADMIN: Global access
   │
   ▼
4. RETURN FILTERED DATA
```

## Territory Assignment Examples

```
SALES_AGENT (John)
├── Territory 1: Los Angeles (City)
├── Territory 2: San Francisco (City)
└── Can only access leads they created in these cities

SALES_MANAGER (Sarah)
├── Territory 1: Los Angeles (City) - All leads in LA
├── Territory 2: California (Province) - All leads in CA
└── Can access ALL leads in assigned territories

PLATFORM_ADMIN (Mike)
└── No territory restrictions - global access
```

## Data Access Rules

```
┌─────────────────────────────────────────────────────────────────┐
│                    ACCESS CONTROL MATRIX                        │
├─────────────────────────────────────────────────────────────────┤
│ Role           │ Territory Level │ Data Access                  │
├─────────────────────────────────────────────────────────────────┤
│ SALES_AGENT    │ City            │ Own leads in assigned cities │
│ SALES_MANAGER  │ City/Province/  │ All leads in assigned        │
│                │ Country         │ territories                  │
│ PLATFORM_ADMIN │ Global          │ All data                     │
└─────────────────────────────────────────────────────────────────┘
```

## Implementation Benefits

✅ **Scalable**: Single database with proper indexing  
✅ **Flexible**: Users can have multiple territories  
✅ **Maintainable**: Centralized access control logic  
✅ **Performance**: Efficient queries with caching  
✅ **Future-proof**: Easy to extend for new requirements  

## Key Database Relationships

```
USERS (1) ──── (N) USER_TERRITORIES (N) ──── (1) GEOGRAPHICAL_ENTITIES
    │                                              │
    │                                              │
    └─── (1) ──── BUSINESS_ENTITIES (N) ──── (1) ADDRESS
```

## Performance Optimization

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   REDIS CACHE   │    │  DATABASE       │    │  APPLICATION    │
│                 │    │                 │    │                 │
│ • User          │◄──►│ • Composite     │◄──►│ • Service Layer │
│   Territories   │    │   Indexes       │    │ • Controllers   │
│ • Geographical  │    │ • Partitioning  │    │ • Filters       │
│   Hierarchies   │    │ • Optimized     │    │                 │
│ • Frequently    │    │   Queries       │    │                 │
│   Accessed Data │    │                 │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
``` 