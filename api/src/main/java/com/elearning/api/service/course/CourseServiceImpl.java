package com.elearning.api.service.course;

import com.elearning.api.payload.course.*;
import com.elearning.common.domain.course.Course;
import com.elearning.common.domain.course.CourseCategory;
import com.elearning.common.domain.course.CourseCategoryRepository;
import com.elearning.common.domain.course.CourseEnrollment;
import com.elearning.common.domain.course.CourseEnrollmentRepository;
import com.elearning.common.domain.course.CourseRepository;
import com.elearning.common.domain.course.Lesson;
import com.elearning.common.domain.course.LessonRepository;
import com.elearning.common.domain.user.User;
import com.elearning.common.domain.user.UserRepository;
import com.elearning.common.enums.CourseStatus;
import com.elearning.common.enums.EnrollmentStatus;
import com.elearning.common.enums.Status;
import com.elearning.common.enums.StatusCode;
import com.elearning.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CourseCategoryRepository courseCategoryRepository;
    private final CourseEnrollmentRepository courseEnrollmentRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Object getPublicCourses(String searchValue, Long categoryId, Pageable pageable) {
        Page<Course> coursesPage = courseRepository.findPublicCourses(
                CourseStatus.PUBLISHED,
                Status.NORMAL,
                categoryId,
                searchValue,
                pageable
        );

        List<CourseResponse> courseResponses = coursesPage.getContent().stream()
                .map(course -> {
                    Long learnerCount = courseRepository.countEnrollmentsByCourseId(course.getId());
                    return CourseResponse.builder()
                            .id(course.getId())
                            .title(course.getTitle())
                            .description(course.getDescription())
                            .category(course.getCategory().getName())
                            .categoryId(course.getCategory().getId())
                            .durationHours(course.getDurationHours())
                            .estimatedDays(course.getEstimatedDays())
                            .dueDate(course.getDueDate())
                            .status(course.getStatus().getLabel())
                            .isPublic(course.getIsPublic())
                            .imageUrl(course.getImageUrl())
                            .learnerCount(learnerCount != null ? learnerCount : 0L)
                            .createdAt(course.getCreatedAt())
                            .updatedAt(course.getUpdateAt())
                            .build();
                })
                .collect(Collectors.toList());

        Page<CourseResponse> responsePage = new PageImpl<>(
                courseResponses,
                coursesPage.getPageable(),
                coursesPage.getTotalElements()
        );

        Map<String, Object> response = new HashMap<>();
        response.put("courses", courseResponses);
        response.put("totalElements", coursesPage.getTotalElements());
        response.put("totalPages", coursesPage.getTotalPages());
        response.put("currentPage", coursesPage.getNumber());
        response.put("pageSize", coursesPage.getSize());
        response.put("hasNext", coursesPage.hasNext());
        response.put("hasPrevious", coursesPage.hasPrevious());

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public Object getAllCategories() {
        List<CourseCategory> categories = courseCategoryRepository.findByStatus(Status.NORMAL);
        
        List<CourseCategoryResponse> categoryResponses = categories.stream()
                .map(category -> CourseCategoryResponse.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .description(category.getDescription())
                        .build())
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("categories", categoryResponses);
        
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public Object getMyCourses(Long userId, String status) {
        List<CourseEnrollment> enrollments;
        
        if (status != null && !status.isEmpty()) {
            EnrollmentStatus enrollmentStatus = EnrollmentStatus.fromValue(status);
            if (enrollmentStatus != null) {
                enrollments = courseEnrollmentRepository.findByUserIdAndStatus(userId, enrollmentStatus);
            } else {
                enrollments = courseEnrollmentRepository.findByUserId(userId);
            }
        } else {
            enrollments = courseEnrollmentRepository.findByUserId(userId);
        }

        List<MyCourseResponse> courseResponses = enrollments.stream()
                .map(enrollment -> {
                    Course course = enrollment.getCourse();
                    return MyCourseResponse.builder()
                            .enrollmentId(enrollment.getId())
                            .courseId(course.getId())
                            .title(course.getTitle())
                            .description(course.getDescription())
                            .category(course.getCategory().getName())
                            .durationHours(course.getDurationHours())
                            .estimatedDays(course.getEstimatedDays())
                            .dueDate(course.getDueDate())
                            .status(enrollment.getStatus().getLabel())
                            .progressPercentage(enrollment.getProgressPercentage())
                            .timeSpentSeconds(enrollment.getTimeSpentSeconds())
                            .enrolledDate(enrollment.getEnrolledDate())
                            .completedDate(enrollment.getCompletedDate())
                            .imageUrl(course.getImageUrl())
                            .build();
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("courses", courseResponses);
        response.put("total", (long) courseResponses.size());
        
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public Object getMyCoursesSummary(Long userId) {
        Long totalCourses = courseEnrollmentRepository.countByUserId(userId);
        Long inProgress = courseEnrollmentRepository.countByUserIdAndStatus(userId, EnrollmentStatus.IN_PROGRESS);
        Long completed = courseEnrollmentRepository.countByUserIdAndStatus(userId, EnrollmentStatus.COMPLETED);
        Long certificates = courseEnrollmentRepository.countCompletedWithCertificate(userId, EnrollmentStatus.COMPLETED);

        List<CourseEnrollment> enrollments = courseEnrollmentRepository.findByUserId(userId);
        List<MyCourseResponse> courseResponses = enrollments.stream()
                .map(enrollment -> {
                    Course course = enrollment.getCourse();
                    return MyCourseResponse.builder()
                            .enrollmentId(enrollment.getId())
                            .courseId(course.getId())
                            .title(course.getTitle())
                            .description(course.getDescription())
                            .category(course.getCategory().getName())
                            .durationHours(course.getDurationHours())
                            .estimatedDays(course.getEstimatedDays())
                            .dueDate(course.getDueDate())
                            .status(enrollment.getStatus().getLabel())
                            .progressPercentage(enrollment.getProgressPercentage())
                            .timeSpentSeconds(enrollment.getTimeSpentSeconds())
                            .enrolledDate(enrollment.getEnrolledDate())
                            .completedDate(enrollment.getCompletedDate())
                            .imageUrl(course.getImageUrl())
                            .build();
                })
                .collect(Collectors.toList());

        return MyCoursesSummaryResponse.builder()
                .totalCourses(totalCourses != null ? totalCourses : 0L)
                .inProgress(inProgress != null ? inProgress : 0L)
                .completed(completed != null ? completed : 0L)
                .certificates(certificates != null ? certificates : 0L)
                .courses(courseResponses)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Object getCourseById(Long courseId, Long userId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(StatusCode.COURSE_NOT_FOUND));

        List<Lesson> lessons = lessonRepository.findByCourseIdOrderByOrderSequenceAsc(courseId);
        List<LessonResponse> lessonResponses = lessons.stream()
                .map(lesson -> LessonResponse.builder()
                        .id(lesson.getId())
                        .title(lesson.getTitle())
                        .description(lesson.getDescription())
                        .content(lesson.getContent())
                        .videoUrl(lesson.getVideoUrl())
                        .durationMinutes(lesson.getDurationMinutes())
                        .orderSequence(lesson.getOrderSequence())
                        .build())
                .collect(Collectors.toList());

        Long learnerCount = courseRepository.countEnrollmentsByCourseId(courseId);

        CourseDetailResponse.EnrollmentInfo enrollmentInfo = null;
        if (userId != null) {
            Optional<CourseEnrollment> enrollment = courseEnrollmentRepository.findByCourseAndUser(
                    course, userRepository.findById(userId).orElse(null));
            if (enrollment.isPresent()) {
                CourseEnrollment ce = enrollment.get();
                enrollmentInfo = new CourseDetailResponse.EnrollmentInfo();
                enrollmentInfo.setEnrollmentId(ce.getId());
                enrollmentInfo.setStatus(ce.getStatus().getLabel());
                enrollmentInfo.setProgressPercentage(ce.getProgressPercentage());
                enrollmentInfo.setTimeSpentSeconds(ce.getTimeSpentSeconds());
                enrollmentInfo.setEnrolledDate(ce.getEnrolledDate());
                enrollmentInfo.setCompletedDate(ce.getCompletedDate());
            }
        }

        return CourseDetailResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .category(course.getCategory().getName())
                .categoryId(course.getCategory().getId())
                .durationHours(course.getDurationHours())
                .estimatedDays(course.getEstimatedDays())
                .dueDate(course.getDueDate())
                .status(course.getStatus().getLabel())
                .isPublic(course.getIsPublic())
                .imageUrl(course.getImageUrl())
                .learnerCount(learnerCount != null ? learnerCount : 0L)
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdateAt())
                .lessons(lessonResponses)
                .enrollmentInfo(enrollmentInfo)
                .build();
    }

    @Override
    @Transactional
    public Object createCourse(CourseRequest request) {
        CourseCategory category = courseCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new BusinessException(StatusCode.COURSE_CATEGORY_NOT_FOUND));

        Course course = Course.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(category)
                .durationHours(request.getDurationHours())
                .estimatedDays(request.getEstimatedDays())
                .dueDate(request.getDueDate())
                .status(CourseStatus.PUBLISHED)
                .isPublic(request.getIsPublic() != null ? request.getIsPublic() : false)
                .imageUrl(request.getImageUrl())
                .build();

        course = courseRepository.save(course);
        return CourseResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .category(course.getCategory().getName())
                .categoryId(course.getCategory().getId())
                .durationHours(course.getDurationHours())
                .estimatedDays(course.getEstimatedDays())
                .dueDate(course.getDueDate())
                .status(course.getStatus().getLabel())
                .isPublic(course.getIsPublic())
                .imageUrl(course.getImageUrl())
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdateAt())
                .build();
    }

    @Override
    @Transactional
    public Object updateCourse(Long courseId, CourseRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(StatusCode.COURSE_NOT_FOUND));

        CourseCategory category = courseCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new BusinessException(StatusCode.COURSE_CATEGORY_NOT_FOUND));

        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setCategory(category);
        course.setDurationHours(request.getDurationHours());
        course.setEstimatedDays(request.getEstimatedDays());
        course.setDueDate(request.getDueDate());
        if (request.getIsPublic() != null) {
            course.setIsPublic(request.getIsPublic());
        }
        course.setImageUrl(request.getImageUrl());

        course = courseRepository.save(course);
        return CourseResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .category(course.getCategory().getName())
                .categoryId(course.getCategory().getId())
                .durationHours(course.getDurationHours())
                .estimatedDays(course.getEstimatedDays())
                .dueDate(course.getDueDate())
                .status(course.getStatus().getLabel())
                .isPublic(course.getIsPublic())
                .imageUrl(course.getImageUrl())
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdateAt())
                .build();
    }

    @Override
    @Transactional
    public Object enrollInCourse(EnrollCourseRequest request, Long userId) {
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new BusinessException(StatusCode.COURSE_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(StatusCode.USER_NOT_FOUND));

        // Check if already enrolled
        Optional<CourseEnrollment> existing = courseEnrollmentRepository.findByCourseAndUser(course, user);
        if (existing.isPresent()) {
            throw new BusinessException(StatusCode.ENROLLMENT_ALREADY_EXISTS);
        }

        CourseEnrollment enrollment = CourseEnrollment.builder()
                .course(course)
                .user(user)
                .status(EnrollmentStatus.PENDING)
                .progressPercentage(0)
                .enrolledDate(Instant.now())
                .timeSpentSeconds(0L)
                .build();

        enrollment = courseEnrollmentRepository.save(enrollment);
        return MyCourseResponse.builder()
                .enrollmentId(enrollment.getId())
                .courseId(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .category(course.getCategory().getName())
                .durationHours(course.getDurationHours())
                .estimatedDays(course.getEstimatedDays())
                .dueDate(course.getDueDate())
                .status(enrollment.getStatus().getLabel())
                .progressPercentage(enrollment.getProgressPercentage())
                .timeSpentSeconds(enrollment.getTimeSpentSeconds())
                .enrolledDate(enrollment.getEnrolledDate())
                .imageUrl(course.getImageUrl())
                .build();
    }

    @Override
    @Transactional
    public Object bulkEnrollUsers(Long courseId, EnrollCourseRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(StatusCode.COURSE_NOT_FOUND));

        if (request.getUserIds() == null || request.getUserIds().isEmpty()) {
            throw new BusinessException(StatusCode.BAD_REQUEST);
        }

        List<User> users = userRepository.findAllById(request.getUserIds());
        List<CourseEnrollment> enrollments = new ArrayList<>();

        for (User user : users) {
            Optional<CourseEnrollment> existing = courseEnrollmentRepository.findByCourseAndUser(course, user);
            if (existing.isEmpty()) {
                CourseEnrollment enrollment = CourseEnrollment.builder()
                        .course(course)
                        .user(user)
                        .status(EnrollmentStatus.PENDING)
                        .progressPercentage(0)
                        .enrolledDate(Instant.now())
                        .timeSpentSeconds(0L)
                        .build();
                enrollments.add(enrollment);
            }
        }

        enrollments = courseEnrollmentRepository.saveAll(enrollments);

        Map<String, Object> response = new HashMap<>();
        response.put("enrolledCount", enrollments.size());
        response.put("totalRequested", request.getUserIds().size());
        return response;
    }

    @Override
    @Transactional
    public Object approveEnrollment(Long enrollmentId) {
        CourseEnrollment enrollment = courseEnrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new BusinessException(StatusCode.ENROLLMENT_NOT_FOUND));

        enrollment.setStatus(EnrollmentStatus.IN_PROGRESS);
        if (enrollment.getEnrolledDate() == null) {
            enrollment.setEnrolledDate(Instant.now());
        }

        enrollment = courseEnrollmentRepository.save(enrollment);
        return MyCourseResponse.builder()
                .enrollmentId(enrollment.getId())
                .courseId(enrollment.getCourse().getId())
                .title(enrollment.getCourse().getTitle())
                .status(enrollment.getStatus().getLabel())
                .build();
    }

    @Override
    @Transactional
    public Object rejectEnrollment(Long enrollmentId) {
        CourseEnrollment enrollment = courseEnrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new BusinessException(StatusCode.ENROLLMENT_NOT_FOUND));

        enrollment.setStatus(EnrollmentStatus.REJECTED);
        enrollment = courseEnrollmentRepository.save(enrollment);

        Map<String, Object> response = new HashMap<>();
        response.put("enrollmentId", enrollment.getId());
        response.put("status", enrollment.getStatus().getLabel());
        return response;
    }

    @Override
    @Transactional
    public Object publishCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(StatusCode.COURSE_NOT_FOUND));

        course.setStatus(CourseStatus.PUBLISHED);
        course = courseRepository.save(course);

        return CourseResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .status(course.getStatus().getLabel())
                .isPublic(course.getIsPublic())
                .build();
    }

    @Override
    @Transactional
    public Object unpublishCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(StatusCode.COURSE_NOT_FOUND));

        course.setStatus(CourseStatus.DRAFT);
        course = courseRepository.save(course);

        return CourseResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .status(course.getStatus().getLabel())
                .isPublic(course.getIsPublic())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Object getCourseLearners(Long courseId, String status, Pageable pageable) {
        // Verify course exists
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(StatusCode.COURSE_NOT_FOUND));

        // Get enrollments with optional status filter
        Page<CourseEnrollment> enrollmentsPage;
        if (status != null && !status.isEmpty()) {
            EnrollmentStatus enrollmentStatus = EnrollmentStatus.fromValue(status);
            if (enrollmentStatus != null) {
                enrollmentsPage = courseEnrollmentRepository.findByCourseIdAndStatus(courseId, enrollmentStatus, pageable);
            } else {
                enrollmentsPage = courseEnrollmentRepository.findByCourseId(courseId, pageable);
            }
        } else {
            enrollmentsPage = courseEnrollmentRepository.findByCourseId(courseId, pageable);
        }

        // Get summary statistics
        Long totalLearners = courseEnrollmentRepository.countByCourseId(courseId);
        Long completedCount = courseEnrollmentRepository.countByCourseIdAndStatus(courseId, EnrollmentStatus.COMPLETED);
        Long inProgressCount = courseEnrollmentRepository.countByCourseIdAndStatus(courseId, EnrollmentStatus.IN_PROGRESS);
        Long pendingCount = courseEnrollmentRepository.countByCourseIdAndStatus(courseId, EnrollmentStatus.PENDING);

        // Map to response DTOs
        List<EnrolledLearnerResponse> learnerResponses = enrollmentsPage.getContent().stream()
                .map(enrollment -> {
                    User user = enrollment.getUser();
                    String departmentName = user.getDepartment() != null ? user.getDepartment().getName() : null;
                    
                    return EnrolledLearnerResponse.builder()
                            .enrollmentId(enrollment.getId())
                            .userId(user.getId())
                            .name(user.getFullName())
                            .email(user.getEmail())
                            .department(departmentName)
                            .status(enrollment.getStatus().getLabel())
                            .progressPercentage(enrollment.getProgressPercentage())
                            .enrolledDate(enrollment.getEnrolledDate())
                            .completedDate(enrollment.getCompletedDate())
                            .build();
                })
                .collect(Collectors.toList());

        // Build response with pagination
        Map<String, Object> response = new HashMap<>();
        response.put("totalLearners", totalLearners != null ? totalLearners : 0L);
        response.put("completedCount", completedCount != null ? completedCount : 0L);
        response.put("inProgressCount", inProgressCount != null ? inProgressCount : 0L);
        response.put("pendingCount", pendingCount != null ? pendingCount : 0L);
        response.put("learners", learnerResponses);
        response.put("totalElements", enrollmentsPage.getTotalElements());
        response.put("totalPages", enrollmentsPage.getTotalPages());
        response.put("currentPage", enrollmentsPage.getNumber());
        response.put("pageSize", enrollmentsPage.getSize());
        response.put("hasNext", enrollmentsPage.hasNext());
        response.put("hasPrevious", enrollmentsPage.hasPrevious());

        return response;
    }
}

