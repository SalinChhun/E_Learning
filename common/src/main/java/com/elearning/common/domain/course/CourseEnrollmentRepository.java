package com.elearning.common.domain.course;

import com.elearning.common.domain.user.User;
import com.elearning.common.enums.EnrollmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, Long> {
    Optional<CourseEnrollment> findByCourseAndUser(Course course, User user);
    
    List<CourseEnrollment> findByCourseId(Long courseId);
    
    Page<CourseEnrollment> findByCourseId(Long courseId, Pageable pageable);
    
    Page<CourseEnrollment> findByCourseIdAndStatus(Long courseId, EnrollmentStatus status, Pageable pageable);
    
    List<CourseEnrollment> findByUserId(Long userId);
    
    List<CourseEnrollment> findByUserIdAndStatus(Long userId, EnrollmentStatus status);
    
    @Query("SELECT COUNT(ce) FROM CourseEnrollment ce WHERE ce.course.id = :courseId AND ce.status = :status")
    Long countByCourseIdAndStatus(@Param("courseId") Long courseId, @Param("status") EnrollmentStatus status);
    
    @Query("SELECT COUNT(ce) FROM CourseEnrollment ce WHERE ce.course.id = :courseId")
    Long countByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT COUNT(ce) FROM CourseEnrollment ce WHERE ce.user.id = :userId")
    Long countByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(ce) FROM CourseEnrollment ce WHERE ce.user.id = :userId AND ce.status = :status")
    Long countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") EnrollmentStatus status);
    
    @Query("SELECT COUNT(ce) FROM CourseEnrollment ce WHERE ce.user.id = :userId AND ce.status = :status AND ce.completedDate IS NOT NULL")
    Long countCompletedWithCertificate(@Param("userId") Long userId, @Param("status") EnrollmentStatus status);
}

