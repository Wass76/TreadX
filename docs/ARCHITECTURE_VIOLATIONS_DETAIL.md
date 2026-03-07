# Architecture Violations — Details and How to Fix Them

Each item states the file/layer, the type of violation, and how to fix it. No code was modified; this is for review only.

---

## 1. Rule: Feature-based structure

**Rule (exact):** The project must follow a feature-based structure. Each feature package (e.g. `dealerDealerCustomer`) should contain only the components related to that feature: **controller**, **service**, **repository**, **dto**, **mapper**, **entity**, and **enum** (if needed).

---

### 1.1 Current vs required structure

| Current package | What it contains | Violation |
|-----------------|------------------|-----------|
| `com.TreadX.district.vendors` | **DealerCustomer** + **DealerCustomerPhone** + **Vendor** + **Dealer** + **DealerEmployee** + **EnhancedVendor** (6 features in one package) | Multiple features; components of different features are mixed (e.g. one `dto/` holds DTOs for dealerDealerCustomer, vendor, dealer-employee). |
| `com.TreadX.district.sales` | **Leads** + **DealerContact** + **FileService** (2 features + shared utility) | Multiple features in one package. |
| `com.TreadX.user` | **User** + **Role** + **Permission** + **Territory** + **UserTerritory** + **VendorStaff** + auth flows (DealerAuth, VendorAuth) | Many features in one package; flat layout (controller/, service/, …) instead of one subpackage per feature. |
| `com.TreadX.address` | **Address** + **City** + **Country** + **State** + **Region** + **Subregion** + **SystemCity** + **SystemCountry** + **SystemProvince** | Multiple address/geography concepts in one package. |
| `com.TreadX.plans` | **SubscriptionPlan** + **VendorSubscription** | Two features in one package. |
| `com.TreadX.tire` | **Tire** + **Package** + **DealerPackages** + **TireTransaction** | Four features in one package. |

---

### 1.2 `district/vendors` — detailed breakdown

**Current:** One package with shared subpackages `controller/`, `service/`, `repository/`, `dto/`, `mapper/`, `entity/`, `enums/`.

**Features identified:**

| Feature | Controller | Service | Repository | Mapper | Entity | DTOs | Enum(s) |
|---------|------------|---------|------------|--------|--------|------|--------|
| **DealerCustomer** | DealerCustomerController, DealerCustomerPhoneController | DealerCustomerService, DealerCustomerPhoneService | DealerCustomerRepository, DealerCustomerPhoneRepository | DealerCustomerMapper | DealerCustomer, DealerCustomerPhone | DealerCustomerRequest/Response, DealerCustomerPhoneRequest/Response | PhoneType, PhoneStatus (for DealerCustomerPhone) |
| **Vendor** | VendorController, EnhancedVendorController | VendorService, EnhancedVendorService | VendorRepository | VendorMapper | Vendor | VendorRequest/Response, VendorCreationRequest/Response, InitiateContactRequest | VendorStatus, Channel, ContactMethod, ContactStatus, LeadSource |
| **DealerEmployee** | DealerController, DealerEmployeeController | DealerEmployeeService, ConversionService | DealerEmployeeRepository | (none – see Rule 6) | DealerEmployee | DealerEmployeeRequest/Response, DealerEmployeeAuthenticationResponse | — |

**Suggested target (feature-based):**

- `com.TreadX.district.vendors.dealerDealerCustomer`  
  - `controller/` (DealerCustomerController, DealerCustomerPhoneController)  
  - `service/` (DealerCustomerService, DealerCustomerPhoneService)  
  - `repository/` (DealerCustomerRepository, DealerCustomerPhoneRepository)  
  - `dto/` (DealerCustomer*, DealerCustomerPhone*)  
  - `mapper/` (DealerCustomerMapper)  
  - `entity/` (DealerCustomer, DealerCustomerPhone)  
  - `enums/` (PhoneType, PhoneStatus) if used only by this feature  

