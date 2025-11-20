package com.elearning.api.controller;

import com.elearning.api.payload.MultiSortBuilder;
import com.elearning.api.payload.certificate.CertificateTemplateRequest;
import com.elearning.api.service.certificate.CertificateTemplateService;
import com.elearning.common.common.RestApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wba/v1/certificate-templates")
@RequiredArgsConstructor
@Tag(name = "Certificate Template Management", description = "APIs for managing certificate templates")
@SecurityRequirement(name = "Bearer Authentication")
public class CertificateTemplateController extends RestApiResponse {

    private final CertificateTemplateService certificateTemplateService;

    @GetMapping
    @Operation(
            summary = "Get all certificate templates",
            description = "Retrieves a paginated list of certificate templates with optional search and status filtering"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Templates retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<?> getTemplates(
            @Parameter(description = "Search by template name or description", example = "corporate")
            @RequestParam(value = "search_value", required = false) String searchValue,
            @Parameter(description = "Filter by template status (1=DRAFT, 2=ACTIVE, 9=DELETE). If not provided, returns all templates.", example = "2")
            @RequestParam(value = "status", required = false) String status,
            @Parameter(description = "Sort columns (e.g., 'id:desc' or 'name:asc,id:desc')", example = "id:desc")
            @RequestParam(value = "sort_columns", required = false, defaultValue = "id:desc") String sortColumns,
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(value = "page_number", defaultValue = "0") int pageNumber,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(value = "page_size", defaultValue = "10") int pageSize
    ) {
        List<Sort.Order> sortBuilder = new MultiSortBuilder().with(sortColumns).build();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortBuilder));

        return ok(certificateTemplateService.getTemplates(searchValue, status, pageable));
    }

    @GetMapping("/summary")
    @Operation(
            summary = "Get certificate templates summary",
            description = "Retrieves summary statistics (total templates, active, drafts, courses using)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Summary retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<?> getTemplatesSummary() {
        return ok(certificateTemplateService.getTemplatesSummary());
    }

    @GetMapping("/{templateId}")
    @Operation(
            summary = "Get certificate template details",
            description = "Retrieves detailed information about a specific certificate template"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Template details retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Template not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<?> getTemplateById(
            @Parameter(description = "Template ID", required = true, example = "1")
            @PathVariable Long templateId) {
        return ok(certificateTemplateService.getTemplateById(templateId));
    }

    @PostMapping
    @Operation(
            summary = "Create certificate template",
            description = "Creates a new certificate template. The template will be created in DRAFT status. Requires admin privileges."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Template created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    })
    public ResponseEntity<?> createTemplate(@Valid @RequestBody CertificateTemplateRequest request) {
        return ok(certificateTemplateService.createTemplate(request));
    }

    @PutMapping("/{templateId}")
    @Operation(
            summary = "Update certificate template",
            description = "Updates an existing certificate template. Requires admin privileges."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Template updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Template not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    })
    public ResponseEntity<?> updateTemplate(
            @Parameter(description = "Template ID", required = true, example = "1")
            @PathVariable Long templateId,
            @Valid @RequestBody CertificateTemplateRequest request) {
        return ok(certificateTemplateService.updateTemplate(templateId, request));
    }

    @DeleteMapping("/{templateId}")
    @Operation(
            summary = "Delete certificate template",
            description = "Soft deletes a certificate template by setting its status to DELETE. Requires admin privileges."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Template deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Template not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    })
    public ResponseEntity<?> deleteTemplate(
            @Parameter(description = "Template ID", required = true, example = "1")
            @PathVariable Long templateId) {
        return ok(certificateTemplateService.deleteTemplate(templateId));
    }

    @GetMapping("/{templateId}/courses")
    @Operation(
            summary = "Get courses using template",
            description = "Retrieves a list of all courses that are using this certificate template"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Courses retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Template not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<?> getCoursesUsingTemplate(
            @Parameter(description = "Template ID", required = true, example = "1")
            @PathVariable Long templateId) {
        return ok(certificateTemplateService.getCoursesUsingTemplate(templateId));
    }
}

