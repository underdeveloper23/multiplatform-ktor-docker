# API Documentation
Here's the documentation for the API endpoints in your application:

```Root Endpoint
GET /:
```

This endpoint serves the HTML content for the root of the application.
```
Response:
Status Code: 200 (OK)
Content Type: text/html
```

## Static Resources
```
GET /static/:
```

This endpoint serves static resources, such as CSS or JavaScript files.

```
Response:
Status Code: 200 (OK)
Content Type: Depends on the requested resource (e.g., text/css, application/javascript)
```

# User Management

## Create User

```
POST /api/users:
```


This endpoint allows the creation of a new user.

### Request:

```
Content Type: application/json
Body: User object
```

### Response:
```
Status Code: 201 (Created)
Content Type: application/json
Body: The ID of the newly created user
```


## Read User
```
GET /api/users/{id}:
```

This endpoint retrieves user information by providing the user's ID.

### Request:


Path Parameter: `id (integer)` - ID of the user to retrieve

### Response:

```
Status Code: 200 (OK) if the user exists
Status Code: 404 (Not Found) if the user does not exist
Content Type: application/json
Body: User object
```

## Update User
```
PUT /api/users/{id}:
```

This endpoint allows updating an existing user's information.

### Request:

```
Path Parameter: id (integer) - ID of the user to update
Content Type: application/json
Body: Updated User object
```

### Response:
```
Status Code: 200 (OK)
```


## Delete User
```
DELETE /api/users/{id}:
```

This endpoint allows the deletion of a user by providing the user's ID.

### Request:

```
Path Parameter: id (integer) - ID of the user to delete
```

### Response:
```
Status Code: 200 (OK)
```

## User Login
```
POST /api/users/login:
```

This endpoint handles user login and authentication.

### Request:

```
Content Type: application/json
Body: UserCredentials object
```

### Response:
```
Status Code: 200 (OK) if login is successful
Status Code: 401 (Unauthorized) if login fails
Status Code: 400 (Bad Request) if the tenant is not found
Content Type: text/plain
Body: "Login successful" or "Login failed" as appropriate
```

# Tenant Management
## Create Tenant

```
POST /api/tenants:
```

This endpoint allows the creation of a new tenant.

### Request:
```
Content Type: application/json
Body: ExposedTenant object
```

### Response:
```Status Code: 201 (Created)
Content Type: application/json
Body: The ID of the newly created tenant
```

## Get All Tenants
```
GET /api/tenants:
```

This endpoint retrieves a list of all tenants.

### Response:

```
Status Code: 200 (OK)
Content Type: application/json
Body: List of tenants
```

## Tenant Switch
```
POST /api/tenants/switch:
```

This endpoint handles switching the current tenant for a user.

### Request:
```
Content Type: application/json
Body: Tenant ID (integer)
```

### Response:
```
Status Code: 200 (OK)
```

## Get Users for Current Tenant
```
GET /api/tenants/current/users:
```

This endpoint retrieves users for the current tenant.

### Response:
```
Status Code: 200 (OK) if the user is authenticated
Status Code: 401 (Unauthorized) if login fails
Content Type: application/json
Body: List of users
```