- `com.TreadX.district.vendors.vendor`  
  - Same pattern: controller, service, repository, dto, mapper, entity, enums (VendorStatus, Channel, etc. if vendor-only).  

- `com.TreadX.district.vendors.dealeremployee`  
  - Same pattern for Dealer, DealerEmployee, ConversionService; shared enums can stay in a common `enums` package or be duplicated per feature depending on team choice.

**Shared enums** (e.g. LeadSource, ContactMethod) used by more than one feature can live in a small shared package (e.g. `district.vendors.common.enums`) or be duplicated per feature.

---

### 1.3 `district/sales` — detailed breakdown

**Current:** One package for Leads, DealerContact, and FileService.

**Features identified:**

| Feature | Controller | Service | Repository | Mapper | Entity | DTOs |
|---------|------------|---------|------------|--------|--------|------|
| **Leads** | LeadsController | LeadsService | LeadsRepository | LeadsMapper | Leads | LeadsRequest/Response, LeadValidationRequest |
| **DealerContact** | DealerContactController | DealerContactService | DealerContactRepository | DealerContactMapper | DealerContact | DealerContactRequest/Response |
| **File** (utility) | — | FileService | — | — | — | — |

**Suggested target:**

- `com.TreadX.district.sales.leads` — controller, service, repository, dto, mapper, entity (only Leads-related).
- `com.TreadX.district.sales.dealercontact` — controller, service, repository, dto, mapper, entity (only DealerContact-related).
- `FileService` can stay under a shared/utility package (e.g. `district.sales.common` or `utils`) since it is not a standalone feature with entity/repository.

---

### 1.4 `user` — detailed breakdown

**Current:** One flat package with many entities and flows (User, Role, Permission, Territory, UserTerritory, VendorStaff, auth).

**Suggested target (feature-based):** One subpackage per feature, each with only that feature’s components:

- `com.TreadX.user.user` — UserController, UserService, UserRepository, UserMapper, User entity, user DTOs.
- `com.TreadX.user.role` — RoleController, RoleService, RoleRepository, RoleMapper, Role entity, role DTOs.
- `com.TreadX.user.permission` — PermissionController (if any), PermissionService, PermissionRepository, PermissionMapper, Permission entity, permission DTOs.
- `com.TreadX.user.territory` — TerritoryController, TerritoryService, TerritoryRepository, TerritoryMapper, Territory entity, territory DTOs.
- `com.TreadX.user.userterritory` — UserTerritoryController, UserTerritoryService, UserTerritoryRepository, UserTerritoryMapper, UserTerritory entity, DTOs.
- `com.TreadX.user.vendorstaff` — VendorStaff-related controller, service, repository, mapper, entity, DTOs.
- Auth flows (DealerAuthentication, VendorAuth, VendorPortal) can be separate features (e.g. `user.auth.dealer`, `user.auth.vendor`) or a single `user.auth` feature with multiple controllers, depending on how you want to slice them.

Each of these would have its own `controller/`, `service/`, `repository/`, `dto/`, `mapper/`, `entity/`, and `enum/` (if needed) **for that feature only**.

---

### 1.5 `address` — detailed breakdown

**Current:** One package with Address, City, Country, State, Region, Subregion, SystemCity, SystemCountry, SystemProvince.

**Options:**

- **Option A (single feature):** Treat the whole as one “address” feature: keep one package but ensure it has a single logical feature (addressing/geography) with one controller, one service (or a few cohesive services), repositories, mappers, entities, DTOs. Then the package “contains only components related to that feature” by definition.
- **Option B (split):** Split into features, e.g.:
  - `address.address` — Address entity, AddressController, AddressService, AddressRepository, AddressMapper, address DTOs.
  - `address.geography` or `address.reference` — Country, State, City, Region, Subregion (and optionally System*) as one reference-data feature with its own controller/service/repository/mapper/entity/dto.

