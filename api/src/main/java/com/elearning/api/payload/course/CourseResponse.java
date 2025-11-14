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
public class CourseResponse {
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
    private Long learnerCount;
    private Instant createdAt;
    private Instant updatedAt;

    @Builder
    public CourseResponse(Long id, String title, String description, String category, Long categoryId,
                         Integer durationHours, Integer estimatedDays, LocalDate dueDate, String status,
                         Boolean isPublic, String imageUrl, Long learnerCount, Instant createdAt, Instant updatedAt) {
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
        this.learnerCount = learnerCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}

