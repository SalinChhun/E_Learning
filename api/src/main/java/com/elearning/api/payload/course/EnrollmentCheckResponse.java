package com.elearning.api.payload.course;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder
public class EnrollmentCheckResponse {
    private Boolean isEnrolled;
    private Long enrollmentId;
    private String status;
    private Integer progressPercentage;
    private Long timeSpentSeconds;
    private Instant enrolledDate;
    private Instant completedDate;
}