Choose one and align the package layout so that each feature package contains only the components for that feature.

---

### 1.6 `plans` — detailed breakdown

**Current:** SubscriptionPlan and VendorSubscription in one package with shared controller/, service/, repository/, dto/, entity/.

**Suggested target:**

- `com.TreadX.plans.subscriptionplan` — SubscriptionPlanController, SubscriptionPlanService, SubscriptionPlanRepository, SubscriptionPlanMapper (to add), SubscriptionPlan entity, SubscriptionPlanRequest/Response DTOs.
- `com.TreadX.plans.vendorsubscription` — VendorSubscriptionController, VendorSubscriptionService, VendorSubscriptionRepository, VendorSubscriptionMapper (to add), VendorSubscription entity, VendorSubscriptionRequest/Response DTOs.

Each feature package then contains only the components for that feature.

---

### 1.7 `tire` — detailed breakdown

**Current:** Tire, Package, DealerPackages, TireTransaction in one package.

**Suggested target:**

- `com.TreadX.tire.tire` — TireController, TireService, TireRepository, TireMapper (to add), Tire entity, TireRequest/Response DTOs.
- `com.TreadX.tire.package` — PackageController (if any), PackageService, PackageRepository, PackageMapper (to add), Package entity, package DTOs.
- `com.TreadX.tire.dealerpackages` — DealerPackagesService, DealerPackagesRepository, DealerPackages entity, DTOs (and controller if exposed).
- `com.TreadX.tire.tiretransaction` — TireTransactionService, TireTransactionRepository, TireTransaction entity, DTOs (and controller/mapper if exposed).

Each feature package contains only the components related to that feature.

---

### 1.8 Summary: Rule 1 compliance

| Package | Action to comply |
|---------|------------------|
| `district.vendors` | Split into feature packages: `dealerDealerCustomer`, `vendor`, `dealeremployee` (and optionally a shared `enums` or common package). |
| `district.sales` | Split into `leads` and `dealercontact`; move FileService to a common/utility package. |
| `user` | Split into feature packages: `user`, `role`, `permission`, `territory`, `userterritory`, `vendorstaff`, and auth-related features. |
| `address` | Either keep as one “address” feature (Option A) or split into e.g. `address` and `geography`/`reference` (Option B). |
| `plans` | Split into `subscriptionplan` and `vendorsubscription`. |
| `tire` | Split into `tire`, `package`, `dealerpackages`, `tiretransaction`. |

After refactor, each feature package must contain **only**: controller, service, repository, dto, mapper, entity, and enum (if needed) for that feature.

---

## 2. Rule: Controller has no logic — only Service calls and Swagger

### 2.1 Building Pageable and Sort in the Controller

**Files:**
- `DealerCustomerController.java` — around lines 53–54, 67–68, 105–106: building `Sort.Direction` and `Pageable` from the request.
- `SubscriptionPlanController.java` — lines 92–94, 117–119: same pattern.

**Issue:** Building Pageable/Sort is considered logic and should live in a lower layer.

**Suggested fix:**
- Move creation of `Pageable` and `Sort` into the Service.
- Have the Controller pass only primitive parameters (page, size, sortBy, direction) and call e.g. `dealerDealerCustomerService.getDealerCustomersByVendor(vendorId, page, size, sortBy, direction)` where the Service builds the Pageable internally.

### 2.2 Importing and using Entity in the Controller

**File:** `user/controller/UserController.java`  
**Line:** import of `com.TreadX.user.entity.User` (line 4).

**Issue:** The Controller must not depend on Entity, even if unused in the body (unused or undesired import).

**Suggested fix:** Remove the `User` import from the Controller and rely only on DTOs and the Service.

### 2.3 Authorization logic in the Controller

**File:** `district/sales/controller/DealerContactController.java` (currently commented out)  
In the commented version: calling `authorizationService.hasAccessToDealerContact(id, "READ")` and then throwing `AccessDeniedException` in the Controller.

