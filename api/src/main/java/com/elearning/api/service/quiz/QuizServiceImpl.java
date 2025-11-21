package com.elearning.api.service.quiz;

import com.elearning.api.payload.quiz.*;
import com.elearning.common.domain.course.Course;
import com.elearning.common.domain.course.CourseEnrollment;
import com.elearning.common.domain.course.CourseEnrollmentRepository;
import com.elearning.common.domain.course.CourseRepository;
import com.elearning.common.domain.quiz.*;
import com.elearning.common.domain.user.User;
import com.elearning.common.domain.user.UserRepository;
import com.elearning.common.enums.EnrollmentStatus;
import com.elearning.common.enums.QuizType;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final QuestionOptionRepository questionOptionRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final QuizAttemptAnswerRepository quizAttemptAnswerRepository;
    private final CourseRepository courseRepository;
    private final CourseEnrollmentRepository courseEnrollmentRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Object createQuiz(QuizRequest request) {
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new BusinessException(StatusCode.COURSE_NOT_FOUND));

        QuizType quizType = QuizType.fromValue(request.getType());
        if (quizType == null) {
            quizType = QuizType.QUIZ;
        }

        Quiz quiz = Quiz.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .type(quizType)
                .course(course)
                .durationMinutes(request.getDurationMinutes())
                .passingScore(request.getPassingScore())
                .status(Status.NORMAL)
                .build();

        quiz = quizRepository.save(quiz);

        return QuizResponse.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .description(quiz.getDescription())
                .type(quiz.getType().getLabel())
                .courseId(quiz.getCourse().getId())
                .courseTitle(quiz.getCourse().getTitle())
                .durationMinutes(quiz.getDurationMinutes())
                .passingScore(quiz.getPassingScore())
                .status(quiz.getStatus().getLabel())
                .createdAt(quiz.getCreatedAt())
                .updatedAt(quiz.getUpdateAt())
                .build();
    }

    @Override
    @Transactional
    public Object updateQuiz(Long quizId, QuizRequest request) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new BusinessException(StatusCode.QUIZ_NOT_FOUND));

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new BusinessException(StatusCode.COURSE_NOT_FOUND));

        QuizType quizType = QuizType.fromValue(request.getType());
        if (quizType != null) {
            quiz.setType(quizType);
        }

        quiz.setTitle(request.getTitle());
        quiz.setDescription(request.getDescription());
        quiz.setCourse(course);
        quiz.setDurationMinutes(request.getDurationMinutes());
        quiz.setPassingScore(request.getPassingScore());

        quiz = quizRepository.save(quiz);

        // Update questions if provided
        if (request.getQuestions() != null) {
            // Get all existing questions for this quiz
            List<Question> existingQuestions = questionRepository.findByQuizIdOrderByOrderSequenceAsc(quizId);
            
            // Get the set of question IDs from the request
            Set<Long> requestedQuestionIds = request.getQuestions().stream()
                    .filter(q -> q.getId() != null)
                    .map(QuestionRequest::getId)
                    .collect(Collectors.toSet());
            
            // Find questions to remove (questions not in the new list)
            List<Question> questionsToRemove = existingQuestions.stream()
                    .filter(question -> !requestedQuestionIds.contains(question.getId()))
                    .collect(Collectors.toList());
            
            // Delete questions that are no longer in the list
            // First delete all options for these questions, then delete the questions
            if (!questionsToRemove.isEmpty()) {
                for (Question questionToRemove : questionsToRemove) {
                    // Delete all options for this question first
                    List<QuestionOption> optionsToDelete = questionOptionRepository.findByQuestionIdOrderByOrderSequenceAsc(questionToRemove.getId());
                    if (!optionsToDelete.isEmpty()) {
                        questionOptionRepository.deleteAll(optionsToDelete);
                    }
                }
                // Now delete the questions
                questionRepository.deleteAll(questionsToRemove);
            }
            
            // Update or create questions
            int orderSequence = 1;
            for (QuestionRequest questionRequest : request.getQuestions()) {
                Question question;
                
                if (questionRequest.getId() != null) {
                    // Update existing question
                    question = questionRepository.findById(questionRequest.getId())
                            .orElseThrow(() -> new BusinessException(StatusCode.QUESTION_NOT_FOUND));
                    
                    // Verify the question belongs to this quiz
                    if (!question.getQuiz().getId().equals(quizId)) {
                        throw new BusinessException(StatusCode.QUESTION_NOT_FOUND);
                    }
                    
                    question.setQuestionText(questionRequest.getQuestionText());
                    question.setQuestionType(questionRequest.getQuestionType());
                    if (questionRequest.getPoints() != null) {
                        question.setPoints(questionRequest.getPoints());
                    }
                    question.setAnswerExplanation(questionRequest.getAnswerExplanation());
                    question.setOrderSequence(questionRequest.getOrderSequence() != null ? 
                            questionRequest.getOrderSequence() : orderSequence++);
                    question.setImageUrl(questionRequest.getImageUrl());
                    question.setVideoUrl(questionRequest.getVideoUrl());
                    question.setFileUrl(questionRequest.getFileUrl());
                    question.setVoiceUrl(questionRequest.getVoiceUrl());
                } else {
                    // Create new question
                    question = Question.builder()
                            .quiz(quiz)
                            .questionText(questionRequest.getQuestionText())
                            .questionType(questionRequest.getQuestionType())
                            .points(questionRequest.getPoints() != null ? questionRequest.getPoints() : 10)
                            .answerExplanation(questionRequest.getAnswerExplanation())
                            .orderSequence(questionRequest.getOrderSequence() != null ? 
                                    questionRequest.getOrderSequence() : orderSequence++)
                            .imageUrl(questionRequest.getImageUrl())
                            .videoUrl(questionRequest.getVideoUrl())
                            .fileUrl(questionRequest.getFileUrl())
                            .voiceUrl(questionRequest.getVoiceUrl())
                            .build();
                }
                
                question = questionRepository.save(question);
                
                // Update options for the question
                if (questionRequest.getOptions() != null) {
                    // Delete existing options
                    List<QuestionOption> existingOptions = questionOptionRepository.findByQuestionIdOrderByOrderSequenceAsc(question.getId());
                    questionOptionRepository.deleteAll(existingOptions);
                    
                    // Add new options
                    int optionSequence = 1;
                    for (QuestionOptionRequest optionRequest : questionRequest.getOptions()) {
                        QuestionOption option = QuestionOption.builder()
                                .question(question)
                                .optionText(optionRequest.getOptionText())
                                .isCorrect(optionRequest.getIsCorrect() != null ? optionRequest.getIsCorrect() : false)
                                .orderSequence(optionRequest.getOrderSequence() != null ? 
                                        optionRequest.getOrderSequence() : optionSequence++)
                                .build();
                        questionOptionRepository.save(option);
                    }
                }
            }
        }

        return QuizResponse.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .description(quiz.getDescription())
                .type(quiz.getType().getLabel())
                .courseId(quiz.getCourse().getId())
                .courseTitle(quiz.getCourse().getTitle())
                .durationMinutes(quiz.getDurationMinutes())
                .passingScore(quiz.getPassingScore())
                .status(quiz.getStatus().getLabel())
                .createdAt(quiz.getCreatedAt())
                .updatedAt(quiz.getUpdateAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Object getQuizById(Long quizId) {
        Quiz quiz = quizRepository.findByIdAndStatus(quizId, Status.NORMAL)
                .orElseThrow(() -> new BusinessException(StatusCode.QUIZ_NOT_FOUND));

        List<Question> questions = questionRepository.findByQuizIdOrderByOrderSequenceAsc(quizId);
        List<QuestionResponse> questionResponses = questions.stream()
                .map(question -> {
                    List<QuestionOption> options = questionOptionRepository.findByQuestionIdOrderByOrderSequenceAsc(question.getId());
                    List<QuestionOptionResponse> optionResponses = options.stream()
                            .map(option -> QuestionOptionResponse.builder()
                                    .id(option.getId())
                                    .optionText(option.getOptionText())
                                    .isCorrect(option.getIsCorrect())
                                    .orderSequence(option.getOrderSequence())
                                    .build())
                            .collect(Collectors.toList());

                    return QuestionResponse.builder()
                            .id(question.getId())
                            .questionText(question.getQuestionText())
                            .questionType(question.getQuestionType())
                            .points(question.getPoints())
                            .answerExplanation(question.getAnswerExplanation())
                            .orderSequence(question.getOrderSequence())
                            .imageUrl(question.getImageUrl())
                            .videoUrl(question.getVideoUrl())
                            .fileUrl(question.getFileUrl())
                            .voiceUrl(question.getVoiceUrl())
                            .options(optionResponses)
                            .build();
                })
                .collect(Collectors.toList());

        return QuizDetailResponse.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .description(quiz.getDescription())
                .type(quiz.getType().getLabel())
                .courseId(quiz.getCourse().getId())
                .courseTitle(quiz.getCourse().getTitle())
                .durationMinutes(quiz.getDurationMinutes())
                .passingScore(quiz.getPassingScore())
                .status(quiz.getStatus().getLabel())
                .questions(questionResponses)
                .createdAt(quiz.getCreatedAt())
                .updatedAt(quiz.getUpdateAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Object getQuizzes(String searchValue, Long courseId, String status, Pageable pageable) {
        Page<Quiz> quizzesPage = quizRepository.findQuizzes(
                status,
                courseId,
                searchValue,
                pageable
        );

        List<QuizResponse> quizResponses = quizzesPage.getContent().stream()
                .map(quiz -> QuizResponse.builder()
                        .id(quiz.getId())
                        .title(quiz.getTitle())
                        .description(quiz.getDescription())
                        .type(quiz.getType().getLabel())
                        .courseId(quiz.getCourse().getId())
                        .courseTitle(quiz.getCourse().getTitle())
                        .durationMinutes(quiz.getDurationMinutes())
                        .passingScore(quiz.getPassingScore())
                        .status(quiz.getStatus().getLabel())
                        .createdAt(quiz.getCreatedAt())
                        .updatedAt(quiz.getUpdateAt())
                        .build())
                .collect(Collectors.toList());

        Page<QuizResponse> responsePage = new PageImpl<>(
                quizResponses,
                quizzesPage.getPageable(),
                quizzesPage.getTotalElements()
        );

        Map<String, Object> response = new HashMap<>();
        response.put("quizzes", quizResponses);
        response.put("totalElements", quizzesPage.getTotalElements());
        response.put("totalPages", quizzesPage.getTotalPages());
        response.put("currentPage", quizzesPage.getNumber());
        response.put("pageSize", quizzesPage.getSize());
        response.put("hasNext", quizzesPage.hasNext());
        response.put("hasPrevious", quizzesPage.hasPrevious());

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public Object getQuizzesByCourseId(Long courseId) {
        List<Quiz> quizzes = quizRepository.findByCourseIdAndStatus(courseId, Status.NORMAL);
        return quizzes.stream()
                .map(quiz -> {
                    // Fetch questions for this quiz
                    List<Question> questions = questionRepository.findByQuizIdOrderByOrderSequenceAsc(quiz.getId());
                    List<QuestionResponse> questionResponses = questions.stream()
                            .map(question -> {
                                List<QuestionOption> options = questionOptionRepository.findByQuestionIdOrderByOrderSequenceAsc(question.getId());
                                List<QuestionOptionResponse> optionResponses = options.stream()
                                        .map(option -> QuestionOptionResponse.builder()
                                                .id(option.getId())
                                                .optionText(option.getOptionText())
                                                .isCorrect(option.getIsCorrect())
                                                .orderSequence(option.getOrderSequence())
                                                .build())
                                        .collect(Collectors.toList());

                                return QuestionResponse.builder()
                                        .id(question.getId())
                                        .questionText(question.getQuestionText())
                                        .questionType(question.getQuestionType())
                                        .points(question.getPoints())
                                        .answerExplanation(question.getAnswerExplanation())
                                        .orderSequence(question.getOrderSequence())
                                        .imageUrl(question.getImageUrl())
                                        .videoUrl(question.getVideoUrl())
                                        .fileUrl(question.getFileUrl())
                                        .voiceUrl(question.getVoiceUrl())
                                        .options(optionResponses)
                                        .build();
                            })
                            .collect(Collectors.toList());

                    return QuizResponse.builder()
                            .id(quiz.getId())
                            .title(quiz.getTitle())
                            .description(quiz.getDescription())
                            .type(quiz.getType().getLabel())
                            .courseId(quiz.getCourse().getId())
                            .courseTitle(quiz.getCourse().getTitle())
                            .durationMinutes(quiz.getDurationMinutes())
                            .passingScore(quiz.getPassingScore())
                            .status(quiz.getStatus().getLabel())
                            .questions(questionResponses)
                            .createdAt(quiz.getCreatedAt())
                            .updatedAt(quiz.getUpdateAt())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Object deleteQuiz(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new BusinessException(StatusCode.QUIZ_NOT_FOUND));
        quiz.setStatus(Status.DISABLE);
        quizRepository.save(quiz);
        return Map.of("message", "Quiz deleted successfully", "id", quizId);
    }

    @Override
    @Transactional
    public Object addQuestion(Long quizId, QuestionRequest request) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new BusinessException(StatusCode.QUIZ_NOT_FOUND));

        if (request.getOrderSequence() == null) {
            List<Question> existingQuestions = questionRepository.findByQuizIdOrderByOrderSequenceAsc(quizId);
            request.setOrderSequence(existingQuestions.size() + 1);
        }

        Question question = Question.builder()
                .quiz(quiz)
                .questionText(request.getQuestionText())
                .questionType(request.getQuestionType())
                .points(request.getPoints() != null ? request.getPoints() : 10)
                .answerExplanation(request.getAnswerExplanation())
                .orderSequence(request.getOrderSequence())
                .imageUrl(request.getImageUrl())
                .videoUrl(request.getVideoUrl())
                .fileUrl(request.getFileUrl())
                .voiceUrl(request.getVoiceUrl())
                .build();

        question = questionRepository.save(question);

        // Save options if provided
        if (request.getOptions() != null && !request.getOptions().isEmpty()) {
            int optionSequence = 1;
            for (QuestionOptionRequest optionRequest : request.getOptions()) {
                QuestionOption option = QuestionOption.builder()
                        .question(question)
                        .optionText(optionRequest.getOptionText())
                        .isCorrect(optionRequest.getIsCorrect() != null ? optionRequest.getIsCorrect() : false)
                        .orderSequence(optionRequest.getOrderSequence() != null ? optionRequest.getOrderSequence() : optionSequence++)
                        .build();
                questionOptionRepository.save(option);
            }
        }

        return getQuestionResponse(question);
    }

    @Override
    @Transactional
    public Object updateQuestion(Long questionId, QuestionRequest request) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new BusinessException(StatusCode.QUESTION_NOT_FOUND));

        question.setQuestionText(request.getQuestionText());
        question.setQuestionType(request.getQuestionType());
        if (request.getPoints() != null) {
            question.setPoints(request.getPoints());
        }
        question.setAnswerExplanation(request.getAnswerExplanation());
        if (request.getOrderSequence() != null) {
            question.setOrderSequence(request.getOrderSequence());
        }
        question.setImageUrl(request.getImageUrl());
        question.setVideoUrl(request.getVideoUrl());
        question.setFileUrl(request.getFileUrl());
        question.setVoiceUrl(request.getVoiceUrl());

        question = questionRepository.save(question);

        // Update options
        if (request.getOptions() != null && !request.getOptions().isEmpty()) {
            // Delete existing options
            List<QuestionOption> existingOptions = questionOptionRepository.findByQuestionIdOrderByOrderSequenceAsc(questionId);
            questionOptionRepository.deleteAll(existingOptions);

            // Add new options
            int optionSequence = 1;
            for (QuestionOptionRequest optionRequest : request.getOptions()) {
                QuestionOption option = QuestionOption.builder()
                        .question(question)
                        .optionText(optionRequest.getOptionText())
                        .isCorrect(optionRequest.getIsCorrect() != null ? optionRequest.getIsCorrect() : false)
                        .orderSequence(optionRequest.getOrderSequence() != null ? optionRequest.getOrderSequence() : optionSequence++)
                        .build();
                questionOptionRepository.save(option);
            }
        }

        return getQuestionResponse(question);
    }

    @Override
    @Transactional
    public Object deleteQuestion(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new BusinessException(StatusCode.QUESTION_NOT_FOUND));
        questionRepository.delete(question);
        return Map.of("message", "Question deleted successfully", "id", questionId);
    }

    @Override
    @Transactional
    public Object startQuizAttempt(Long quizId, Long userId) {
        Quiz quiz = quizRepository.findByIdAndStatus(quizId, Status.NORMAL)
                .orElseThrow(() -> new BusinessException(StatusCode.QUIZ_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(StatusCode.USER_NOT_FOUND));

        QuizAttempt attempt = QuizAttempt.builder()
                .quiz(quiz)
                .user(user)
                .startedAt(Instant.now())
                .status(Status.NORMAL)
                .build();

        attempt = quizAttemptRepository.save(attempt);

        return QuizAttemptResponse.builder()
                .id(attempt.getId())
                .quizId(quiz.getId())
                .quizTitle(quiz.getTitle())
                .userId(user.getId())
                .userName(user.getFullName())
                .startedAt(attempt.getStartedAt())
                .status(attempt.getStatus().getLabel())
                .build();
    }

    @Override
    @Transactional
    public Object getQuizForTaking(Long quizId, Long userId) {
        Quiz quiz = quizRepository.findByIdAndStatus(quizId, Status.NORMAL)
                .orElseThrow(() -> new BusinessException(StatusCode.QUIZ_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(StatusCode.USER_NOT_FOUND));

        // Get or create attempt
        QuizAttempt attempt = quizAttemptRepository.findFirstByQuizIdAndUserIdOrderByCreatedAtDesc(quizId, userId)
                .orElse(null);

        if (attempt == null || attempt.getCompletedAt() != null) {
            // Create new attempt if none exists or previous one is completed
            attempt = QuizAttempt.builder()
                    .quiz(quiz)
                    .user(user)
                    .startedAt(Instant.now())
                    .status(Status.NORMAL)
                    .build();
            attempt = quizAttemptRepository.save(attempt);
        } else if (attempt.getStartedAt() == null) {
            // Update start time if not set
            attempt.setStartedAt(Instant.now());
            attempt = quizAttemptRepository.save(attempt);
        }

        // Get questions WITHOUT correct answers and explanations (for taking exam)
        List<Question> questions = questionRepository.findByQuizIdOrderByOrderSequenceAsc(quizId);
        List<QuestionResponse> questionResponses = questions.stream()
                .map(question -> {
                    List<QuestionOption> options = questionOptionRepository.findByQuestionIdOrderByOrderSequenceAsc(question.getId());
                    // Hide isCorrect field when taking exam
                    List<QuestionOptionResponse> optionResponses = options.stream()
                            .map(option -> QuestionOptionResponse.builder()
                                    .id(option.getId())
                                    .optionText(option.getOptionText())
                                    .isCorrect(null) // Hide correct answer
                                    .orderSequence(option.getOrderSequence())
                                    .build())
                            .collect(Collectors.toList());

                    return QuestionResponse.builder()
                            .id(question.getId())
                            .questionText(question.getQuestionText())
                            .questionType(question.getQuestionType())
                            .points(question.getPoints())
                            .answerExplanation(null) // Hide explanation during exam
                            .orderSequence(question.getOrderSequence())
                            .imageUrl(question.getImageUrl())
                            .videoUrl(question.getVideoUrl())
                            .fileUrl(question.getFileUrl())
                            .voiceUrl(question.getVoiceUrl())
                            .options(optionResponses)
                            .build();
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("attemptId", attempt.getId());
        response.put("quizId", quiz.getId());
        response.put("quizTitle", quiz.getTitle());
        response.put("durationMinutes", quiz.getDurationMinutes());
        response.put("passingScore", quiz.getPassingScore());
        response.put("startedAt", attempt.getStartedAt());
        response.put("questions", questionResponses);

        return response;
    }

    @Override
    @Transactional
    public Object submitQuizAttempt(QuizAttemptRequest request, Long userId) {
        Quiz quiz = quizRepository.findByIdAndStatus(request.getQuizId(), Status.NORMAL)
                .orElseThrow(() -> new BusinessException(StatusCode.QUIZ_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(StatusCode.USER_NOT_FOUND));

        // Get or create attempt
        QuizAttempt attempt = quizAttemptRepository.findFirstByQuizIdAndUserIdOrderByCreatedAtDesc(request.getQuizId(), userId)
                .orElse(QuizAttempt.builder()
                        .quiz(quiz)
                        .user(user)
                        .startedAt(Instant.now())
                        .status(Status.NORMAL)
                        .build());

        if (attempt.getStartedAt() == null) {
            attempt.setStartedAt(Instant.now());
        }
        attempt.setCompletedAt(Instant.now());

        // Calculate time spent
        if (attempt.getStartedAt() != null) {
            long timeSpent = attempt.getCompletedAt().getEpochSecond() - attempt.getStartedAt().getEpochSecond();
            attempt.setTimeSpentSeconds(timeSpent);
        }

        // Get all questions for the quiz
        List<Question> questions = questionRepository.findByQuizIdOrderByOrderSequenceAsc(request.getQuizId());
        int totalPoints = questions.stream().mapToInt(Question::getPoints).sum();
        int score = 0;

        // Process answers
        for (AnswerRequest answerRequest : request.getAnswers()) {
            Question question = questions.stream()
                    .filter(q -> q.getId().equals(answerRequest.getQuestionId()))
                    .findFirst()
                    .orElseThrow(() -> new BusinessException(StatusCode.QUESTION_NOT_FOUND));

            boolean isCorrect = false;
            int pointsEarned = 0;

            String questionType = question.getQuestionType();
            
            // Handle MULTIPLE_CHOICE and TRUE_FALSE questions (both use options)
            if (("MULTIPLE_CHOICE".equals(questionType) || "TRUE_FALSE".equals(questionType)) 
                    && answerRequest.getSelectedOptionId() != null) {
                QuestionOption selectedOption = questionOptionRepository.findById(answerRequest.getSelectedOptionId())
                        .orElse(null);
                if (selectedOption != null && selectedOption.getIsCorrect()) {
                    isCorrect = true;
                    pointsEarned = question.getPoints();
                }
            } 
            // Handle SHORT_ANSWER questions (require manual grading)
            else if ("SHORT_ANSWER".equals(questionType) && answerRequest.getAnswerText() != null) {
                // Save the answer text for manual grading
                // For now, set as incorrect and 0 points until manually graded
                isCorrect = false;
                pointsEarned = 0;
            } 
            // Handle ESSAY questions (require manual grading)
            else if ("ESSAY".equals(questionType) && answerRequest.getAnswerText() != null) {
                // Save the answer text for manual grading
                // For now, set as incorrect and 0 points until manually graded
                isCorrect = false;
                pointsEarned = 0;
            }

            QuizAttemptAnswer answer = QuizAttemptAnswer.builder()
                    .attempt(attempt)
                    .question(question)
                    .selectedOption(answerRequest.getSelectedOptionId() != null ?
                            questionOptionRepository.findById(answerRequest.getSelectedOptionId()).orElse(null) : null)
                    .answerText(answerRequest.getAnswerText())
                    .isCorrect(isCorrect)
                    .pointsEarned(pointsEarned)
                    .build();

            quizAttemptAnswerRepository.save(answer);
            score += pointsEarned;
        }

        attempt.setScore(score);
        attempt.setTotalPoints(totalPoints);
        attempt.setPercentageScore(totalPoints > 0 ? (double) score / totalPoints * 100 : 0.0);
        attempt.setIsPassed(quiz.getPassingScore() != null && attempt.getPercentageScore() >= quiz.getPassingScore());

        attempt = quizAttemptRepository.save(attempt);

        // If quiz is passed, update course enrollment status to COMPLETED
        if (attempt.getIsPassed() != null && attempt.getIsPassed()) {
            Course course = quiz.getCourse();
            Optional<CourseEnrollment> enrollment = courseEnrollmentRepository.findByCourseAndUser(course, user);
            if (enrollment.isPresent()) {
                CourseEnrollment courseEnrollment = enrollment.get();
                courseEnrollment.setStatus(EnrollmentStatus.COMPLETED);
                if (courseEnrollment.getCompletedDate() == null) {
                    courseEnrollment.setCompletedDate(Instant.now());
                }
                courseEnrollmentRepository.save(courseEnrollment);
            }
        }

        return QuizAttemptResponse.builder()
                .id(attempt.getId())
                .quizId(quiz.getId())
                .quizTitle(quiz.getTitle())
                .userId(user.getId())
                .userName(user.getFullName())
                .score(attempt.getScore())
                .totalPoints(attempt.getTotalPoints())
                .percentageScore(attempt.getPercentageScore())
                .isPassed(attempt.getIsPassed())
                .startedAt(attempt.getStartedAt())
                .completedAt(attempt.getCompletedAt())
                .timeSpentSeconds(attempt.getTimeSpentSeconds())
                .status(attempt.getStatus().getLabel())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Object getQuizAttempts(Long quizId, Pageable pageable) {
        List<QuizAttempt> attempts = quizAttemptRepository.findByQuizId(quizId);
        List<QuizAttemptResponse> responses = attempts.stream()
                .map(attempt -> QuizAttemptResponse.builder()
                        .id(attempt.getId())
                        .quizId(attempt.getQuiz().getId())
                        .quizTitle(attempt.getQuiz().getTitle())
                        .userId(attempt.getUser().getId())
                        .userName(attempt.getUser().getFullName())
                        .score(attempt.getScore())
                        .totalPoints(attempt.getTotalPoints())
                        .percentageScore(attempt.getPercentageScore())
                        .isPassed(attempt.getIsPassed())
                        .startedAt(attempt.getStartedAt())
                        .completedAt(attempt.getCompletedAt())
                        .timeSpentSeconds(attempt.getTimeSpentSeconds())
                        .status(attempt.getStatus().getLabel())
                        .build())
                .collect(Collectors.toList());

        Page<QuizAttemptResponse> page = new PageImpl<>(responses, pageable, responses.size());
        Map<String, Object> response = new HashMap<>();
        response.put("attempts", page.getContent());
        response.put("totalElements", page.getTotalElements());
        response.put("totalPages", page.getTotalPages());
        response.put("currentPage", page.getNumber());
        response.put("pageSize", page.getSize());
        response.put("hasNext", page.hasNext());
        response.put("hasPrevious", page.hasPrevious());

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public Object getMyQuizAttempts(Long userId) {
        List<QuizAttempt> attempts = quizAttemptRepository.findByUserId(userId);
        return attempts.stream()
                .map(attempt -> QuizAttemptResponse.builder()
                        .id(attempt.getId())
                        .quizId(attempt.getQuiz().getId())
                        .quizTitle(attempt.getQuiz().getTitle())
                        .userId(attempt.getUser().getId())
                        .userName(attempt.getUser().getFullName())
                        .score(attempt.getScore())
                        .totalPoints(attempt.getTotalPoints())
                        .percentageScore(attempt.getPercentageScore())
                        .isPassed(attempt.getIsPassed())
                        .startedAt(attempt.getStartedAt())
                        .completedAt(attempt.getCompletedAt())
                        .timeSpentSeconds(attempt.getTimeSpentSeconds())
                        .status(attempt.getStatus().getLabel())
                        .build())
                .collect(Collectors.toList());
    }

    private QuestionResponse getQuestionResponse(Question question) {
        List<QuestionOption> options = questionOptionRepository.findByQuestionIdOrderByOrderSequenceAsc(question.getId());
        List<QuestionOptionResponse> optionResponses = options.stream()
                .map(option -> QuestionOptionResponse.builder()
                        .id(option.getId())
                        .optionText(option.getOptionText())
                        .isCorrect(option.getIsCorrect())
                        .orderSequence(option.getOrderSequence())
                        .build())
                .collect(Collectors.toList());

        return QuestionResponse.builder()
                .id(question.getId())
                .questionText(question.getQuestionText())
                .questionType(question.getQuestionType())
                .points(question.getPoints())
                .answerExplanation(question.getAnswerExplanation())
                .orderSequence(question.getOrderSequence())
                .imageUrl(question.getImageUrl())
                .videoUrl(question.getVideoUrl())
                .fileUrl(question.getFileUrl())
                .voiceUrl(question.getVoiceUrl())
                .options(optionResponses)
                .build();
    }
}

