package com.elearning.api.payload.common;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ReportCountResponse {
    private Long userCount;
    private Long providerCount;
    private Long authConfigCount;
    private Long apiManagementCount;

    @Builder
    public ReportCountResponse(Long userCount, Long providerCount, Long authConfigCount, Long apiManagementCount) {
        this.userCount = userCount;
        this.providerCount = providerCount;
        this.authConfigCount = authConfigCount;
        this.apiManagementCount = apiManagementCount;
    }
}
