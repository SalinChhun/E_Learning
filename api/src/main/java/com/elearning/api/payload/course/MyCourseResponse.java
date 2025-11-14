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
public class MyCourseResponse {
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

    @Builder
    public MyCourseResponse(Long enrollmentId, Long courseId, String title, String description,
                           String category, Integer durationHours, Integer estimatedDays,
                           LocalDate dueDate, String status, Integer progressPercentage,
                           Long timeSpentSeconds, Instant enrolledDate, Instant completedDate,
                           String imageUrl) {
        this.enrollmentId = enrollmentId;
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.category = category;
        this.durationHours = durationHours;
        this.estimatedDays = estimatedDays;
        this.dueDate = dueDate;
        this.status = status;
        this.progressPercentage = progressPercentage;
        this.timeSpentSeconds = timeSpentSeconds;
        this.enrolledDate = enrolledDate;
        this.completedDate = completedDate;
        this.imageUrl = imageUrl;
    }
}

