package com.elearning.api.controller;

import com.elearning.api.helper.AuthHelper;
import com.elearning.api.payload.MultiSortBuilder;
import com.elearning.api.payload.course.CourseRequest;
import com.elearning.api.payload.course.EnrollCourseRequest;
import com.elearning.api.service.course.CourseService;
import com.elearning.common.common.RestApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wba/v1/courses")
@RequiredArgsConstructor
@Tag(name = "Course Management", description = "APIs for managing courses, enrollments, and learning progress")
@SecurityRequirement(name = "Bearer Authentication")
public class CourseController extends RestApiResponse {

    private final CourseService courseService;

    @GetMapping()
    @Operation(
            summary = "Get public courses",
            description = "Retrieves a paginated list of public courses with optional search, category, and status filtering. If status is not provided, returns all public courses."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Courses retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<?> getPublicCourses(
            @Parameter(description = "Search by course title or description", example = "banking")
            @RequestParam(value = "search_value", required = false) String searchValue,
            @Parameter(description = "Filter by category ID", example = "1")
            @RequestParam(value = "category_id", required = false) Long categoryId,
            @Parameter(description = "Filter by course status (1=DRAFT, 2=PUBLISHED, 9=DELETE). If not provided, returns all public courses.", example = "2")
            @RequestParam(value = "status", required = false) String status,
            @Parameter(description = "Sort columns (e.g., 'id:desc' or 'title:asc,id:desc')", example = "id:desc")
            @RequestParam(value = "sort_columns", required = false, defaultValue = "id:desc") String sortColumns,
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(value = "page_number", defaultValue = "0") int pageNumber,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(value = "page_size", defaultValue = "10") int pageSize
    ) {
        List<Sort.Order> sortBuilder = new MultiSortBuilder().with(sortColumns).build();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortBuilder));

