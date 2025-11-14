# Course API - cURL Request Examples

Base URL: `http://localhost:8080/api/wba/v1`

**Note:** Replace `YOUR_ACCESS_TOKEN` with your actual JWT token from login.

---

# Category CRUD APIs

Base URL: `http://localhost:8080/api/wba/v1/categories`

## 1. Create Category

```bash
curl -X POST "http://localhost:8080/api/wba/v1/categories" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Finance",
    "description": "Financial courses and banking fundamentals"
  }'
```

**Request Body:**
```json
{
  "name": "string (required)",
  "description": "string (optional)"
}
```

**Response:**
```json
{
  "status": {
    "code": 200,
    "message": "SUCCESSFUL"
  },
  "data": {
    "id": 1,
    "name": "Finance",
    "description": "Financial courses and banking fundamentals"
  }
}
```

---

## 2. Get All Categories

```bash
curl -X GET "http://localhost:8080/api/wba/v1/categories" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json"
```

**Response:**
```json
{
  "status": {
    "code": 200,
    "message": "SUCCESSFUL"
  },
  "data": {
    "categories": [
      {
        "id": 1,
        "name": "Finance",
        "description": "Financial courses and banking fundamentals"
      },
      {
        "id": 2,
        "name": "Compliance",
        "description": "Regulatory compliance training"
      }
    ]
  }
}
```

---

## 3. Get Category by ID

```bash
curl -X GET "http://localhost:8080/api/wba/v1/categories/1" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json"
```

**Response:**
```json
{
  "status": {
    "code": 200,
    "message": "SUCCESSFUL"
  },
  "data": {
    "id": 1,
    "name": "Finance",
    "description": "Financial courses and banking fundamentals"
  }
}
```

---

## 4. Update Category

```bash
curl -X PUT "http://localhost:8080/api/wba/v1/categories/1" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Financial Services",
    "description": "Updated description for financial services courses"
  }'
```

**Request Body:** Same as Create Category

**Response:**
```json
{
  "status": {
    "code": 200,
    "message": "SUCCESSFUL"
  },
  "data": {
    "id": 1,
    "name": "Financial Services",
    "description": "Updated description for financial services courses"
  }
}
```

---

## 5. Delete Category

```bash
curl -X DELETE "http://localhost:8080/api/wba/v1/categories/1" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json"
```

**Response:**
```json
{
  "status": {
    "code": 200,
    "message": "SUCCESSFUL"
  },
  "data": {
    "message": "Category deleted successfully",
    "categoryId": 1
  }
}
```

**Note:** This performs a soft delete by setting the status to DISABLE. The category will not appear in GET requests but remains in the database.

---

# Course APIs

Base URL: `http://localhost:8080/api/wba/v1/courses`

---

## 1. Get Public Courses

```bash
curl -X GET "http://localhost:8080/api/wba/v1/courses/public?search_value=banking&category_id=1&sort_columns=id:desc&page_number=0&page_size=10" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json"
```

**Query Parameters:**
- `search_value` (optional): Search by title or description
- `category_id` (optional): Filter by category
- `sort_columns` (optional, default: "id:desc"): Sort columns (e.g., "title:asc,id:desc")
- `page_number` (optional, default: 0): Page number
- `page_size` (optional, default: 10): Page size

---

## 2. Get All Categories

```bash
curl -X GET "http://localhost:8080/api/wba/v1/courses/categories" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json"
```

---

## 3. Get My Courses

```bash
# Get all my courses
curl -X GET "http://localhost:8080/api/wba/v1/courses/my-courses" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json"

# Get my courses filtered by status (1=PENDING, 2=ENROLLED, 3=IN_PROGRESS, 4=COMPLETED)
curl -X GET "http://localhost:8080/api/wba/v1/courses/my-courses?status=3" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json"
```

**Query Parameters:**
- `status` (optional): Filter by enrollment status
  - `"1"` = PENDING
  - `"2"` = ENROLLED
  - `"3"` = IN_PROGRESS
  - `"4"` = COMPLETED
  - `"9"` = REJECTED

---

## 4. Get My Courses Summary

```bash
curl -X GET "http://localhost:8080/api/wba/v1/courses/my-courses/summary" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json"
```

**Response includes:**
- Total courses count
- In progress count
- Completed count
- Certificates count
- Full course list

---

## 5. Get Course Details by ID

```bash
curl -X GET "http://localhost:8080/api/wba/v1/courses/1" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json"
```

