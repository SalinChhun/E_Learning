package com.elearning.api.payload.course;

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
@Schema(description = "Request payload for enrolling in a course or bulk enrolling users")
public class EnrollCourseRequest {
    @NotNull
    @Positive
    @Schema(description = "Course ID to enroll in", example = "1", required = true)
    private Long courseId;

    @Schema(description = "List of user IDs for bulk enrollment (optional - if null, enrolls current user)", example = "[2, 3, 4]")
    private List<Long> userIds; // For bulk enrollment, if null, enroll current user
}