**Suggested fix:** When re-enabling the Controller, move the permission check into the Service so the Controller only calls e.g. `dealerContactService.getContactById(id)` and the Service performs the check and throws if needed.

---

## 3. Rule: Service — logic lives here; no direct use of Entity with DB

**Intent:** All database interaction must go through the Repository; data handling must use DTO and Mapper so Entity is not leaked as return types or public parameters. In practice, the Service may use Entity internally after loading from the Repository and then convert it to DTO via the Mapper.

The violations below are when Entity is returned to callers or used excessively in the Service (e.g. manually mutating fields instead of using the Mapper).

### 3.1 DealerCustomerService

**File:** `district/vendors/service/DealerCustomerService.java`

- Uses `Vendor`, `DealerCustomer`, and `DealerCustomerPhone` (entities) from the repository and mutates them (e.g. `dealerDealerCustomer.setVendor(vendor)`, `savedDealerCustomer.setDealerCustomerUniqueId(...)`, `dealerDealerCustomer.setPhoneNumbers(...)`).
- Returning DTO via `dealerDealerCustomerMapper.toResponse` is correct; the violation is heavy reliance on direct Entity mutation in the Service.

**Suggested fix:** Reduce direct Entity mutation: use the Mapper (e.g. `updateEntity`) for any field updates from the request; generate values (e.g. dealerDealerCustomerUniqueId) in the Service and pass them via the Mapper or a dedicated layer, and keep persistence only through the Repository.

### 3.2 DealerCustomerPhoneService

**File:** `district/vendors/service/DealerCustomerPhoneService.java`

- Methods accept and return `DealerCustomer` and `DealerCustomerPhone` (entities), e.g. `createPhoneNumbers(DealerCustomer dealerDealerCustomer, ...)`, `getPhoneNumbersByDealerCustomer(DealerCustomer dealerDealerCustomer)`, `updatePhoneNumbers(DealerCustomer dealerDealerCustomer, ...)`.
- Entity→DTO conversion lives in the Service: `toResponseDTO(DealerCustomerPhone)` and `toResponseDTOs(List<DealerCustomerPhone>)` (around lines 151–174).

**Suggested fix:**
- Make the Service’s public API use IDs and DTOs (e.g. accept `dealerDealerCustomerId` instead of `DealerCustomer` where appropriate).
- Move `toResponseDTO` and `toResponseDTOs` into `DealerCustomerPhoneMapper` and use them from the Service.

### 3.3 TireService

**File:** `tire/service/TireService.java`

- Creates and updates `Tire` entity manually (e.g. lines 21–27, 48–52) and saves via the Repository.
- Entity→DTO conversion in the Service: `convertToDTO(Tire)` (lines 67–76).

**Suggested fix:** Add `TireMapper` with `toEntity(TireRequestDTO)` and `toResponse(Tire)`, use it from the Service, and remove `convertToDTO` from the Service.

### 3.4 SubscriptionPlanService

**File:** `plans/service/SubscriptionPlanService.java`

- Builds `SubscriptionPlan` entity manually (e.g. 35–43) and updates it manually (76–83).
- Entity→Response DTO conversion in the Service: `convertToResponseDTO` (lines 115–129).

**Suggested fix:** Add `SubscriptionPlanMapper` with `toEntity(SubscriptionPlanRequestDTO)` and `toResponse(SubscriptionPlan)`, use it in the Service, and remove `convertToResponseDTO` from the Service.

### 3.5 VendorSubscriptionService

**File:** `plans/service/VendorSubscriptionService.java`

- Builds `VendorSubscription` manually and converts to DTO via `convertToResponseDTO` in the Service.

**Suggested fix:** Add a Mapper for VendorSubscription (e.g. under plans) with `toEntity` and `toResponse` and use it in the Service.

