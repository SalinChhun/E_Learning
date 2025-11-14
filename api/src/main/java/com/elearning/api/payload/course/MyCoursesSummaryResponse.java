package com.elearning.api.payload.course;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MyCoursesSummaryResponse {
    private Long totalCourses;
    private Long inProgress;
    private Long completed;
    private Long certificates;
    private List<MyCourseResponse> courses;

    @Builder
    public MyCoursesSummaryResponse(Long totalCourses, Long inProgress, Long completed,
                                   Long certificates, List<MyCourseResponse> courses) {
        this.totalCourses = totalCourses;
        this.inProgress = inProgress;
        this.completed = completed;
        this.certificates = certificates;
        this.courses = courses;
    }
}

