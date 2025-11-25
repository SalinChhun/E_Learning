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
public class EnrollmentResponse {
    private Long enrollmentId;
    private Long courseId;
    private String courseTitle;
    private String courseDescription;
    private String courseCategory;
    private Long userId;
    private String userName;
    private String userEmail;
    private String department;
    private String status;
    private Integer progressPercentage;
    private Long timeSpentSeconds;
    private Instant enrolledDate;
    private Instant completedDate;
    private Instant createdAt;
    private Instant updatedAt;

    @Builder
    public EnrollmentResponse(Long enrollmentId, Long courseId, String courseTitle, String courseDescription,
                             String courseCategory, Long userId, String userName, String userEmail,
                             String department, String status, Integer progressPercentage,
                             Long timeSpentSeconds, Instant enrolledDate, Instant completedDate,
                             Instant createdAt, Instant updatedAt) {
        this.enrollmentId = enrollmentId;
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.courseDescription = courseDescription;
        this.courseCategory = courseCategory;
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.department = department;
        this.status = status;
        this.progressPercentage = progressPercentage;
        this.timeSpentSeconds = timeSpentSeconds;
        this.enrolledDate = enrolledDate;
        this.completedDate = completedDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}


