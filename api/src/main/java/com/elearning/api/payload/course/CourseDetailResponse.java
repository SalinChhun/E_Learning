package com.elearning.api.payload.course;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CourseDetailResponse {
    private Long id;
    private String title;
    private String description;
    private String category;
    private Long categoryId;
    private Integer durationHours;
    private Integer estimatedDays;
    private LocalDate dueDate;
    private String status;
    private Boolean isPublic;
    private String imageUrl;
    private String courseContent;
    private String assignmentType;
    private Long learnerCount;
    private Instant createdAt;
    private Instant updatedAt;
    private List<LessonResponse> lessons;
    private EnrollmentInfo enrollmentInfo; // Current user's enrollment if enrolled

    @Getter
    @Setter
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class EnrollmentInfo {
        private Long enrollmentId;
        private String status;
        private Integer progressPercentage;
        private Long timeSpentSeconds;
        private Instant enrolledDate;
        private Instant completedDate;
    }

    @Builder
    public CourseDetailResponse(Long id, String title, String description, String category,
                               Long categoryId, Integer durationHours, Integer estimatedDays,
                               LocalDate dueDate, String status, Boolean isPublic, String imageUrl,
                               String courseContent, String assignmentType, Long learnerCount,
                               Instant createdAt, Instant updatedAt, List<LessonResponse> lessons,
                               EnrollmentInfo enrollmentInfo) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.categoryId = categoryId;
        this.durationHours = durationHours;
        this.estimatedDays = estimatedDays;
        this.dueDate = dueDate;
        this.status = status;
        this.isPublic = isPublic;
        this.imageUrl = imageUrl;
        this.courseContent = courseContent;
        this.assignmentType = assignmentType;
        this.learnerCount = learnerCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.lessons = lessons;
        this.enrollmentInfo = enrollmentInfo;
    }
}

