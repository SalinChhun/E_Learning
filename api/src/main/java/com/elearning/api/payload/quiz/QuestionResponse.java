package com.elearning.api.payload.quiz;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder
public class QuestionResponse {
    private Long id;
    private String questionText;
    private String questionType;
    private Integer points;
    private String answerExplanation;
    private Integer orderSequence;
    private String imageUrl;
    private String videoUrl;
    private String fileUrl;
    private String voiceUrl;
    private List<QuestionOptionResponse> options;
}

