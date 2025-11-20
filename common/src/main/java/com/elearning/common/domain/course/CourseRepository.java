package com.elearning.common.domain.course;

import com.elearning.common.enums.CourseStatus;
import com.elearning.common.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    
    @Query(value = """
        SELECT c.* FROM tb_course c
        INNER JOIN tb_course_category cat ON cat.id = c.category_id
        WHERE (COALESCE(:status, '') = '' OR c.status = CAST(:status AS char))
        AND cat.status = CAST(:#{#categoryStatus.getValue()} AS char)
        AND (:categoryId IS NULL OR c.category_id = :categoryId)
        AND (COALESCE(:searchValue, '') = '' OR 
             LOWER(c.title) LIKE LOWER(CONCAT('%', :searchValue, '%')) OR 
             LOWER(c.description) LIKE LOWER(CONCAT('%', :searchValue, '%')))
        """,
        countQuery = """
        SELECT COUNT(c.id) FROM tb_course c
        INNER JOIN tb_course_category cat ON cat.id = c.category_id
        WHERE (COALESCE(:status, '') = '' OR c.status = CAST(:status AS char))
        AND cat.status = CAST(:#{#categoryStatus.getValue()} AS char)
        AND (:categoryId IS NULL OR c.category_id = :categoryId)
        AND (COALESCE(:searchValue, '') = '' OR 
             LOWER(c.title) LIKE LOWER(CONCAT('%', :searchValue, '%')) OR 
             LOWER(c.description) LIKE LOWER(CONCAT('%', :searchValue, '%')))
        """,
        nativeQuery = true)
    Page<Course> findCourses(
            @Param("status") String status,
            @Param("categoryStatus") Status categoryStatus,
            @Param("categoryId") Long categoryId,
            @Param("searchValue") String searchValue,
            Pageable pageable
    );

    @Query("SELECT COUNT(ce) FROM CourseEnrollment ce WHERE ce.course.id = :courseId")
    Long countEnrollmentsByCourseId(@Param("courseId") Long courseId);

    Optional<Course> findByIdAndStatusAndIsPublic(Long id, CourseStatus status, Boolean isPublic);
    
    List<Course> findByCertificateTemplateId(Long certificateTemplateId);
    
    Long countByCertificateTemplateIsNotNull();
}

