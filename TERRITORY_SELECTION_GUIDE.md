# Territory Selection and API Header Guide for Multi-Tenant (District) System

## Overview
This guide explains how the frontend should handle territory (district) selection and configure API requests to ensure all management operations (leads, vendors, etc.) are routed to the correct district database.

---

## 1. User Login and Territory Discovery
- User logs in via the authentication endpoint (e.g., `/api/v1/auth/login`).
- Backend returns a JWT/session token and user info.
- **After login**, the frontend must fetch the list of territories (districts) the user can access:
  - **API:** `GET /api/v1/user-territories/my`
  - **Headers:** `Authorization: Bearer <token>`
  - **Response Example:**
    ```json
    [
      { "id": 1, "code": "n6b", "name": "District N6B" },
      { "id": 2, "code": "n5v", "name": "District N5V" }
    ]
    ```

---

## 2. Territory Selection in the UI
- If the user has only one territory, auto-select it.
- If the user has multiple territories, show a dropdown or selector for the user to pick.
- Store the selected territory code (e.g., `n6b`) in the frontend state (Redux, Vuex, React Context, etc.).

---

## 3. Attaching the Territory Code to API Requests
- For every API call that needs to be territory-aware, add the header:
  - `X-Territory-Code: <selectedTerritoryCode>`
- Example with Axios:
  ```js
  axios.defaults.headers.common['X-Territory-Code'] = selectedTerritoryCode;
  // or per request:
  axios.get('/api/v1/leads', { headers: { 'X-Territory-Code': selectedTerritoryCode } });
  ```

---

## 4. Changing Territory
- If the user changes the selected territory, update the stored value and update the default header for future requests.

---

## 5. Displaying the Current Territory
- Show the current territory in the UI (e.g., navbar) so the user always knows which district they are working in.

---

## 6. Example Flow
```mermaid
sequenceDiagram
    participant User
    participant Frontend
    participant Backend

    User->>Frontend: Login
    Frontend->>Backend: POST /login
    Backend-->>Frontend: JWT + user info
    Frontend->>Backend: GET /user-territories/my (with JWT)
    Backend-->>Frontend: [n6b, n5v, ...]
    Frontend->>User: Show territory selector (if >1)
    User->>Frontend: Selects territory (e.g., n6b)
    Frontend: Stores territory code (n6b)
    Frontend->>Backend: Any API call (with X-Territory-Code: n6b)
    Backend: Uses correct DB for n6b
```

---

## 7. Best Practices
- Always fetch user territories after login.
- Require the user to select a territory if they have more than one.
- Always send the territory code in the header for every relevant request.
- Update the header if the user changes territory.
- Show the current territory in the UI for clarity.

---

## 8. Example API Endpoints
- **Get User Territories:**
  - `GET /api/v1/user-territories/my`
  - Returns all territories the user can access.
- **Other Management Endpoints:**
  - All require `X-Territory-Code` header.

---

## 9. Summary Table
| Operation         | Territory Code Required | DB Used         | How to Specify |
|-------------------|------------------------|-----------------|----------------|
| Add Lead          | Yes                    | That district   | Header/Param   |
| Update Vendor     | Yes                    | That district   | Header/Param   |
| Get All Leads     | Yes                    | That district   | Header/Param   |
| Cross-district    | Yes (per request)      | Each district   | Loop in code   |

---

## 10. What to Ask Your Frontend Team to Implement
1. After login, fetch `/api/v1/user-territories/my` and store the list.
2. Require the user to select a territory if more than one is available.
3. Store the selected territory code in app state.
4. Attach `X-Territory-Code` to every API request.
5. Update the header if the user changes territory.
6. Show the current territory in the UI. 