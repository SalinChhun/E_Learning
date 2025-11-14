package com.elearning.api.payload.course;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "Request payload for creating or updating a course")
public class CourseRequest {
    @NotBlank
    @Schema(description = "Course title", example = "Banking Fundamentals", required = true)
    private String title;

    @Schema(description = "Course description", example = "Essential banking concepts and practices for all staff")
    private String description;

    @NotNull
    @Positive
    @Schema(description = "Category ID", example = "1", required = true)
    private Long categoryId;

    @Schema(description = "Course duration in hours", example = "8")
    private Integer durationHours;

    @Schema(description = "Estimated days to complete", example = "4")
    private Integer estimatedDays;

    @Schema(description = "Course due date", example = "2025-12-31")
    private LocalDate dueDate;

    @Schema(description = "Whether the course is public", example = "true")
    private Boolean isPublic;

    @Schema(description = "Course image URL", example = "https://example.com/course-image.jpg")
    private String imageUrl;
}

