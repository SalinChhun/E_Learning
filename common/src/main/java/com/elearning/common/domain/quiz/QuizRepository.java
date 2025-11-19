package com.elearning.common.domain.quiz;

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
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByCourseId(Long courseId);
    List<Quiz> findByCourseIdAndStatus(Long courseId, Status status);
    Optional<Quiz> findByIdAndStatus(Long id, Status status);
    
    @Query(value = """
        SELECT q.* FROM tb_quiz q
        INNER JOIN tb_course c ON c.id = q.course_id
        WHERE (COALESCE(:status, '') = '' OR q.status = CAST(:status AS char))
        AND (:courseId IS NULL OR q.course_id = :courseId)
        AND (COALESCE(:searchValue, '') = '' OR 
             LOWER(q.title) LIKE LOWER(CONCAT('%', :searchValue, '%')) OR 
             LOWER(q.description) LIKE LOWER(CONCAT('%', :searchValue, '%')))
        """,
        countQuery = """
        SELECT COUNT(q.id) FROM tb_quiz q
        INNER JOIN tb_course c ON c.id = q.course_id
        WHERE (COALESCE(:status, '') = '' OR q.status = CAST(:status AS char))
        AND (:courseId IS NULL OR q.course_id = :courseId)
        AND (COALESCE(:searchValue, '') = '' OR 
             LOWER(q.title) LIKE LOWER(CONCAT('%', :searchValue, '%')) OR 
             LOWER(q.description) LIKE LOWER(CONCAT('%', :searchValue, '%')))
        """,
        nativeQuery = true)
    Page<Quiz> findQuizzes(
            @Param("status") String status,
            @Param("courseId") Long courseId,
            @Param("searchValue") String searchValue,
            Pageable pageable
    );
}