        return ok(courseService.getCourses(searchValue, categoryId, status, pageable));
    }

    @GetMapping("/categories")
    @Operation(
            summary = "Get all course categories",
            description = "Retrieves a list of all active course categories (deprecated - use /api/wba/v1/categories instead)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categories retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<?> getAllCategories() {
        return ok(courseService.getAllCategories());
    }

    @GetMapping("/my-courses")
    @Operation(
            summary = "Get my enrolled courses",
            description = "Retrieves all courses enrolled by the current user, optionally filtered by enrollment status"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Courses retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<?> getMyCourses(
            @Parameter(description = "Filter by enrollment status (1=PENDING, 2=ENROLLED, 3=IN_PROGRESS, 4=COMPLETED, 9=REJECTED)", example = "3")
            @RequestParam(value = "status", required = false) String status
    ) {
        Long userId = AuthHelper.getCurrentUserId();
        return ok(courseService.getMyCourses(userId, status));
    }

    @GetMapping("/my-courses/summary")
    @Operation(
            summary = "Get my courses summary",
            description = "Retrieves summary statistics (total, in progress, completed, certificates) and all enrolled courses for the current user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Summary retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<?> getMyCoursesSummary() {
        Long userId = AuthHelper.getCurrentUserId();
        return ok(courseService.getMyCoursesSummary(userId));
    }

    @GetMapping("/{courseId}")
    @Operation(
            summary = "Get course details",
            description = "Retrieves detailed information about a specific course including lessons and enrollment info for the current user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Course details retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Course not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<?> getCourseById(
            @Parameter(description = "Course ID", required = true, example = "1")
            @PathVariable Long courseId) {
        Long userId = AuthHelper.getCurrentUserId();
        return ok(courseService.getCourseById(courseId, userId));
    }

    @PostMapping
    @Operation(
            summary = "Create new course",
            description = "Creates a new course. The course will be created in DRAFT status. Requires admin privileges."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Course created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    })
    public ResponseEntity<?> createCourse(@Valid @RequestBody CourseRequest request) {
        return ok(courseService.createCourse(request));
    }

    @PutMapping("/{courseId}")
    @Operation(
            summary = "Update course",
            description = "Updates an existing course. Requires admin privileges."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Course updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Course or category not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    })
    public ResponseEntity<?> updateCourse(
            @Parameter(description = "Course ID", required = true, example = "1")
            @PathVariable Long courseId,
            @Valid @RequestBody CourseRequest request) {
        return ok(courseService.updateCourse(courseId, request));
    }

    @PostMapping("/enroll")
    @Operation(
            summary = "Enroll in course",
            description = "Enrolls the current user in a course. Enrollment status will be PENDING initially."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Enrollment request created successfully"),
            @ApiResponse(responseCode = "404", description = "Course not found"),
            @ApiResponse(responseCode = "409", description = "User is already enrolled in this course"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<?> enrollInCourse(@Valid @RequestBody EnrollCourseRequest request) {
        Long userId = AuthHelper.getCurrentUserId();
        return ok(courseService.enrollInCourse(request, userId));
    }

    @PostMapping("/{courseId}/enrollments")
    @Operation(
            summary = "Bulk enroll users",
            description = "Enrolls multiple users in a course. Requires admin privileges."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users enrolled successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input - user IDs required"),
            @ApiResponse(responseCode = "404", description = "Course not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    })
    public ResponseEntity<?> bulkEnrollUsers(
            @Parameter(description = "Course ID", required = true, example = "1")
            @PathVariable Long courseId,
            @Valid @RequestBody EnrollCourseRequest request) {
        return ok(courseService.bulkEnrollUsers(courseId, request));
    }

    @PatchMapping("/enrollments/{enrollmentId}/approve")
    @Operation(
            summary = "Approve enrollment",
            description = "Approves a pending enrollment request. Changes status from PENDING to ENROLLED. Requires admin privileges."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Enrollment approved successfully"),
            @ApiResponse(responseCode = "404", description = "Enrollment not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    })
    public ResponseEntity<?> approveEnrollment(
            @Parameter(description = "Enrollment ID", required = true, example = "1")
            @PathVariable Long enrollmentId) {
        return ok(courseService.approveEnrollment(enrollmentId));
    }

    @PatchMapping("/enrollments/{enrollmentId}/reject")
    @Operation(
            summary = "Reject enrollment",
            description = "Rejects a pending enrollment request. Changes status to REJECTED. Requires admin privileges."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Enrollment rejected successfully"),
            @ApiResponse(responseCode = "404", description = "Enrollment not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    })
    public ResponseEntity<?> rejectEnrollment(
            @Parameter(description = "Enrollment ID", required = true, example = "1")
            @PathVariable Long enrollmentId) {
        return ok(courseService.rejectEnrollment(enrollmentId));
    }

    @PatchMapping("/{courseId}/unpublish")
    @Operation(
            summary = "Unpublish course",
            description = "Unpublishes a course, removing it from public courses list. Changes status from PUBLISHED to DRAFT. Requires admin privileges."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Course unpublished successfully"),
            @ApiResponse(responseCode = "404", description = "Course not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    })
    public ResponseEntity<?> unpublishCourse(
            @Parameter(description = "Course ID", required = true, example = "1")
            @PathVariable Long courseId) {
        return ok(courseService.unpublishCourse(courseId));
    }

    @GetMapping("/{courseId}/learners")
    @Operation(
            summary = "Get enrolled learners for a course",
            description = "Retrieves a paginated list of learners enrolled in a specific course with summary statistics. " +
                    "Includes learner details, enrollment status, and progress information."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Learners retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Course not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<?> getCourseLearners(
            @Parameter(description = "Course ID", required = true, example = "1")
            @PathVariable Long courseId,
            @Parameter(description = "Filter by enrollment status (1=PENDING, 2=ENROLLED, 3=IN_PROGRESS, 4=COMPLETED, 9=REJECTED)", example = "3")
            @RequestParam(value = "status", required = false) String status,
            @Parameter(description = "Sort columns (e.g., 'id:desc' or 'name:asc')", example = "id:desc")
            @RequestParam(value = "sort_columns", required = false, defaultValue = "id:desc") String sortColumns,
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(value = "page_number", defaultValue = "0") int pageNumber,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(value = "page_size", defaultValue = "10") int pageSize
    ) {
        List<Sort.Order> sortBuilder = new MultiSortBuilder().with(sortColumns).build();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortBuilder));

        return ok(courseService.getCourseLearners(courseId, status, pageable));
    }
}

