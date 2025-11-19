package com.elearning.api.service.quiz;

import com.elearning.api.payload.quiz.QuestionRequest;
import com.elearning.api.payload.quiz.QuizAttemptRequest;
import com.elearning.api.payload.quiz.QuizRequest;
import org.springframework.data.domain.Pageable;

public interface QuizService {
    Object createQuiz(QuizRequest request);
    Object updateQuiz(Long quizId, QuizRequest request);
    Object getQuizById(Long quizId);
    Object getQuizzes(String searchValue, Long courseId, String status, Pageable pageable);
    Object getQuizzesByCourseId(Long courseId);
    Object deleteQuiz(Long quizId);
    Object addQuestion(Long quizId, QuestionRequest request);
    Object updateQuestion(Long questionId, QuestionRequest request);
    Object deleteQuestion(Long questionId);
    Object startQuizAttempt(Long quizId, Long userId);
    Object getQuizForTaking(Long quizId, Long userId);
    Object submitQuizAttempt(QuizAttemptRequest request, Long userId);
    Object getQuizAttempts(Long quizId, Pageable pageable);
    Object getMyQuizAttempts(Long userId);
}

