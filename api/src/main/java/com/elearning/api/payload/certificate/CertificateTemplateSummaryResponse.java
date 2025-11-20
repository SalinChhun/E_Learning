package com.elearning.api.payload.certificate;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder
public class CertificateTemplateSummaryResponse {
    private Long totalTemplates;
    private Long activeTemplates;
    private Long draftTemplates;
    private Long coursesUsing;
}