**Response includes:**
- Course details
- Lessons list
- Enrollment info (if user is enrolled)

---

## 6. Create New Course (Admin)

```bash
curl -X POST "http://localhost:8080/api/wba/v1/courses" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Banking Fundamentals",
    "description": "Essential banking concepts and practices for all staff",
    "category_id": 1,
    "duration_hours": 8,
    "estimated_days": 4,
    "due_date": "2025-12-31",
    "is_public": true,
    "image_url": "https://example.com/course-image.jpg"
  }'
```

**Request Body:**
```json
{
  "title": "string (required)",
  "description": "string (optional)",
  "category_id": "number (required)",
  "duration_hours": "number (optional)",
  "estimated_days": "number (optional)",
  "due_date": "YYYY-MM-DD (optional)",
  "is_public": "boolean (optional, default: false)",
  "image_url": "string (optional)"
}
```

---

## 7. Update Course (Admin)

```bash
curl -X PUT "http://localhost:8080/api/wba/v1/courses/1" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Updated Banking Fundamentals",
    "description": "Updated description",
    "category_id": 1,
    "duration_hours": 10,
    "estimated_days": 5,
    "due_date": "2025-12-31",
    "is_public": true,
    "image_url": "https://example.com/updated-image.jpg"
  }'
```

**Request Body:** Same as Create Course

---

## 8. Enroll in Course

```bash
curl -X POST "http://localhost:8080/api/wba/v1/courses/enroll" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "course_id": 1
  }'
```

**Request Body:**
```json
{
  "course_id": "number (required)"
}
```

**Note:** This enrolls the currently authenticated user. The enrollment status will be PENDING initially.

---

## 9. Bulk Enroll Users (Admin)

```bash
curl -X POST "http://localhost:8080/api/wba/v1/courses/1/enrollments" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "user_ids": [2, 3, 4, 5]
  }'
```

**Request Body:**
```json
{
  "user_ids": [1, 2, 3]  // Array of user IDs to enroll
}
```

**Response:**
```json
{
  "enrolled_count": 3,
  "total_requested": 3
}
```

---

## 10. Approve Enrollment Request (Admin)

```bash
curl -X PATCH "http://localhost:8080/api/wba/v1/courses/enrollments/1/approve" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json"
```

**Changes enrollment status from PENDING to ENROLLED**

---

## 11. Reject Enrollment Request (Admin)

```bash
curl -X PATCH "http://localhost:8080/api/wba/v1/courses/enrollments/1/reject" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json"
```

**Changes enrollment status to REJECTED**

---

## 12. Update Course Progress

```bash
curl -X PATCH "http://localhost:8080/api/wba/v1/courses/enrollments/1/progress" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "progress_percentage": 65,
    "time_spent_seconds": 3600
  }'
```

**Request Body:**
```json
{
  "progress_percentage": "number (required, 0-100)",
  "time_spent_seconds": "number (optional)"
}
```

**Note:** 
- Progress 100% automatically sets status to COMPLETED
- Progress > 0% automatically sets status to IN_PROGRESS (if currently ENROLLED)

---

## 13. Publish Course (Admin)

```bash
curl -X PATCH "http://localhost:8080/api/wba/v1/courses/1/publish" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json"
```

**Changes course status from DRAFT to PUBLISHED, making it visible in public courses list.**

**Response:**
```json
{
  "status": {
    "code": 200,
    "message": "SUCCESSFUL"
  },
  "data": {
    "id": 1,
    "title": "Banking Fundamentals",
    "status": "Published",
    "is_public": true
  }
}
```

---

## 14. Unpublish Course (Admin)

```bash
curl -X PATCH "http://localhost:8080/api/wba/v1/courses/1/unpublish" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json"
```

**Changes course status from PUBLISHED to DRAFT, removing it from public courses list.**

---

## 15. Get Enrolled Learners by Course ID

```bash
curl -X GET "http://localhost:8080/api/wba/v1/courses/1/learners?status=3&sort_columns=id:desc&page_number=0&page_size=10" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json"
```

**Query Parameters:**
- `status` (optional): Filter by enrollment status (1=PENDING, 2=ENROLLED, 3=IN_PROGRESS, 4=COMPLETED, 9=REJECTED)
- `sort_columns` (optional, default: "id:desc"): Sort columns (e.g., "name:asc,id:desc")
- `page_number` (optional, default: 0): Page number
- `page_size` (optional, default: 10): Page size

