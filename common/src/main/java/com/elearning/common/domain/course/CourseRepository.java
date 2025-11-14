package com.elearning.common.domain.course;

import com.elearning.common.enums.CourseStatus;
import com.elearning.common.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    
    @Query("SELECT c FROM Course c WHERE " +
           "c.status = :status AND " +
           "c.isPublic = true AND " +
           "c.category.status = :categoryStatus AND " +
           "(:categoryId IS NULL OR c.category.id = :categoryId) AND " +
           "(:searchValue IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', :searchValue, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :searchValue, '%')))")
    Page<Course> findPublicCourses(
            @Param("status") CourseStatus status,
            @Param("categoryStatus") Status categoryStatus,
            @Param("categoryId") Long categoryId,
            @Param("searchValue") String searchValue,
            Pageable pageable
    );

    @Query("SELECT COUNT(ce) FROM CourseEnrollment ce WHERE ce.course.id = :courseId")
    Long countEnrollmentsByCourseId(@Param("courseId") Long courseId);

    Optional<Course> findByIdAndStatusAndIsPublic(Long id, CourseStatus status, Boolean isPublic);
}

