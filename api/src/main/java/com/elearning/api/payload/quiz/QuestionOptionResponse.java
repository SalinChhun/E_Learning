package com.elearning.api.payload.quiz;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder
public class QuestionOptionResponse {
    private Long id;
    private String optionText;
    private Boolean isCorrect;
    private Integer orderSequence;
}

