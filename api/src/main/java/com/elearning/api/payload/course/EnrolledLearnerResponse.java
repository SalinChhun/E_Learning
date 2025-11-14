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
public class EnrolledLearnerResponse {
    private Long enrollmentId;
    private Long userId;
    private String name;
    private String email;
    private String department;
    private String status;
    private Integer progressPercentage;
    private Instant enrolledDate;
    private Instant completedDate;

    @Builder
    public EnrolledLearnerResponse(Long enrollmentId, Long userId, String name, String email,
                                   String department, String status, Integer progressPercentage,
                                   Instant enrolledDate, Instant completedDate) {
        this.enrollmentId = enrollmentId;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.department = department;
        this.status = status;
        this.progressPercentage = progressPercentage;
        this.enrolledDate = enrolledDate;
        this.completedDate = completedDate;
    }
}

