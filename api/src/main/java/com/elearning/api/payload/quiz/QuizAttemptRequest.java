package com.elearning.api.payload.quiz;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "Request payload for submitting a quiz attempt")
public class QuizAttemptRequest {
    @NotNull(message = "Quiz ID cannot be null")
    @Positive(message = "Quiz ID must be positive")
    @Schema(description = "Quiz ID", example = "1", required = true)
    private Long quizId;

    @Schema(description = "List of answers", required = true)
    private List<AnswerRequest> answers;
}

