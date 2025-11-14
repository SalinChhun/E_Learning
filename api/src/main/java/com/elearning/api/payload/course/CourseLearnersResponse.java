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
public class CourseLearnersResponse {
    private Long totalLearners;
    private Long completedCount;
    private Long inProgressCount;
    private Long pendingCount;
    private List<EnrolledLearnerResponse> learners;

    @Builder
    public CourseLearnersResponse(Long totalLearners, Long completedCount, Long inProgressCount,
                                 Long pendingCount, List<EnrolledLearnerResponse> learners) {
        this.totalLearners = totalLearners;
        this.completedCount = completedCount;
        this.inProgressCount = inProgressCount;
        this.pendingCount = pendingCount;
        this.learners = learners;
    }
}

