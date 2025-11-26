package com.elearning.api.service.course;

import com.elearning.api.payload.course.*;
import com.elearning.common.domain.certificate.CertificateTemplate;
import com.elearning.common.domain.certificate.CertificateTemplateRepository;
import com.elearning.common.domain.course.Course;
import com.elearning.common.domain.course.CourseCategory;
import com.elearning.common.domain.course.CourseCategoryRepository;
import com.elearning.common.domain.course.CourseEnrollment;
import com.elearning.common.domain.course.CourseEnrollmentRepository;
import com.elearning.common.domain.course.CourseRepository;
import com.elearning.common.domain.course.Lesson;
import com.elearning.common.domain.course.LessonRepository;
import com.elearning.common.domain.quiz.Quiz;
import com.elearning.common.domain.quiz.QuizAttempt;
import com.elearning.common.domain.quiz.QuizAttemptRepository;
import com.elearning.common.domain.quiz.QuizRepository;
import com.elearning.common.domain.user.User;
import com.elearning.common.domain.user.UserRepository;
import com.elearning.common.enums.AssignmentType;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CourseCategoryRepository courseCategoryRepository;
    private final CourseEnrollmentRepository courseEnrollmentRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;
    private final CertificateTemplateRepository certificateTemplateRepository;
    private final QuizRepository quizRepository;
    private final QuizAttemptRepository quizAttemptRepository;

    @Override
    @Transactional(readOnly = true)
    public Object getCourses(String searchValue, Long categoryId, String status, Long userId, Pageable pageable) {
        Page<Course> coursesPage = courseRepository.findCourses(
                status,
                Status.NORMAL,
                categoryId,
                searchValue,
                pageable
        );

        List<CourseResponse> courseResponses = coursesPage.getContent().stream()
                .map(course -> {
                    Long learnerCount = courseRepository.countEnrollmentsByCourseId(course.getId());
                    
                    // Check enrollment status with current user
                    Optional<CourseEnrollment> enrollment = courseEnrollmentRepository.findByCourseAndUser_Id(course, userId);

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
                            .videoUrl(course.getVideoUrl())
                            .assignmentType(course.getAssignmentType() != null ? course.getAssignmentType().getValue() : null)
                            .learnerCount(learnerCount != null ? learnerCount : 0L)
                            .enableCertificate(course.getEnableCertificate() != null ? course.getEnableCertificate() : false)
                            .certificateTemplateId(course.getCertificateTemplate() != null ? course.getCertificateTemplate().getId() : null)
                            .certificateTemplateName(course.getCertificateTemplate() != null ? course.getCertificateTemplate().getName() : null)
                            .enrollmentStatus(enrollment.isPresent())
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

        // Calculate statistics from all enrollments (not filtered)
        Long totalCourses = courseEnrollmentRepository.countByUserId(userId);
        Long inProgress = courseEnrollmentRepository.countByUserIdAndStatus(userId, EnrollmentStatus.IN_PROGRESS);
        Long completed = courseEnrollmentRepository.countByUserIdAndStatus(userId, EnrollmentStatus.COMPLETED);
        Long certificates = courseEnrollmentRepository.countCertificates(userId, EnrollmentStatus.COMPLETED);

        Map<String, Object> response = new HashMap<>();
        response.put("courses", courseResponses);
        response.put("total", (long) courseResponses.size());
        response.put("totalCourses", totalCourses != null ? totalCourses : 0L);
        response.put("inProgress", inProgress != null ? inProgress : 0L);
        response.put("completed", completed != null ? completed : 0L);
        response.put("certificates", certificates != null ? certificates : 0L);
        
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public Object getMyCoursesSummary(Long userId) {
        Long totalCourses = courseEnrollmentRepository.countByUserId(userId);
        Long inProgress = courseEnrollmentRepository.countByUserIdAndStatus(userId, EnrollmentStatus.IN_PROGRESS);
        Long completed = courseEnrollmentRepository.countByUserIdAndStatus(userId, EnrollmentStatus.COMPLETED);
        Long certificates = courseEnrollmentRepository.countCertificates(userId, EnrollmentStatus.COMPLETED);

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

        // Get all learner IDs enrolled in this course
        List<CourseEnrollment> enrollments = courseEnrollmentRepository.findByCourseId(courseId);
        List<Long> learnerIds = enrollments.stream()
                .map(enrollment -> enrollment.getUser().getId())
                .collect(Collectors.toList());

        // Check enrollment status by current user
        Boolean enrollmentStatus = null;
        CourseDetailResponse.EnrollmentInfo enrollmentInfo = null;

        if (userId != null) {
            Optional<CourseEnrollment> enrollment = courseEnrollmentRepository.findByCourseAndUser(
                    course, userRepository.findById(userId).orElse(null));
            if (enrollment.isPresent()) {
                CourseEnrollment ce = enrollment.get();
                enrollmentStatus = true;
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
                .videoUrl(course.getVideoUrl())
                .courseContent(course.getCourseContent())
                .assignmentType(course.getAssignmentType() != null ? course.getAssignmentType().getValue() : null)
                .learnerCount(learnerCount != null ? learnerCount : 0L)
                .learners(learnerIds)
                .enableCertificate(course.getEnableCertificate() != null ? course.getEnableCertificate() : false)
                .certificateTemplateId(course.getCertificateTemplate() != null ? course.getCertificateTemplate().getId() : null)
                .certificateTemplateName(course.getCertificateTemplate() != null ? course.getCertificateTemplate().getName() : null)
                .enrollmentStatus(enrollmentStatus)
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

        AssignmentType assignmentType = null;
        if (request.getAssignmentType() != null && !request.getAssignmentType().isEmpty()) {
            assignmentType = AssignmentType.fromValue(request.getAssignmentType());
        }

        CourseStatus courseStatus = CourseStatus.DRAFT; // Default to DRAFT
        if (request.getStatus() != null && !request.getStatus().isEmpty()) {
            CourseStatus statusFromRequest = CourseStatus.fromValue(request.getStatus());
            if (statusFromRequest != null) {
                courseStatus = statusFromRequest;
            }
        }

        // Handle certificate template
        Boolean enableCertificate = request.getEnableCertificate() != null ? request.getEnableCertificate() : false;
        CertificateTemplate certificateTemplate = null;
        
        if (enableCertificate) {
            if (request.getCertificateTemplateId() == null) {
                throw new BusinessException(StatusCode.CERTIFICATE_TEMPLATE_ID_REQUIRED);
            }
            certificateTemplate = certificateTemplateRepository.findById(request.getCertificateTemplateId())
                    .orElseThrow(() -> new BusinessException(StatusCode.NOT_FOUND));
        }

        Course course = Course.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(category)
                .durationHours(request.getDurationHours())
                .estimatedDays(request.getEstimatedDays())
                .dueDate(request.getDueDate())
                .status(courseStatus)
                .isPublic(request.getIsPublic() != null ? request.getIsPublic() : false)
                .imageUrl(request.getImageUrl())
                .videoUrl(request.getVideoUrl())
                .courseContent(request.getCourseContent())
                .assignmentType(assignmentType)
                .enableCertificate(enableCertificate)
                .certificateTemplate(certificateTemplate)
                .build();

        course = courseRepository.save(course);

        // Enroll users if learners are provided
        if (request.getLearners() != null && !request.getLearners().isEmpty()) {
            List<User> users = userRepository.findAllById(request.getLearners());
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

            if (!enrollments.isEmpty()) {
                courseEnrollmentRepository.saveAll(enrollments);
            }
        }

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
                .videoUrl(course.getVideoUrl())
                .courseContent(course.getCourseContent())
                .assignmentType(course.getAssignmentType() != null ? course.getAssignmentType().getValue() : null)
                .enableCertificate(course.getEnableCertificate() != null ? course.getEnableCertificate() : false)
                .certificateTemplateId(course.getCertificateTemplate() != null ? course.getCertificateTemplate().getId() : null)
                .certificateTemplateName(course.getCertificateTemplate() != null ? course.getCertificateTemplate().getName() : null)
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
        course.setVideoUrl(request.getVideoUrl());
        course.setCourseContent(request.getCourseContent());
        if (request.getAssignmentType() != null && !request.getAssignmentType().isEmpty()) {
            AssignmentType assignmentType = AssignmentType.fromValue(request.getAssignmentType());
            course.setAssignmentType(assignmentType);
        } else {
            course.setAssignmentType(null);
        }
        if (request.getStatus() != null && !request.getStatus().isEmpty()) {
            CourseStatus courseStatus = CourseStatus.fromValue(request.getStatus());
            if (courseStatus != null) {
                course.setStatus(courseStatus);
            }
        }

        // Handle certificate template
        if (request.getEnableCertificate() != null) {
            course.setEnableCertificate(request.getEnableCertificate());
            
            if (request.getEnableCertificate()) {
                if (request.getCertificateTemplateId() == null) {
                    throw new BusinessException(StatusCode.CERTIFICATE_TEMPLATE_ID_REQUIRED);
                }
                CertificateTemplate certificateTemplate = certificateTemplateRepository.findById(request.getCertificateTemplateId())
                        .orElseThrow(() -> new BusinessException(StatusCode.NOT_FOUND));
                course.setCertificateTemplate(certificateTemplate);
            } else {
                // If disabling certificate, remove the template
                course.setCertificateTemplate(null);
            }
        } else if (request.getCertificateTemplateId() != null) {
            // If only certificateTemplateId is provided without enableCertificate, enable it
            CertificateTemplate certificateTemplate = certificateTemplateRepository.findById(request.getCertificateTemplateId())
                    .orElseThrow(() -> new BusinessException(StatusCode.NOT_FOUND));
            course.setEnableCertificate(true);
            course.setCertificateTemplate(certificateTemplate);
        }

        course = courseRepository.save(course);

        // Update user enrollments if learners are provided
        // This replaces the existing enrollments with the new list
        if (request.getLearners() != null) {
            // Get all current enrollments for this course
            List<CourseEnrollment> existingEnrollments = courseEnrollmentRepository.findByCourseId(courseId);
            
            // Get the set of user IDs that should be enrolled
            List<Long> requestedUserIds = request.getLearners();
            Set<Long> requestedUserIdSet = requestedUserIds != null ? new HashSet<>(requestedUserIds) : new HashSet<>();
            
            // Find enrollments to remove (users not in the new list)
            List<CourseEnrollment> enrollmentsToRemove = existingEnrollments.stream()
                    .filter(enrollment -> !requestedUserIdSet.contains(enrollment.getUser().getId()))
                    .collect(Collectors.toList());
            
            // Delete enrollments that are no longer in the list
            if (!enrollmentsToRemove.isEmpty()) {
                courseEnrollmentRepository.deleteAll(enrollmentsToRemove);
            }
            
            // Add new enrollments for users in the list who aren't already enrolled
            if (requestedUserIds != null && !requestedUserIds.isEmpty()) {
                List<User> users = userRepository.findAllById(requestedUserIds);
                List<CourseEnrollment> enrollmentsToAdd = new ArrayList<>();
                
                // Get set of user IDs that remain enrolled after deletion
                // (users that were in existing enrollments and are also in the new list)
                Set<Long> remainingEnrolledUserIds = existingEnrollments.stream()
                        .filter(enrollment -> requestedUserIdSet.contains(enrollment.getUser().getId()))
                        .map(enrollment -> enrollment.getUser().getId())
                        .collect(Collectors.toSet());
                
                for (User user : users) {
                    // Only add if not already enrolled (not in the remaining enrollments)
                    if (!remainingEnrolledUserIds.contains(user.getId())) {
                        CourseEnrollment enrollment = CourseEnrollment.builder()
                                .course(course)
                                .user(user)
                                .status(EnrollmentStatus.PENDING)
                                .progressPercentage(0)
                                .enrolledDate(Instant.now())
                                .timeSpentSeconds(0L)
                                .build();
                        enrollmentsToAdd.add(enrollment);
                    }
                }
                
                if (!enrollmentsToAdd.isEmpty()) {
                    courseEnrollmentRepository.saveAll(enrollmentsToAdd);
                }
            }
        }

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
                .videoUrl(course.getVideoUrl())
                .courseContent(course.getCourseContent())
                .assignmentType(course.getAssignmentType() != null ? course.getAssignmentType().getValue() : null)
                .enableCertificate(course.getEnableCertificate() != null ? course.getEnableCertificate() : false)
                .certificateTemplateId(course.getCertificateTemplate() != null ? course.getCertificateTemplate().getId() : null)
                .certificateTemplateName(course.getCertificateTemplate() != null ? course.getCertificateTemplate().getName() : null)
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
                .totalScore(0)
                .percentageScore(0.0)
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
                .totalScore(0)
                .percentageScore(0.0)
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

    @Override
    @Transactional(readOnly = true)
    public Object checkEnrollment(Long courseId, Long userId) {
        // Verify course exists
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(StatusCode.COURSE_NOT_FOUND));

        // Verify user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(StatusCode.USER_NOT_FOUND));

        // Check if user is enrolled
        Optional<CourseEnrollment> enrollment = courseEnrollmentRepository.findByCourseAndUser(course, user);

        if (enrollment.isPresent()) {
            CourseEnrollment ce = enrollment.get();
            return EnrollmentCheckResponse.builder()
                    .isEnrolled(true)
                    .enrollmentId(ce.getId())
                    .status(ce.getStatus().getLabel())
                    .progressPercentage(ce.getProgressPercentage())
                    .timeSpentSeconds(ce.getTimeSpentSeconds())
                    .enrolledDate(ce.getEnrolledDate())
                    .completedDate(ce.getCompletedDate())
                    .build();
        } else {
            return EnrollmentCheckResponse.builder()
                    .isEnrolled(false)
                    .build();
        }
    }

    @Override
    @Transactional
    public Object deleteEnrollment(Long enrollmentId) {
        CourseEnrollment enrollment = courseEnrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new BusinessException(StatusCode.ENROLLMENT_NOT_FOUND));

        Long courseId = enrollment.getCourse().getId();
        Long userId = enrollment.getUser().getId();
        
        courseEnrollmentRepository.delete(enrollment);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Enrollment deleted successfully");
        response.put("enrollmentId", enrollmentId);
        response.put("courseId", courseId);
        response.put("userId", userId);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public Object getMyCourseById(Long courseId, Long userId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(StatusCode.COURSE_NOT_FOUND));

        // Check if user is enrolled
        Optional<CourseEnrollment> enrollmentOpt = courseEnrollmentRepository.findByCourseAndUser(
                course, userRepository.findById(userId).orElse(null));
        
        if (enrollmentOpt.isEmpty()) {
            throw new BusinessException(StatusCode.ENROLLMENT_NOT_FOUND);
        }

        CourseEnrollment enrollment = enrollmentOpt.get();

        // Calculate total score and percentage score from all quiz attempts in this course
        List<Quiz> quizzes = quizRepository.findByCourseIdAndStatus(courseId, Status.NORMAL);
        int totalScoreSum = 0;
        int totalPointsSum = 0;
        String examAttemptStatus = null;

        // Collect all quiz attempts from all quizzes in this course
        List<QuizAttempt> allAttempts = new java.util.ArrayList<>();
        for (Quiz quiz : quizzes) {
            List<QuizAttempt> attempts = quizAttemptRepository.findByQuizIdAndUserId(quiz.getId(), userId);
            allAttempts.addAll(attempts);
        }

        // Find the last PASSED attempt first (prioritize passed attempts)
        QuizAttempt lastPassedAttempt = allAttempts.stream()
                .filter(attempt -> attempt.getCompletedAt() != null 
                        && attempt.getIsPassed() != null 
                        && attempt.getIsPassed() == true)
                .max((a1, a2) -> a2.getCompletedAt().compareTo(a1.getCompletedAt()))
                .orElse(null);

        // If no passed attempt found, use the last attempt (even if failed)
        QuizAttempt lastAttempt = lastPassedAttempt;
        if (lastAttempt == null) {
            lastAttempt = allAttempts.stream()
                    .filter(attempt -> attempt.getCompletedAt() != null)
                    .max((a1, a2) -> a2.getCompletedAt().compareTo(a1.getCompletedAt()))
                    .orElse(null);
        }

        // Determine exam attempt status based on the selected attempt
        if (lastAttempt != null && lastAttempt.getIsPassed() != null) {
            examAttemptStatus = lastAttempt.getIsPassed() ? "passed" : "failed";
        }

        // Calculate scores using passed attempts or best attempts
        for (Quiz quiz : quizzes) {
            List<QuizAttempt> attempts = quizAttemptRepository.findByQuizIdAndUserId(quiz.getId(), userId);
            // Get the last attempt where isPassed == true, or fall back to best attempt
            QuizAttempt selectedAttempt = attempts.stream()
                    .filter(attempt -> attempt.getCompletedAt() != null 
                            && attempt.getIsPassed() != null 
                            && attempt.getIsPassed() == true
                            && attempt.getScore() != null 
                            && attempt.getTotalPoints() != null)
                    .max((a1, a2) -> a2.getCompletedAt().compareTo(a1.getCompletedAt())) // Latest passed attempt
                    .orElse(null);
            
            // If no passed attempt found, fall back to best attempt (highest score)
            if (selectedAttempt == null) {
                selectedAttempt = attempts.stream()
                        .filter(attempt -> attempt.getCompletedAt() != null 
                                && attempt.getScore() != null 
                                && attempt.getTotalPoints() != null)
                        .max((a1, a2) -> {
                            // Compare by score first, then by completion date (latest)
                            int scoreCompare = Integer.compare(a2.getScore(), a1.getScore());
                            if (scoreCompare != 0) return scoreCompare;
                            return a2.getCompletedAt().compareTo(a1.getCompletedAt());
                        })
                        .orElse(null);
            }
            
            if (selectedAttempt != null) {
                totalScoreSum += selectedAttempt.getScore() != null ? selectedAttempt.getScore() : 0;
                totalPointsSum += selectedAttempt.getTotalPoints() != null ? selectedAttempt.getTotalPoints() : 0;
            }
        }

        Integer totalScore = totalScoreSum;
        Double percentageScore = totalPointsSum > 0 ? (double) totalScoreSum / totalPointsSum * 100 : 0.0;

        return MyCourseDetailResponse.builder()
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
                .videoUrl(course.getVideoUrl())
                .courseContent(course.getCourseContent())
                .totalScore(totalScore)
                .percentageScore(percentageScore)
                .examAttemptStatus(examAttemptStatus)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Object getEnrollments(Long courseId, Long userId, String status, Pageable pageable) {
        Page<CourseEnrollment> enrollmentsPage = courseEnrollmentRepository.findEnrollments(
                courseId,
                userId,
                status,
                pageable
        );

        List<EnrollmentResponse> enrollmentResponses = enrollmentsPage.getContent().stream()
                .map(enrollment -> {
                    Course course = enrollment.getCourse();
                    User user = enrollment.getUser();
                    String departmentName = user.getDepartment() != null ? user.getDepartment().getName() : null;
                    String categoryName = course.getCategory() != null ? course.getCategory().getName() : null;
                    
                    return EnrollmentResponse.builder()
                            .enrollmentId(enrollment.getId())
                            .courseId(course.getId())
                            .courseTitle(course.getTitle())
                            .courseDescription(course.getDescription())
                            .courseCategory(categoryName)
                            .userId(user.getId())
                            .userName(user.getFullName())
                            .userEmail(user.getEmail())
                            .department(departmentName)
                            .status(enrollment.getStatus().getLabel())
                            .progressPercentage(enrollment.getProgressPercentage())
                            .timeSpentSeconds(enrollment.getTimeSpentSeconds())
                            .enrolledDate(enrollment.getEnrolledDate())
                            .completedDate(enrollment.getCompletedDate())
                            .createdAt(enrollment.getCreatedAt())
//                            .updatedAt(enrollment.getUpdatedAt())
                            .build();
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("enrollments", enrollmentResponses);
        response.put("totalElements", enrollmentsPage.getTotalElements());
        response.put("totalPages", enrollmentsPage.getTotalPages());
        response.put("currentPage", enrollmentsPage.getNumber());
        response.put("pageSize", enrollmentsPage.getSize());
        response.put("hasNext", enrollmentsPage.hasNext());
        response.put("hasPrevious", enrollmentsPage.hasPrevious());

        return response;
    }
}

