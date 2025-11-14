package com.elearning.api.payload.quiz;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "Answer for a question in quiz attempt")
public class AnswerRequest {
    @NotNull(message = "Question ID cannot be null")
    @Positive(message = "Question ID must be positive")
    @Schema(description = "Question ID", example = "1", required = true)
    private Long questionId;

    @Schema(description = "Selected option ID (for multiple choice questions)", example = "3")
    private Long selectedOptionId;

    @Schema(description = "Answer text (for short answer or essay questions)", example = "Autocratic leadership")
    private String answerText;
}

