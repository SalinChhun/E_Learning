package com.elearning.api.controller;

import com.elearning.api.helper.AuthHelper;
import com.elearning.api.payload.MultiSortBuilder;
import com.elearning.api.payload.quiz.QuestionRequest;
import com.elearning.api.payload.quiz.QuizAttemptRequest;
import com.elearning.api.payload.quiz.QuizRequest;
import com.elearning.api.service.quiz.QuizService;
import com.elearning.common.common.RestApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wba/v1/quizzes")
@RequiredArgsConstructor
@Tag(name = "Quiz Management", description = "APIs for managing quizzes, exams, questions, and quiz attempts")
@SecurityRequirement(name = "Bearer Authentication")
public class QuizController extends RestApiResponse {

    private final QuizService quizService;

    @PostMapping
    @Operation(
            summary = "Create a new quiz/exam",
            description = "Creates a new quiz or exam and attaches it to a course. Requires admin privileges."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quiz created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Course not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    })
    public ResponseEntity<?> createQuiz(@Valid @RequestBody QuizRequest request) {
        return ok(quizService.createQuiz(request));
    }

    @PutMapping("/{quizId}")
    @Operation(
            summary = "Update quiz/exam",
            description = "Updates an existing quiz or exam. If questions are provided, they will replace all existing questions. " +
                    "Questions with ID will be updated, questions without ID will be created. Requires admin privileges."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quiz updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Quiz or course not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    })
    public ResponseEntity<?> updateQuiz(
            @Parameter(description = "Quiz ID", required = true, example = "1")
            @PathVariable Long quizId,
            @Valid @RequestBody QuizRequest request) {
        return ok(quizService.updateQuiz(quizId, request));
    }

    @GetMapping
    @Operation(
            summary = "Get all quizzes",
            description = "Retrieves a paginated list of quizzes with optional search, course, and status filtering"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quizzes retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<?> getQuizzes(
            @Parameter(description = "Search by quiz title or description", example = "banking")
            @RequestParam(value = "search_value", required = false) String searchValue,
            @Parameter(description = "Filter by course ID", example = "1")
            @RequestParam(value = "course_id", required = false) Long courseId,
            @Parameter(description = "Filter by quiz status (1=NORMAL, 2=DISABLE, 9=DELETE). If not provided, returns all quizzes.", example = "1")
            @RequestParam(value = "status", required = false) String status,
            @Parameter(description = "Sort columns (e.g., 'id:desc' or 'title:asc,id:desc')", example = "id:desc")
            @RequestParam(value = "sort_columns", required = false, defaultValue = "id:desc") String sortColumns,
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(value = "page_number", defaultValue = "0") int pageNumber,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(value = "page_size", defaultValue = "10") int pageSize
    ) {
        List<Sort.Order> sortBuilder = new MultiSortBuilder().with(sortColumns).build();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortBuilder));

        return ok(quizService.getQuizzes(searchValue, courseId, status, pageable));
    }

    @GetMapping("/{quizId}")
    @Operation(
            summary = "Get quiz details",
            description = "Retrieves detailed information about a quiz including all questions and options"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quiz details retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Quiz not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<?> getQuizById(
            @Parameter(description = "Quiz ID", required = true, example = "1")
            @PathVariable Long quizId) {
        return ok(quizService.getQuizById(quizId));
    }

    @GetMapping("/course/{courseId}")
    @Operation(
            summary = "Get quizzes by course ID",
            description = "Retrieves all quizzes/exams attached to a specific course"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quizzes retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<?> getQuizzesByCourseId(
            @Parameter(description = "Course ID", required = true, example = "1")
            @PathVariable Long courseId) {
        return ok(quizService.getQuizzesByCourseId(courseId));
    }

    @DeleteMapping("/{quizId}")
    @Operation(
            summary = "Delete quiz/exam",
            description = "Soft deletes a quiz/exam by setting its status to DISABLE. Requires admin privileges."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quiz deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Quiz not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    })
    public ResponseEntity<?> deleteQuiz(
            @Parameter(description = "Quiz ID", required = true, example = "1")
            @PathVariable Long quizId) {
        return ok(quizService.deleteQuiz(quizId));
    }

    @PostMapping("/{quizId}/questions")
    @Operation(
            summary = "Add question to quiz",
            description = "Adds a new question to a quiz. Supports multiple choice, true/false, short answer, and essay questions. Requires admin privileges."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Question added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Quiz not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    })
    public ResponseEntity<?> addQuestion(
            @Parameter(description = "Quiz ID", required = true, example = "1")
            @PathVariable Long quizId,
            @Valid @RequestBody QuestionRequest request) {
        return ok(quizService.addQuestion(quizId, request));
    }

    @PutMapping("/questions/{questionId}")
    @Operation(
            summary = "Update question",
            description = "Updates an existing question in a quiz. Requires admin privileges."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Question updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Question not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    })
    public ResponseEntity<?> updateQuestion(
            @Parameter(description = "Question ID", required = true, example = "1")
            @PathVariable Long questionId,
            @Valid @RequestBody QuestionRequest request) {
        return ok(quizService.updateQuestion(questionId, request));
    }

    @DeleteMapping("/questions/{questionId}")
    @Operation(
            summary = "Delete question",
            description = "Deletes a question from a quiz. Requires admin privileges."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Question deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Question not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    })
    public ResponseEntity<?> deleteQuestion(
            @Parameter(description = "Question ID", required = true, example = "1")
            @PathVariable Long questionId) {
        return ok(quizService.deleteQuestion(questionId));
    }

    @PostMapping("/{quizId}/start")
    @Operation(
            summary = "Start quiz attempt",
            description = "Starts a new quiz attempt for the current user. Records the start time."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quiz attempt started successfully"),
            @ApiResponse(responseCode = "404", description = "Quiz not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<?> startQuizAttempt(
            @Parameter(description = "Quiz ID", required = true, example = "1")
            @PathVariable Long quizId) {
        Long userId = AuthHelper.getCurrentUserId();
        return ok(quizService.startQuizAttempt(quizId, userId));
    }

    @GetMapping("/{quizId}/take")
    @Operation(
            summary = "Get quiz for taking (Take Exam Now)",
            description = "Returns quiz questions for taking the exam. Hides correct answers and explanations. " +
                    "Automatically starts or resumes an attempt. This is the API to call when user clicks 'Take Exam Now'."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quiz questions retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Quiz not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<?> getQuizForTaking(
            @Parameter(description = "Quiz ID", required = true, example = "1")
            @PathVariable Long quizId) {
        Long userId = AuthHelper.getCurrentUserId();
        return ok(quizService.getQuizForTaking(quizId, userId));
    }

    @PostMapping("/submit")
    @Operation(
            summary = "Submit quiz attempt",
            description = "Submits a quiz attempt with answers. Automatically calculates score, percentage, and pass/fail status."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quiz attempt submitted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Quiz or question not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<?> submitQuizAttempt(@Valid @RequestBody QuizAttemptRequest request) {
        Long userId = AuthHelper.getCurrentUserId();
        return ok(quizService.submitQuizAttempt(request, userId));
    }

    @GetMapping("/{quizId}/attempts")
    @Operation(
            summary = "Get quiz attempts",
            description = "Retrieves all attempts for a specific quiz with pagination. Requires admin privileges."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Attempts retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    })
    public ResponseEntity<?> getQuizAttempts(
            @Parameter(description = "Quiz ID", required = true, example = "1")
            @PathVariable Long quizId,
            @Parameter(description = "Sort columns (e.g., 'id:desc')", example = "id:desc")
            @RequestParam(value = "sort_columns", required = false, defaultValue = "id:desc") String sortColumns,
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(value = "page_number", defaultValue = "0") int pageNumber,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(value = "page_size", defaultValue = "10") int pageSize
    ) {
        List<Sort.Order> sortBuilder = new MultiSortBuilder().with(sortColumns).build();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortBuilder));
        return ok(quizService.getQuizAttempts(quizId, pageable));
    }

    @GetMapping("/my-attempts")
    @Operation(
            summary = "Get my quiz attempts",
            description = "Retrieves all quiz attempts for the current user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Attempts retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<?> getMyQuizAttempts() {
        Long userId = AuthHelper.getCurrentUserId();
        return ok(quizService.getMyQuizAttempts(userId));
    }
}

