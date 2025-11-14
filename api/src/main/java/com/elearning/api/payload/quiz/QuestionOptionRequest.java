package com.elearning.api.payload.quiz;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "Request payload for question option")
public class QuestionOptionRequest {
    @NotBlank(message = "Option text cannot be empty")
    @Schema(description = "Option text", example = "Autocratic leadership", required = true)
    private String optionText;

    @Schema(description = "Whether this option is correct", example = "true")
    private Boolean isCorrect;

    @Schema(description = "Order sequence", example = "1")
    private Integer orderSequence;
}

