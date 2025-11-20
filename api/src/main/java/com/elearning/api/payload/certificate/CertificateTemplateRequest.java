package com.elearning.api.payload.certificate;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "Request payload for creating or updating a certificate template")
public class CertificateTemplateRequest {
    @NotBlank(message = "Template name cannot be empty")
    @Schema(description = "Template name", example = "Corporate Certificate", required = true)
    private String name;

    @Schema(description = "Template description", example = "Official corporate certificate template with PPCBank branding")
    private String description;

    @Schema(description = "Template image URL (JPG/PNG)", example = "https://example.com/certificate-template.jpg")
    private String templateImageUrl;

    @Schema(description = "Template status (1=DRAFT, 2=ACTIVE, 9=DELETE)", example = "1")
    private String status;

    @Schema(description = "List of course IDs to assign this template to", example = "[1, 2, 3]")
    private List<Long> courseIds;
}

