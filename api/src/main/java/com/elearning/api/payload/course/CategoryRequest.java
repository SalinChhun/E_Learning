package com.elearning.api.payload.course;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "Request payload for creating or updating a course category")
public class CategoryRequest {
    @NotBlank(message = "Category name is required")
    @Schema(description = "Category name (must be unique)", example = "Finance", required = true)
    private String name;

    @Schema(description = "Category description", example = "Financial courses and banking fundamentals")
    private String description;
}

