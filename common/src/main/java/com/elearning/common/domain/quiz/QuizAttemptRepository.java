package com.elearning.common.domain.quiz;

import com.elearning.common.domain.user.User;
import com.elearning.common.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
    List<QuizAttempt> findByQuizId(Long quizId);
    List<QuizAttempt> findByUserId(Long userId);
    List<QuizAttempt> findByQuizIdAndUserId(Long quizId, Long userId);
    Optional<QuizAttempt> findFirstByQuizIdAndUserIdOrderByCreatedAtDesc(Long quizId, Long userId);
    List<QuizAttempt> findByQuizIdAndStatus(Long quizId, Status status);
}

