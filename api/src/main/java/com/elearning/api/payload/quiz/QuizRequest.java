package com.elearning.api.payload.quiz;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "Request payload for creating or updating a quiz/exam")
public class QuizRequest {
    @NotBlank(message = "Title cannot be empty")
    @Schema(description = "Quiz/Exam title", example = "Banking Fundamentals Final Exam", required = true)
    private String title;

    @Schema(description = "Quiz/Exam description", example = "Final assessment for Banking Fundamentals course")
    private String description;

    @Schema(description = "Quiz type (1=QUIZ, 2=EXAM)", example = "2")
    private String type;

    @NotNull(message = "Course ID cannot be null")
    @Positive(message = "Course ID must be positive")
    @Schema(description = "Course ID to attach this quiz to", example = "1", required = true)
    private Long courseId;

    @Schema(description = "Duration in minutes", example = "30")
    private Integer durationMinutes;

    @Schema(description = "Passing score percentage", example = "70")
    private Integer passingScore;
}