**Response:**
```json
{
  "status": {
    "code": 200,
    "message": "SUCCESSFUL"
  },
  "data": {
    "totalLearners": 5,
    "completedCount": 1,
    "inProgressCount": 2,
    "pendingCount": 2,
    "learners": [
      {
        "enrollment_id": 1,
        "user_id": 2,
        "name": "Sarah Johnson",
        "email": "sarah.j@ppcbank.com",
        "department": "Retail Banking",
        "status": "Completed",
        "progress_percentage": 100,
        "enrolled_date": "2025-10-20T00:00:00Z",
        "completed_date": "2025-10-25T00:00:00Z"
      },
      {
        "enrollment_id": 2,
        "user_id": 3,
        "name": "Michael Chen",
        "email": "michael.c@ppcbank.com",
        "department": "Corporate Banking",
        "status": "In Progress",
        "progress_percentage": 75,
        "enrolled_date": "2025-10-08T00:00:00Z",
        "completed_date": null
      }
    ],
    "totalElements": 5,
    "totalPages": 1,
    "currentPage": 0,
    "pageSize": 10,
    "hasNext": false,
    "hasPrevious": false
  }
}
```

**Note:** 
- Returns summary statistics: total learners, completed count, in progress count, and pending count
- Each learner includes enrollment details, progress, and status
- Supports filtering by enrollment status and pagination

---

## ⚠️ Important: Why Courses May Not Appear in Public Courses

**The `/courses/public` endpoint only returns courses that meet ALL of these conditions:**

1. ✅ **Course Status = PUBLISHED** (`status = "2"`)
   - DRAFT courses (`status = "1"`) will NOT appear
   - Use `PATCH /courses/{courseId}/publish` to publish a course

2. ✅ **Course is Public** (`is_public = true`)

3. ✅ **Category Status = NORMAL** (`category.status = "1"`)
   - Categories with status DISABLE (`status = "2"`) will hide their courses

**To fix empty results:**
1. **Publish the course:**
   ```bash
   curl -X PATCH "http://localhost:8080/api/wba/v1/courses/1/publish" \
     -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
   ```

2. **Verify category status is NORMAL** (should be "1" in database)

3. **Verify course is_public is true** (should be `true` in database)

---

## Authentication

All endpoints require authentication. Get your token from the login endpoint:

```bash
curl -X POST "http://localhost:8080/api/wba/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "your_username",
    "password": "your_password"
  }'
```

**Response:**
```json
{
  "status": {
    "code": 200,
    "message": "SUCCESSFUL"
  },
  "data": {
    "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expires_in": 3600
  }
}
```

Use the `access_token` in the `Authorization: Bearer` header for all subsequent requests.

---

## Status Codes Reference

### Enrollment Status Values:
- `"1"` = PENDING REQUEST
- `"2"` = ENROLLED
- `"3"` = IN PROGRESS
- `"4"` = COMPLETED
- `"9"` = REJECTED

### Course Status Values:
- `"1"` = DRAFT
- `"2"` = PUBLISHED
- `"9"` = ARCHIVED

---

## Example: Complete Workflow

### 1. Login
```bash
TOKEN=$(curl -s -X POST "http://localhost:8080/api/wba/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password"}' | jq -r '.data.access_token')
```

### 2. Get Public Courses
```bash
curl -X GET "http://localhost:8080/api/wba/v1/courses/public" \
  -H "Authorization: Bearer $TOKEN"
```

### 3. Enroll in Course
```bash
curl -X POST "http://localhost:8080/api/wba/v1/courses/enroll" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"course_id": 1}'
```

### 4. Get My Courses
```bash
curl -X GET "http://localhost:8080/api/wba/v1/courses/my-courses" \
  -H "Authorization: Bearer $TOKEN"
```

### 5. Update Progress
```bash
curl -X PATCH "http://localhost:8080/api/wba/v1/courses/enrollments/1/progress" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"progress_percentage": 50, "time_spent_seconds": 1800}'
```

---

## Error Responses

All endpoints return errors in this format:

```json
{
  "status": {
    "code": 404,
    "message": "Resource not found"
  },
  "data": null
}
```

Common HTTP Status Codes:
- `200` - Success
- `201` - Created
- `400` - Bad Request
- `401` - Unauthorized
- `403` - Forbidden
- `404` - Not Found
- `409` - Conflict (e.g., duplicate enrollment)
- `422` - Validation Error
- `500` - Internal Server Error

