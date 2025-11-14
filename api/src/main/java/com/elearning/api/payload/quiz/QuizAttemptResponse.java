package com.elearning.api.payload.quiz;

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
public class QuizAttemptResponse {
    private Long id;
    private Long quizId;
    private String quizTitle;
    private Long userId;
    private String userName;
    private Integer score;
    private Integer totalPoints;
    private Double percentageScore;
    private Boolean isPassed;
    private Instant startedAt;
    private Instant completedAt;
    private Long timeSpentSeconds;
    private String status;
}

