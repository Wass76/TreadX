# Architecture Review Report — TreadX

The codebase was reviewed against the defined rules. This report summarizes compliance and points to the detailed violations document for locations and suggested fixes.

---

## Adopted Rules

| # | Rule |
|---|------|
| 1 | The project must follow a **feature-based structure**. Each feature package (e.g. `dealerDealerCustomer`) must contain only the components for that feature: controller, service, repository, dto, mapper, entity, and enum (if needed). |
| 2 | The Controller must not contain any business logic; only calls to the Service and API definitions with Swagger. |

| 3 | Business logic lives in the Service; all database access goes through the repository. The Service must not use the Entity directly (no passing/returning Entity to upper layers; use DTO and Mapper). |
| 4 | In the Repository, prefer JPA (method names). Do not write SQL unless strictly necessary. |
| 5 | For each Entity that has input/output operations: at least one Request DTO and one Response DTO. For output-only: at least one Response DTO. |
| 6 | Each Mapper must provide at least: `toEntity` (for writing to the DB) and `toResponse` (for reading from the DB). |

---

## Compliance Summary by Rule

| Rule | Status | Brief note |
|------|--------|------------|
| **1** | Violations | Several packages mix multiple features; they do not follow a strict feature-based structure (one feature per package with only that feature’s components). |
| **2** | Minor violations | Some Controllers build Pageable/Sort internally; UserController imports Entity. |
| **3** | Violations | Most Services use Entity directly (read/save/update). |
| **4** | OK with notes | JPQL is used in several repositories; almost no native SQL. |
| **5** | Violations | Tire uses a single TireDTO; some entities lack separate Request/Response; AddressService returns Entity. |
| **6** | Violations | plans and tire have no Mapper; DealerCustomerPhoneService contains Entity→DTO conversion; LeadsMapper calls Repository. |

---

## Summary

- **Rule 1:** Adopt a feature-based structure: each feature (e.g. dealerDealerCustomer, vendor, lead) in its own package, containing only that feature’s controller, service, repository, dto, mapper, entity, and enum (if needed). See the violations document for current vs target package layout.
- **Rule 2:** Remove any logic from Controllers (e.g. building Pageable) and move it to the Service; remove Entity imports from the Controller.
- **Rule 3:** Ensure the Service does not expose Entity to upper layers; use Repository for persistence and DTO/Mapper for input and output.
- **Rule 4:** Keep using JPA and JPQL; avoid adding native SQL unless necessary.
- **Rule 5:** Add Request/Response DTOs for every Entity that has input/output operations; do not return Entity from the API (e.g. `getAllCountries()` in AddressService).
- **Rule 6:** Add Mappers for plans and tire with `toEntity` and `toResponse`; move Entity→DTO conversion from Service to Mapper; do not call Repository from inside a Mapper.

For exact file paths, line references, and suggested fixes, see **`ARCHITECTURE_VIOLATIONS_DETAIL.md`**.
