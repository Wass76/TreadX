# 📋 Manual Testing Checklist

## **Prerequisites**
- [ ] Application is running on `http://localhost:8080`
- [ ] Database migrations have been executed
- [ ] Test data has been inserted
- [ ] Postman or similar API testing tool is ready

## **Phase 1: Basic Setup Verification**

### **1.1 Application Health**
- [ ] **Endpoint**: `GET /actuator/health`
- [ ] **Expected**: `{"status":"UP"}`
- [ ] **Status**: ⬜ Pass / ⬜ Fail

### **1.2 Database Connection**
- [ ] **Endpoint**: `GET /api/v1/users` (if exists)
- [ ] **Expected**: List of users or proper error
- [ ] **Status**: ⬜ Pass / ⬜ Fail

## **Phase 2: Authentication Testing**

### **2.1 Platform Admin Login**
- [ ] **Endpoint**: `POST /api/v1/auth/login`
- [ ] **Body**: `{"email":"admin@treadx.com","password":"password"}`
- [ ] **Expected**: JWT token in response
- [ ] **Status**: ⬜ Pass / ⬜ Fail
- [ ] **Token**: `_________________`

### **2.2 Sales Manager Login**
- [ ] **Endpoint**: `POST /api/v1/auth/login`
- [ ] **Body**: `{"email":"manager@treadx.com","password":"password"}`
- [ ] **Expected**: JWT token in response
- [ ] **Status**: ⬜ Pass / ⬜ Fail
- [ ] **Token**: `_________________`

### **2.3 Sales Agent Login**
- [ ] **Endpoint**: `POST /api/v1/auth/login`
- [ ] **Body**: `{"email":"agent@treadx.com","password":"password"}`
- [ ] **Expected**: JWT token in response
- [ ] **Status**: ⬜ Pass / ⬜ Fail
- [ ] **Token**: `_________________`

## **Phase 3: Security Context Testing**

### **3.1 Get Current User Info (Admin)**
- [ ] **Endpoint**: `GET /api/v1/test/current-user`
- [ ] **Headers**: `Authorization: Bearer {adminToken}`
- [ ] **Expected**: User info with accessible territories
- [ ] **Status**: ⬜ Pass / ⬜ Fail
- [ ] **Notes**: `_________________`

### **3.2 Get Current User Info (Manager)**
- [ ] **Endpoint**: `GET /api/v1/test/current-user`
- [ ] **Headers**: `Authorization: Bearer {managerToken}`
- [ ] **Expected**: User info with N6B territory only
- [ ] **Status**: ⬜ Pass / ⬜ Fail
- [ ] **Notes**: `_________________`

### **3.3 Get Current User Info (Agent)**
- [ ] **Endpoint**: `GET /api/v1/test/current-user`
- [ ] **Headers**: `Authorization: Bearer {agentToken}`
- [ ] **Expected**: User info with N6B territory only
- [ ] **Status**: ⬜ Pass / ⬜ Fail
- [ ] **Notes**: `_________________`

### **3.4 Test Territory Access - N6B (Admin)**
- [ ] **Endpoint**: `GET /api/v1/test/territory-access/N6B`
- [ ] **Headers**: `Authorization: Bearer {adminToken}`
- [ ] **Expected**: `{"canAccess": true}`
- [ ] **Status**: ⬜ Pass / ⬜ Fail

### **3.5 Test Territory Access - N5V (Manager)**
- [ ] **Endpoint**: `GET /api/v1/test/territory-access/N5V`
- [ ] **Headers**: `Authorization: Bearer {managerToken}`
- [ ] **Expected**: `{"canAccess": false}`
- [ ] **Status**: ⬜ Pass / ⬜ Fail

### **3.6 Get Primary Territory (Manager)**
- [ ] **Endpoint**: `GET /api/v1/test/primary-territory`
- [ ] **Headers**: `Authorization: Bearer {managerToken}`
- [ ] **Expected**: `{"primaryTerritory": "N6B"}`
- [ ] **Status**: ⬜ Pass / ⬜ Fail

### **3.7 Get Primary Territory (Admin)**
- [ ] **Endpoint**: `GET /api/v1/test/primary-territory`
- [ ] **Headers**: `Authorization: Bearer {adminToken}`
- [ ] **Expected**: Error about multiple territories
- [ ] **Status**: ⬜ Pass / ⬜ Fail

## **Phase 4: Leads API Testing**

### **4.1 Get My Leads (Automatic) - Admin**
- [ ] **Endpoint**: `GET /api/v1/leads/my-leads`
- [ ] **Headers**: `Authorization: Bearer {adminToken}`
- [ ] **Expected**: List of leads from all territories
- [ ] **Status**: ⬜ Pass / ⬜ Fail
- [ ] **Count**: `____` leads returned

