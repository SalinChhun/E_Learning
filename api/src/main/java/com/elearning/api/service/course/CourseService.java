package com.elearning.api.service.course;

import com.elearning.api.payload.course.CourseRequest;
import com.elearning.api.payload.course.EnrollCourseRequest;
import com.elearning.api.payload.course.UpdateProgressRequest;
import org.springframework.data.domain.Pageable;

public interface CourseService {
    Object getCourses(String searchValue, Long categoryId, String status, Long userId, Pageable pageable);
    Object getAllCategories();
    Object getMyCourses(Long userId, String status);
    Object getMyCoursesSummary(Long userId);
    Object getCourseById(Long courseId, Long userId);
    Object createCourse(CourseRequest request);
    Object updateCourse(Long courseId, CourseRequest request);
    Object enrollInCourse(EnrollCourseRequest request, Long userId);
    Object bulkEnrollUsers(Long courseId, EnrollCourseRequest request);
    Object approveEnrollment(Long enrollmentId);
    Object rejectEnrollment(Long enrollmentId);
    Object publishCourse(Long courseId);
    Object unpublishCourse(Long courseId);
    Object getCourseLearners(Long courseId, String status, Pageable pageable);
    Object checkEnrollment(Long courseId, Long userId);
    Object deleteEnrollment(Long enrollmentId);
    Object getMyCourseById(Long courseId, Long userId);
    Object getEnrollments(Long courseId, Long userId, String status, Pageable pageable);
}