### 3.6 LeadsService

**File:** `district/sales/service/LeadsService.java`

- Uses `Leads`, `Vendor`, and `User` (entities) from repositories and mutates them (e.g. `leads.setVendor(vendor)`, `leads.setAddedByManager(...)`).
- Uses `EntityManager.createNativeQuery("SELECT current_database()")` (lines 99–104) for non–business logic (logging only).

**Suggested fix:** Keep mapping via LeadsMapper; reduce direct Entity mutation by using the Mapper. Move the current_database query to a Repository or helper layer if you want to keep it, or remove it from the Service if it is only for logging.

### 3.7 VendorService

**File:** `district/vendors/service/VendorService.java`

- Uses `Vendor` and `Leads` (entities) from the repository and mutates them (e.g. `vendor.setVendorUniqueId(...)`, `lead.setStatus(...)`).

**Suggested fix:** Keep persistence through the Repository; use the Mapper to update fields from the request; handle Lead status change via a dedicated Repository or Service method so Entity mutation is not overly exposed in the Service.

### 3.8 AddressService — returning Entity

**File:** `address/service/AddressService.java`  
**Method:** `getAllCountries()` (around line 57).

**Issue:** Return type is `List<SystemCountry>` (Entity).

**Suggested fix:** Create `SystemCountryResponseDTO` (or reuse an existing DTO) and return `List<SystemCountryResponseDTO>` after mapping, so Entity is not exposed to upper layers.

### 3.9 AddressService — manual mapping in the Service

**File:** `address/service/AddressService.java`

- Methods such as `getAllBaseCountries`, `getAllBaseStates`, `getAllBaseCities`, `getBaseCitiesByProvince`, `getBaseCitiesByCountry`, `getBaseProvincesByCountry` build Response DTOs manually from Entity inside the Service (e.g. 65–72, 79–87, 95–105).

**Suggested fix:** Add or complete Mappers (e.g. CountryMapper, StateMapper, CityMapper) with `toResponse` and use them from the Service instead of manual DTO construction.

---

## 4. Rule: Repository — prefer JPA; avoid SQL unless necessary

**Current state:** Most Repositories use JPQL in `@Query`, not native SQL. There are no clear violations for unnecessary native SQL.

**Notes:**
- `DealerCustomerRepository`: `existsDuplicateDealerCustomer` and `searchByVendorAndTerm` use JPQL — acceptable.
- `LeadsRepository`, `DealerContactRepository`, `VendorStaffRepository`, `TerritoryRepository`, `SubscriptionPlanRepository`, `VendorSubscriptionRepository`, `DealerCustomerPhoneRepository`: JPQL usage — acceptable.
- `LeadsService` uses `EntityManager.createNativeQuery("SELECT current_database()")` — this is in the Service, not the Repository; prefer moving it to a Repository or removing it if only used for logging (see 3.6).

**Suggested fix:** Keep the current approach; when you need queries that are too complex for JPA/JPQL, document the reason for using native SQL in comments.

---

## 5. Rule: For each Entity — Request DTO and Response DTO (or Response only for output)

### 5.1 Tire

**File:** `tire/dto/TireDTO.java`

**Issue:** Only one DTO (`TireDTO`) is used for both input and output. The rule requires at least separate Request and Response when there are both input and output operations.

**Suggested fix:** Add `TireRequestDTO` and `TireResponseDTO` (or at least `TireResponseDTO` if you keep the request as TireDTO) and use them in the API, Service, and Mapper.

### 5.2 Plans — SubscriptionPlan and VendorSubscription

**Status:** `SubscriptionPlanRequestDTO` / `SubscriptionPlanResponseDTO` and `VendorSubscriptionRequestDTO` / `VendorSubscriptionResponseDTO` exist. From a DTO perspective this is compliant. The violation here is the missing Mapper (Rule 6).

### 5.3 Address — getAllCountries returns Entity

