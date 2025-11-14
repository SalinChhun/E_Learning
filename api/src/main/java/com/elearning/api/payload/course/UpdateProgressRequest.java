package com.elearning.api.payload.course;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "Request payload for updating course progress")
public class UpdateProgressRequest {
    @NotNull
    @Min(0)
    @Max(100)
    @Schema(description = "Progress percentage (0-100)", example = "65", required = true, minimum = "0", maximum = "100")
    private Integer progressPercentage;

    @Schema(description = "Time spent in seconds", example = "3600")
    private Long timeSpentSeconds;
}

