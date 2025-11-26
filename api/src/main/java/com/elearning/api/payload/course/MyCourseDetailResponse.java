package com.elearning.api.payload.course;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder
public class MyCourseDetailResponse {
    private Long enrollmentId;
    private Long courseId;
    private String title;
    private String description;
    private String category;
    private Integer durationHours;
    private Integer estimatedDays;
    private LocalDate dueDate;
    private String status;
    private Integer progressPercentage;
    private Long timeSpentSeconds;
    private Instant enrolledDate;
    private Instant completedDate;
    private String imageUrl;
    private String videoUrl;
    private String courseContent;
    private Integer totalScore;
    private Double percentageScore;
    private String examAttemptStatus; // "passed" or "failed" based on last quiz attempt
}