**File:** `address/service/AddressService.java` — `getAllCountries()` returns `List<SystemCountry>`.

**Suggested fix:** As in 3.8: do not return Entity; use a Response DTO (e.g. `SystemCountryResponseDTO`) and map the list in the Service or via a Mapper.

### 5.4 Other (reference) entities

- In `address`: SystemCity, SystemProvince, etc. — if they are exposed via the API, each should have at least a Response DTO.

---

## 6. Rule: Mapper — at least toEntity and toResponse

### 6.1 No Mapper in plans

**Folder:** `plans/`

**Issue:** There is no `SubscriptionPlanMapper` or `VendorSubscriptionMapper`; mapping is done inside the Service.

**Suggested fix:**
- Add `SubscriptionPlanMapper` with `toEntity(SubscriptionPlanRequestDTO)` and `toResponse(SubscriptionPlan)` (and `updateEntity` if needed for updates).
- Add `VendorSubscriptionMapper` with `toEntity(VendorSubscriptionRequestDTO)` and `toResponse(VendorSubscription)` and use them in the Service.

### 6.2 No Mapper in tire

**Folder:** `tire/`

**Issue:** No `TireMapper`; Entity conversion is done inside `TireService`.

**Suggested fix:** Add `TireMapper` with `toEntity(TireRequestDTO)` and `toResponse(Tire)` and use it in `TireService` instead of `convertToDTO`.

### 6.3 DealerCustomerPhone — mapping in Service instead of Mapper

**File:** `district/vendors/service/DealerCustomerPhoneService.java`

**Issue:** `toResponseDTO(DealerCustomerPhone)` and `toResponseDTOs(List<DealerCustomerPhone>)` are defined in the Service.

**Suggested fix:** Create `DealerCustomerPhoneMapper` (or add these methods to `DealerCustomerMapper` if phone stays part of the same feature) with `toEntity(DealerCustomerPhoneRequestDTO)` and `toResponse(DealerCustomerPhone)` and move the conversion from the Service to the Mapper.

### 6.4 LeadsMapper — calling Repository

**File:** `district/sales/mapper/LeadsMapper.java`

**Issue:** The Mapper injects `UserRepository` and calls `userRepository.findById(userId)` inside `getAddedByName` (lines 187–198). The Mapper should only perform mapping, not load data from the database.

**Suggested fix:** Move the logic for "get user name from userId" into the Service (e.g. LeadsService or UserService) and pass the resolved name to the Mapper, or pass an object that contains the name in the DTO/context so the Mapper fills the Response from provided data without calling the Repository.

---

## Summary: Files to Update (for review)

| File / folder | Rule | Suggested action |
|---------------|------|-------------------|
| district/vendors, district/sales, user, address, plans, tire | 1 | Adopt feature-based structure: split each package so that each feature (e.g. dealerDealerCustomer, vendor, leads) has its own package containing only controller, service, repository, dto, mapper, entity, enum (if needed). See §1 above. |
| DealerCustomerController, SubscriptionPlanController | 2 | Move Pageable/Sort construction to Service |
| UserController | 2 | Remove Entity import |
| DealerCustomerService, DealerCustomerPhoneService, TireService, SubscriptionPlanService, VendorSubscriptionService, LeadsService, VendorService, AddressService | 3, 5, 6 | Reduce direct Entity use; use Mappers; do not return Entity; add DTOs where missing |
| plans/, tire/ | 6 | Add Mappers with toEntity and toResponse |
| DealerCustomerPhoneService | 6 | Move toResponseDTO into DealerCustomerPhoneMapper |
| LeadsMapper | 6 | Remove UserRepository usage; move name resolution to Service |
| address/AddressService | 3, 5 | Return DTO instead of SystemCountry; use Mappers for Country/State/City |
| tire/dto | 5 | Add TireRequestDTO and TireResponseDTO |

Review completed without applying any code changes.
