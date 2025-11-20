package com.elearning.api.payload.certificate;

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
public class CertificateTemplateResponse {
    private Long id;
    private String name;
    private String description;
    private String templateImageUrl;
    private String status;
    private Long coursesUsingCount;
    private Instant createdAt;
    private Instant updatedAt;
}

