# Swagger/OpenAPI Documentation Access

## Access URLs

After starting the application, you can access Swagger UI at:

**Swagger UI:** `http://localhost:8080/swagger-ui.html`

**OpenAPI JSON:** `http://localhost:8080/v3/api-docs`

**OpenAPI YAML:** `http://localhost:8080/v3/api-docs.yaml`

## Features

- **Interactive API Documentation**: Test all endpoints directly from the browser
- **JWT Authentication**: Click "Authorize" button and enter your Bearer token
- **Request/Response Examples**: See example requests and responses for each endpoint
- **Schema Documentation**: View detailed schemas for all request/response models

## Authentication

1. Click the **"Authorize"** button at the top right of Swagger UI
2. Enter your JWT token in the format: `Bearer YOUR_ACCESS_TOKEN`
3. Click **"Authorize"** and then **"Close"**
4. All authenticated endpoints will now use your token

## API Groups

The APIs are organized into the following groups:

1. **Category Management** - CRUD operations for course categories
2. **Course Management** - Course operations, enrollments, and progress tracking

## Example: Testing an API

1. Navigate to Swagger UI: `http://localhost:8080/swagger-ui.html`
2. Expand the "Category Management" section
3. Click on "POST /api/wba/v1/categories"
4. Click "Try it out"
5. Enter the request body:
   ```json
   {
     "name": "Finance",
     "description": "Financial courses"
   }
   ```
6. Click "Execute"
7. View the response

## Configuration

Swagger is configured in `SwaggerConfig.java` with:
- API title: "PPCBank E-Learning API"
- Version: 1.0.0
- JWT Bearer token authentication
- Contact information

All endpoints are secured with Bearer token authentication except Swagger UI itself.

