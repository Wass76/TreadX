# Platform Admin Role Fix

## Issue
The platform admin user (admin@gmail.com) was incorrectly assigned role ID 3 (SALES_AGENT) instead of role ID 1 (PLATFORM_ADMIN) due to hardcoded role IDs in the TreadXApplication.

## Solution

### 1. Moved User Creation Logic
- **Before**: User creation logic was in `TreadXApplication.java` with hardcoded role IDs
- **After**: User creation logic moved to `SystemUserInitializer.java` with proper role name lookup

### 2. Fixed Role Assignment
- **Before**: `roleRepository.findById(3L)` (SALES_AGENT role)
- **After**: `roleRepository.findByName("PLATFORM_ADMIN")` (correct role lookup)

### 3. Added Role Constants
- Created `RoleConstants.java` to avoid hardcoding role names
- Updated both `SystemRolesInitializer` and `SystemUserInitializer` to use constants

### 4. Database Migration
- Created `V9__fix_platform_admin_role.sql` to fix existing users in the database

## Files Changed

### New Files
- `src/main/java/com/TreadX/user/config/SystemUserInitializer.java` - Handles user creation
- `src/main/java/com/TreadX/user/config/RoleConstants.java` - Role name constants
- `src/main/resources/sql/V9__fix_platform_admin_role.sql` - Database fix migration

### Modified Files
- `src/main/java/com/TreadX/TreadXApplication.java` - Removed user creation logic
- `src/main/java/com/TreadX/user/config/SystemRolesInitializer.java` - Updated to use constants

## How It Works

### Initialization Order
1. `SystemRolesInitializer` (@Order(1)) - Creates roles and permissions
2. `SystemUserInitializer` (@Order(2)) - Creates system users with correct roles

### Role Lookup
Instead of using hardcoded IDs:
```java
// OLD (incorrect)
.role(roleRepository.findById(3L).orElseThrow(...))

// NEW (correct)
.role(roleRepository.findByName(RoleConstants.PLATFORM_ADMIN).orElseThrow(...))
```

### Database Fix
The migration script fixes existing users:
```sql
UPDATE "user" 
SET role_id = (SELECT id FROM roles WHERE name = 'PLATFORM_ADMIN' LIMIT 1)
WHERE email = 'admin@gmail.com' 
AND role_id = (SELECT id FROM roles WHERE name = 'SALES_AGENT' LIMIT 1);
```

## Benefits

1. **Correct Role Assignment**: Platform admin users now have the correct PLATFORM_ADMIN role
2. **No Hardcoded IDs**: Uses role names instead of IDs for better maintainability
3. **Proper Separation**: User creation logic is separated from the main application class
4. **Database Fix**: Existing users are automatically fixed when the migration runs
5. **Constants**: Role names are centralized in constants to avoid typos

## Deployment

1. Deploy the updated code
2. The migration `V9__fix_platform_admin_role.sql` will automatically fix existing users
3. New users will be created with correct roles
4. Both admin users (wasee.tenbakji@gmail.com and admin@gmail.com) will have PLATFORM_ADMIN role

## Verification

After deployment, verify that:
- `admin@gmail.com` has PLATFORM_ADMIN role (not SALES_AGENT)
- `wasee.tenbakji@gmail.com` has PLATFORM_ADMIN role
- Both users can access platform admin features
- New users are created with correct roles 