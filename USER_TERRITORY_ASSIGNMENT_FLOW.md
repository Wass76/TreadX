# User Territory Assignment Flow

## 1. User Creation
- A new user is created via the API (e.g., `createUser` or `createUserWithTerritories`).
- The user is always created as a regular user (`isSystem = false`).
- The user's role and other details are set according to the request and business rules.

## 2. Assigning Territory to User
- An admin or sales manager (with proper access) initiates a territory assignment for the user.
- The request includes base entity IDs: `cityId`, `provinceId`, `countryId`, and the desired territory level.

## 3. Strict Base Entity Containment Checks
- **Before any mapping to system entities:**
  - If both `cityId` and `provinceId` are provided, the system checks that the city actually belongs to the specified province.
  - If both `provinceId` and `countryId` are provided, the system checks that the province actually belongs to the specified country.
  - If these relationships are not valid, the request is rejected.

## 4. Manager Containment and Access Checks
- For sales managers:
  - The system checks that the requested territory (city/province/country) is within the manager's managed scope.
  - The manager can only assign users to territories they themselves manage.
- For admins:
  - No such restriction; they can assign any territory.

## 5. System Entity Mapping and Assignment
- If all checks pass, the system maps the base entities to their corresponding system entities.
- The territory assignment is created and saved.

## 6. Restrictions Enforced
- **No cross-country/province/city assignments:**  
  You cannot assign a user to a city that is not in the specified province/country, or a province not in the specified country.
- **Managers cannot assign outside their scope:**  
  A manager can only assign users to territories they themselves manage.
- **All relationships are validated at both the base entity and system entity levels.**

---

This ensures data integrity and strict adherence to your geographical hierarchy and access control model. 