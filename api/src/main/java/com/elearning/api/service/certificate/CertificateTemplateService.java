package com.elearning.api.service.certificate;

import com.elearning.api.payload.certificate.CertificateTemplateRequest;
import org.springframework.data.domain.Pageable;

public interface CertificateTemplateService {
    Object createTemplate(CertificateTemplateRequest request);
    Object updateTemplate(Long templateId, CertificateTemplateRequest request);
    Object getTemplateById(Long templateId);
    Object getTemplates(String searchValue, String status, Pageable pageable);
    Object getTemplatesSummary();
    Object deleteTemplate(Long templateId);
    Object getCoursesUsingTemplate(Long templateId);
    Object getTemplateByCourseId(Long courseId);
}