### **4.2 Get My Leads (Automatic) - Manager**
- [ ] **Endpoint**: `GET /api/v1/leads/my-leads`
- [ ] **Headers**: `Authorization: Bearer {managerToken}`
- [ ] **Expected**: List of leads from N6B only
- [ ] **Status**: ⬜ Pass / ⬜ Fail
- [ ] **Count**: `____` leads returned

### **4.3 Get My Leads (Automatic) - Agent**
- [ ] **Endpoint**: `GET /api/v1/leads/my-leads`
- [ ] **Headers**: `Authorization: Bearer {agentToken}`
- [ ] **Expected**: List of leads from N6B only
- [ ] **Status**: ⬜ Pass / ⬜ Fail
- [ ] **Count**: `____` leads returned

### **4.4 Get Leads by Territory (Explicit) - N6B**
- [ ] **Endpoint**: `GET /api/v1/leads/territories/N6B`
- [ ] **Headers**: `Authorization: Bearer {adminToken}`
- [ ] **Expected**: Paginated leads from N6B
- [ ] **Status**: ⬜ Pass / ⬜ Fail

### **4.5 Get Leads by Territory (Explicit) - N5V**
- [ ] **Endpoint**: `GET /api/v1/leads/territories/N5V`
- [ ] **Headers**: `Authorization: Bearer {adminToken}`
- [ ] **Expected**: Paginated leads from N5V
- [ ] **Status**: ⬜ Pass / ⬜ Fail

### **4.6 Get Leads by Territory (Unauthorized) - N5V**
- [ ] **Endpoint**: `GET /api/v1/leads/territories/N5V`
- [ ] **Headers**: `Authorization: Bearer {managerToken}`
- [ ] **Expected**: 403 Forbidden
- [ ] **Status**: ⬜ Pass / ⬜ Fail

### **4.7 Get My Leads by Status (Automatic) - PENDING**
- [ ] **Endpoint**: `GET /api/v1/leads/my-leads/status?status=PENDING`
- [ ] **Headers**: `Authorization: Bearer {adminToken}`
- [ ] **Expected**: Only PENDING leads
- [ ] **Status**: ⬜ Pass / ⬜ Fail
- [ ] **Count**: `____` PENDING leads

### **4.8 Get Leads by Territory and Status (Explicit) - N6B PENDING**
- [ ] **Endpoint**: `GET /api/v1/leads/territories/N6B/status?status=PENDING`
- [ ] **Headers**: `Authorization: Bearer {adminToken}`
- [ ] **Expected**: PENDING leads from N6B only
- [ ] **Status**: ⬜ Pass / ⬜ Fail
- [ ] **Count**: `____` PENDING leads from N6B

## **Phase 5: Error Handling Testing**

### **5.1 Invalid Token**
- [ ] **Endpoint**: `GET /api/v1/leads/my-leads`
- [ ] **Headers**: `Authorization: Bearer invalid_token`
- [ ] **Expected**: 401 Unauthorized
- [ ] **Status**: ⬜ Pass / ⬜ Fail

### **5.2 No Token**
- [ ] **Endpoint**: `GET /api/v1/leads/my-leads`
- [ ] **Headers**: None
- [ ] **Expected**: 401 Unauthorized
- [ ] **Status**: ⬜ Pass / ⬜ Fail

### **5.3 Invalid Territory Code**
- [ ] **Endpoint**: `GET /api/v1/leads/territories/INVALID`
- [ ] **Headers**: `Authorization: Bearer {adminToken}`
- [ ] **Expected**: 403 Forbidden
- [ ] **Status**: ⬜ Pass / ⬜ Fail

## **Phase 6: Performance Testing**

### **6.1 Response Time - Get My Leads**
- [ ] **Endpoint**: `GET /api/v1/leads/my-leads`
- [ ] **Headers**: `Authorization: Bearer {adminToken}`
- [ ] **Expected**: Response time < 2 seconds
- [ ] **Actual Time**: `____` seconds
- [ ] **Status**: ⬜ Pass / ⬜ Fail

### **6.2 Response Time - Territory Access Check**
- [ ] **Endpoint**: `GET /api/v1/test/territory-access/N6B`
- [ ] **Headers**: `Authorization: Bearer {adminToken}`
- [ ] **Expected**: Response time < 1 second
- [ ] **Actual Time**: `____` seconds
- [ ] **Status**: ⬜ Pass / ⬜ Fail

## **Test Results Summary**

### **Overall Status**
- [ ] **All Tests Passed** ✅
- [ ] **Some Tests Failed** ⚠️
- [ ] **Most Tests Failed** ❌

### **Issues Found**
1. `_________________________________`
2. `_________________________________`
3. `_________________________________`

### **Next Steps**
- [ ] Fix identified issues
- [ ] Re-run failed tests
- [ ] Proceed to next phase of implementation
- [ ] Document any workarounds needed

---

**Tested By**: `_________________`
**Date**: `_________________`
**Time**: `_________________` 