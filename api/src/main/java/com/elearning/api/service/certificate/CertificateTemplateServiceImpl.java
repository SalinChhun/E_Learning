package com.elearning.api.service.certificate;

import com.elearning.api.payload.certificate.CertificateTemplateRequest;
import com.elearning.api.payload.certificate.CertificateTemplateResponse;
import com.elearning.api.payload.certificate.CertificateTemplateSummaryResponse;
import com.elearning.common.components.properties.FileInfoConfig;
import com.elearning.common.domain.certificate.CertificateTemplate;
import com.elearning.common.domain.certificate.CertificateTemplateRepository;
import com.elearning.common.domain.course.Course;
import com.elearning.common.domain.course.CourseRepository;
import com.elearning.common.enums.CertificateTemplateStatus;
import com.elearning.common.enums.StatusCode;
import com.elearning.common.exception.BusinessException;
import com.elearning.common.util.ImageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CertificateTemplateServiceImpl implements CertificateTemplateService {

    private final CertificateTemplateRepository certificateTemplateRepository;
    private final CourseRepository courseRepository;
    private final FileInfoConfig fileInfoConfig;

    @Override
    @Transactional
    public Object createTemplate(CertificateTemplateRequest request) {
        CertificateTemplateStatus templateStatus = CertificateTemplateStatus.DRAFT;
        if (request.getStatus() != null && !request.getStatus().isEmpty()) {
            CertificateTemplateStatus statusFromRequest = CertificateTemplateStatus.fromValue(request.getStatus());
            if (statusFromRequest != null) {
                templateStatus = statusFromRequest;
            }
        }

        CertificateTemplate template = CertificateTemplate.builder()
                .name(request.getName())
                .description(request.getDescription())
                .templateImageUrl(request.getTemplateImageUrl())
                .status(templateStatus)
                .build();

        template = certificateTemplateRepository.save(template);

        // Assign template to courses if courseIds are provided
        if (request.getCourseIds() != null && !request.getCourseIds().isEmpty()) {
            List<Course> courses = courseRepository.findAllById(request.getCourseIds());
            List<Course> coursesToUpdate = new ArrayList<>();
            
            for (Course course : courses) {
                course.setCertificateTemplate(template);
                course.setEnableCertificate(true);
                coursesToUpdate.add(course);
            }
            
            if (!coursesToUpdate.isEmpty()) {
                courseRepository.saveAll(coursesToUpdate);
            }
        }

        Long coursesUsingCount = certificateTemplateRepository.countCoursesUsingTemplate(template.getId());

        return CertificateTemplateResponse.builder()
                .id(template.getId())
                .name(template.getName())
                .description(template.getDescription())
                .templateImageUrl(ImageUtil.getImageUrl(fileInfoConfig.getBaseUrl(), template.getTemplateImageUrl()))
                .status(template.getStatus().getLabel())
                .coursesUsingCount(coursesUsingCount != null ? coursesUsingCount : 0L)
                .createdAt(template.getCreatedAt())
                .updatedAt(template.getUpdateAt())
                .build();
    }

    @Override
    @Transactional
    public Object updateTemplate(Long templateId, CertificateTemplateRequest request) {
        CertificateTemplate template = certificateTemplateRepository.findById(templateId)
                .orElseThrow(() -> new BusinessException(StatusCode.NOT_FOUND));

        template.setName(request.getName());
        template.setDescription(request.getDescription());
        template.setTemplateImageUrl(request.getTemplateImageUrl());
        
        if (request.getStatus() != null && !request.getStatus().isEmpty()) {
            CertificateTemplateStatus templateStatus = CertificateTemplateStatus.fromValue(request.getStatus());
            if (templateStatus != null) {
                template.setStatus(templateStatus);
            }
        }

        template = certificateTemplateRepository.save(template);

        // Update course assignments if courseIds are provided
        if (request.getCourseIds() != null) {
            // Get all courses currently using this template
            List<Course> existingCourses = courseRepository.findByCertificateTemplateId(templateId);
            
            // Get the set of course IDs that should be assigned to this template
            Set<Long> requestedCourseIds = request.getCourseIds().isEmpty() ? new HashSet<>() : new HashSet<>(request.getCourseIds());
            
            // Find courses to remove template from (courses not in the new list)
            List<Course> coursesToRemoveTemplate = existingCourses.stream()
                    .filter(course -> !requestedCourseIds.contains(course.getId()))
                    .collect(Collectors.toList());
            
            // Remove template from courses that are no longer in the list
            if (!coursesToRemoveTemplate.isEmpty()) {
                for (Course course : coursesToRemoveTemplate) {
                    course.setCertificateTemplate(null);
                    course.setEnableCertificate(false);
                }
                courseRepository.saveAll(coursesToRemoveTemplate);
            }
            
            // Add template to courses in the new list that aren't already assigned
            if (!requestedCourseIds.isEmpty()) {
                List<Course> coursesToAdd = courseRepository.findAllById(requestedCourseIds);
                List<Course> coursesToUpdate = new ArrayList<>();
                
                // Get set of course IDs that remain assigned after removal
                Set<Long> remainingAssignedCourseIds = existingCourses.stream()
                        .filter(course -> requestedCourseIds.contains(course.getId()))
                        .map(Course::getId)
                        .collect(Collectors.toSet());
                
                for (Course course : coursesToAdd) {
                    // Only update if not already assigned
                    if (!remainingAssignedCourseIds.contains(course.getId())) {
                        course.setCertificateTemplate(template);
                        course.setEnableCertificate(true);
                        coursesToUpdate.add(course);
                    }
                }
                
                if (!coursesToUpdate.isEmpty()) {
                    courseRepository.saveAll(coursesToUpdate);
                }
            } else {
                // If empty list provided, remove template from all courses
                if (!existingCourses.isEmpty()) {
                    for (Course course : existingCourses) {
                        course.setCertificateTemplate(null);
                        course.setEnableCertificate(false);
                    }
                    courseRepository.saveAll(existingCourses);
                }
            }
        }

        Long coursesUsingCount = certificateTemplateRepository.countCoursesUsingTemplate(template.getId());

        return CertificateTemplateResponse.builder()
                .id(template.getId())
                .name(template.getName())
                .description(template.getDescription())
                .templateImageUrl(ImageUtil.getImageUrl(fileInfoConfig.getBaseUrl(), template.getTemplateImageUrl()))
                .status(template.getStatus().getLabel())
                .coursesUsingCount(coursesUsingCount != null ? coursesUsingCount : 0L)
                .createdAt(template.getCreatedAt())
                .updatedAt(template.getUpdateAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Object getTemplateById(Long templateId) {
        CertificateTemplate template = certificateTemplateRepository.findById(templateId)
                .orElseThrow(() -> new BusinessException(StatusCode.NOT_FOUND));

        Long coursesUsingCount = certificateTemplateRepository.countCoursesUsingTemplate(templateId);

        return CertificateTemplateResponse.builder()
                .id(template.getId())
                .name(template.getName())
                .description(template.getDescription())
                .templateImageUrl(ImageUtil.getImageUrl(fileInfoConfig.getBaseUrl(), template.getTemplateImageUrl()))
                .status(template.getStatus().getLabel())
                .coursesUsingCount(coursesUsingCount != null ? coursesUsingCount : 0L)
                .createdAt(template.getCreatedAt())
                .updatedAt(template.getUpdateAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Object getTemplates(String searchValue, String status, Pageable pageable) {
        Page<CertificateTemplate> templatesPage = certificateTemplateRepository.findTemplates(
                status,
                searchValue,
                pageable
        );

        List<CertificateTemplateResponse> templateResponses = templatesPage.getContent().stream()
                .map(template -> {
                    Long coursesUsingCount = certificateTemplateRepository.countCoursesUsingTemplate(template.getId());
                    return CertificateTemplateResponse.builder()
                            .id(template.getId())
                            .name(template.getName())
                            .description(template.getDescription())
                            .templateImageUrl(ImageUtil.getImageUrl(fileInfoConfig.getBaseUrl(), template.getTemplateImageUrl()))
                            .status(template.getStatus().getLabel())
                            .coursesUsingCount(coursesUsingCount != null ? coursesUsingCount : 0L)
                            .createdAt(template.getCreatedAt())
                            .updatedAt(template.getUpdateAt())
                            .build();
                })
                .collect(Collectors.toList());

        Page<CertificateTemplateResponse> responsePage = new PageImpl<>(
                templateResponses,
                templatesPage.getPageable(),
                templatesPage.getTotalElements()
        );

        // Calculate summary statistics
        Long activeTemplates = certificateTemplateRepository.countByStatus(CertificateTemplateStatus.ACTIVE);
        Long draftTemplates = certificateTemplateRepository.countByStatus(CertificateTemplateStatus.DRAFT);
        Long totalTemplates = (activeTemplates != null ? activeTemplates : 0L) + 
                              (draftTemplates != null ? draftTemplates : 0L);
        Long coursesUsing = courseRepository.countByCertificateTemplateIsNotNull();

        Map<String, Object> response = new HashMap<>();
        response.put("templates", templateResponses);
        response.put("totalElements", templatesPage.getTotalElements());
        response.put("totalPages", templatesPage.getTotalPages());
        response.put("currentPage", templatesPage.getNumber());
        response.put("pageSize", templatesPage.getSize());
        response.put("hasNext", templatesPage.hasNext());
        response.put("hasPrevious", templatesPage.hasPrevious());
        
        // Add summary statistics
        response.put("summary", Map.of(
                "totalTemplates", totalTemplates,
                "activeTemplates", activeTemplates != null ? activeTemplates : 0L,
                "draftTemplates", draftTemplates != null ? draftTemplates : 0L,
                "coursesUsing", coursesUsing != null ? coursesUsing : 0L
        ));

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public Object getTemplatesSummary() {
        // Count templates excluding deleted ones
        Long activeTemplates = certificateTemplateRepository.countByStatus(CertificateTemplateStatus.ACTIVE);
        Long draftTemplates = certificateTemplateRepository.countByStatus(CertificateTemplateStatus.DRAFT);
        Long totalTemplates = (activeTemplates != null ? activeTemplates : 0L) + 
                              (draftTemplates != null ? draftTemplates : 0L);
        
        // Count total courses using any template
        Long coursesUsing = courseRepository.countByCertificateTemplateIsNotNull();

        return CertificateTemplateSummaryResponse.builder()
                .totalTemplates(totalTemplates)
                .activeTemplates(activeTemplates != null ? activeTemplates : 0L)
                .draftTemplates(draftTemplates != null ? draftTemplates : 0L)
                .coursesUsing(coursesUsing != null ? coursesUsing : 0L)
                .build();
    }

    @Override
    @Transactional
    public Object deleteTemplate(Long templateId) {
        CertificateTemplate template = certificateTemplateRepository.findById(templateId)
                .orElseThrow(() -> new BusinessException(StatusCode.NOT_FOUND));
        
        template.setStatus(CertificateTemplateStatus.DELETE);
        certificateTemplateRepository.save(template);
        
        return Map.of("message", "Certificate template deleted successfully", "id", templateId);
    }

    @Override
    @Transactional(readOnly = true)
    public Object getCoursesUsingTemplate(Long templateId) {
        CertificateTemplate template = certificateTemplateRepository.findById(templateId)
                .orElseThrow(() -> new BusinessException(StatusCode.NOT_FOUND));

        List<Course> courses = courseRepository.findByCertificateTemplateId(templateId);
        
        List<Map<String, Object>> courseList = courses.stream()
                .map(course -> {
                    Map<String, Object> courseMap = new HashMap<>();
                    courseMap.put("id", course.getId());
                    courseMap.put("title", course.getTitle());
                    courseMap.put("description", course.getDescription());
                    return courseMap;
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("templateId", templateId);
        response.put("templateName", template.getName());
        response.put("courses", courseList);
        response.put("total", (long) courseList.size());

        return response;
    }
}

