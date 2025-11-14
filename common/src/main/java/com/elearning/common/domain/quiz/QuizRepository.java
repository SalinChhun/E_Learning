package com.elearning.common.domain.quiz;

import com.elearning.common.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByCourseId(Long courseId);
    List<Quiz> findByCourseIdAndStatus(Long courseId, Status status);
    Optional<Quiz> findByIdAndStatus(Long id, Status status);
}

