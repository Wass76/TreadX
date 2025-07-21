@echo off
echo 🧪 TreadX Hybrid Approach Quick Test
echo =====================================

set BASE_URL=http://localhost:8080

REM Wait for application to start
echo ⏳ Waiting for application to start...
timeout /t 10 /nobreak >nul

REM Test 1: Health Check
echo.
echo 🔍 Testing Health Check...
curl -s -X GET "%BASE_URL%/actuator/health"
if %errorlevel% equ 0 (
    echo ✅ Health check successful
) else (
    echo ❌ Health check failed
)

REM Test 2: Login as Platform Admin
echo.
echo 🔐 Testing Authentication...
for /f "tokens=*" %%i in ('curl -s -X POST -H "Content-Type: application/json" -d "{\"email\":\"admin@treadx.com\",\"password\":\"password\"}" "%BASE_URL%/api/v1/auth/login"') do set admin_response=%%i

echo Admin login response: %admin_response%

REM Extract token (simplified for Windows)
for /f "tokens=2 delims=:" %%a in ('echo %admin_response% ^| findstr "token"') do set admin_token=%%a
set admin_token=%admin_token:"=%

if defined admin_token (
    echo ✅ Admin login successful
    echo Token: %admin_token%
) else (
    echo ❌ Admin login failed
    goto :end
)

REM Test 3: Get Current User Info
echo.
echo 👤 Testing Get Current User Info...
curl -s -X GET -H "Authorization: Bearer %admin_token%" "%BASE_URL%/api/v1/test/current-user"
if %errorlevel% equ 0 (
    echo ✅ Get current user successful
) else (
    echo ❌ Get current user failed
)

REM Test 4: Test Territory Access
echo.
echo 🗺️ Testing Territory Access N6B...
curl -s -X GET -H "Authorization: Bearer %admin_token%" "%BASE_URL%/api/v1/test/territory-access/N6B"
if %errorlevel% equ 0 (
    echo ✅ Territory access test successful
) else (
    echo ❌ Territory access test failed
)

REM Test 5: Get Primary Territory
echo.
echo 🎯 Testing Get Primary Territory...
curl -s -X GET -H "Authorization: Bearer %admin_token%" "%BASE_URL%/api/v1/test/primary-territory"
if %errorlevel% equ 0 (
    echo ✅ Get primary territory successful
) else (
    echo ❌ Get primary territory failed
)

REM Test 6: Get My Leads (Automatic)
echo.
echo 📋 Testing Get My Leads (Automatic)...
curl -s -X GET -H "Authorization: Bearer %admin_token%" "%BASE_URL%/api/v1/leads/my-leads"
if %errorlevel% equ 0 (
    echo ✅ Get my leads successful
) else (
    echo ❌ Get my leads failed
)

REM Test 7: Get Leads by Territory (Explicit)
echo.
echo 🏢 Testing Get Leads by Territory N6B...
curl -s -X GET -H "Authorization: Bearer %admin_token%" "%BASE_URL%/api/v1/leads/territories/N6B"
if %errorlevel% equ 0 (
    echo ✅ Get leads by territory successful
) else (
    echo ❌ Get leads by territory failed
)

REM Test 8: Get My Leads by Status
echo.
echo 📊 Testing Get My Leads by Status PENDING...
curl -s -X GET -H "Authorization: Bearer %admin_token%" "%BASE_URL%/api/v1/leads/my-leads/status?status=PENDING"
if %errorlevel% equ 0 (
    echo ✅ Get my leads by status successful
) else (
    echo ❌ Get my leads by status failed
)

:end
echo.
echo 🎉 Quick test completed!
echo For detailed testing, use the Postman collection: test/TreadX_Hybrid_Testing.postman_collection.json
pause 