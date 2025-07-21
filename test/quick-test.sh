#!/bin/bash

# Quick Test Script for TreadX Hybrid Implementation
echo "🧪 TreadX Hybrid Approach Quick Test"
echo "====================================="

BASE_URL="http://localhost:8080"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to test endpoint
test_endpoint() {
    local name="$1"
    local method="$2"
    local url="$3"
    local data="$4"
    local token="$5"
    
    echo -e "\n${YELLOW}Testing: $name${NC}"
    
    if [ -n "$token" ]; then
        if [ -n "$data" ]; then
            response=$(curl -s -w "\n%{http_code}" -X "$method" \
                -H "Content-Type: application/json" \
                -H "Authorization: Bearer $token" \
                -d "$data" \
                "$url")
        else
            response=$(curl -s -w "\n%{http_code}" -X "$method" \
                -H "Authorization: Bearer $token" \
                "$url")
        fi
    else
        if [ -n "$data" ]; then
            response=$(curl -s -w "\n%{http_code}" -X "$method" \
                -H "Content-Type: application/json" \
                -d "$data" \
                "$url")
        else
            response=$(curl -s -w "\n%{http_code}" -X "$method" \
                "$url")
        fi
    fi
    
    # Extract status code (last line)
    status_code=$(echo "$response" | tail -n1)
    # Extract response body (all lines except last)
    body=$(echo "$response" | head -n -1)
    
    if [ "$status_code" -eq 200 ] || [ "$status_code" -eq 201 ]; then
        echo -e "${GREEN}✅ Success ($status_code)${NC}"
        echo "Response: $body" | head -c 200
        if [ ${#body} -gt 200 ]; then
            echo "..."
        fi
    else
        echo -e "${RED}❌ Failed ($status_code)${NC}"
        echo "Response: $body"
    fi
}

# Wait for application to start
echo "⏳ Waiting for application to start..."
sleep 10

# Test 1: Health Check
test_endpoint "Health Check" "GET" "$BASE_URL/actuator/health"

# Test 2: Login as Platform Admin
echo -e "\n${YELLOW}🔐 Testing Authentication${NC}"
admin_response=$(curl -s -X POST \
    -H "Content-Type: application/json" \
    -d '{"email":"admin@treadx.com","password":"password"}' \
    "$BASE_URL/api/v1/auth/login")

admin_token=$(echo "$admin_response" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

if [ -n "$admin_token" ]; then
    echo -e "${GREEN}✅ Admin login successful${NC}"
else
    echo -e "${RED}❌ Admin login failed${NC}"
    echo "Response: $admin_response"
    exit 1
fi

# Test 3: Get Current User Info
test_endpoint "Get Current User Info" "GET" "$BASE_URL/api/v1/test/current-user" "" "$admin_token"

# Test 4: Test Territory Access
test_endpoint "Test Territory Access N6B" "GET" "$BASE_URL/api/v1/test/territory-access/N6B" "" "$admin_token"

# Test 5: Get Primary Territory
test_endpoint "Get Primary Territory" "GET" "$BASE_URL/api/v1/test/primary-territory" "" "$admin_token"

# Test 6: Get My Leads (Automatic)
test_endpoint "Get My Leads (Automatic)" "GET" "$BASE_URL/api/v1/leads/my-leads" "" "$admin_token"

# Test 7: Get Leads by Territory (Explicit)
test_endpoint "Get Leads by Territory N6B" "GET" "$BASE_URL/api/v1/leads/territories/N6B" "" "$admin_token"

# Test 8: Get My Leads by Status
test_endpoint "Get My Leads by Status PENDING" "GET" "$BASE_URL/api/v1/leads/my-leads/status?status=PENDING" "" "$admin_token"

echo -e "\n${GREEN}🎉 Quick test completed!${NC}"
echo "For detailed testing, use the Postman collection: test/TreadX_Hybrid_Testing.postman_collection.json" 