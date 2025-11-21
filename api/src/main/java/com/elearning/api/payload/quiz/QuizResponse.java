package com.elearning.api.payload.quiz;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder
public class QuizResponse {
    private Long id;
    private String title;
    private String description;
    private String type;
    private Long courseId;
    private String courseTitle;
    private Integer durationMinutes;
    private Integer passingScore;
    private String status;
    private List<QuestionResponse> questions;
    private Instant createdAt;
    private Instant updatedAt;
}

