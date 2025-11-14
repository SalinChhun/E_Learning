package com.elearning.api.payload.course;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class LessonResponse {
    private Long id;
    private String title;
    private String description;
    private String content;
    private String videoUrl;
    private Integer durationMinutes;
    private Integer orderSequence;

    @Builder
    public LessonResponse(Long id, String title, String description, String content,
                         String videoUrl, Integer durationMinutes, Integer orderSequence) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.content = content;
        this.videoUrl = videoUrl;
        this.durationMinutes = durationMinutes;
        this.orderSequence = orderSequence;
    }
}

