package com.elearning.common.domain.certificate;

import com.elearning.common.enums.CertificateTemplateStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CertificateTemplateRepository extends JpaRepository<CertificateTemplate, Long> {
    List<CertificateTemplate> findByStatus(CertificateTemplateStatus status);
    
    Long countByStatus(CertificateTemplateStatus status);
    
    Optional<CertificateTemplate> findByIdAndStatus(Long id, CertificateTemplateStatus status);
    
    @Query(value = """
        SELECT ct.* FROM tb_certificate_template ct
        WHERE (COALESCE(:status, '') = '' OR ct.status = CAST(:status AS char))
        AND ct.status != '9'
        AND (COALESCE(:searchValue, '') = '' OR 
             LOWER(ct.name) LIKE LOWER(CONCAT('%', :searchValue, '%')) OR 
             LOWER(ct.description) LIKE LOWER(CONCAT('%', :searchValue, '%')))
        """,
        countQuery = """
        SELECT COUNT(ct.id) FROM tb_certificate_template ct
        WHERE (COALESCE(:status, '') = '' OR ct.status = CAST(:status AS char))
        AND ct.status != '9'
        AND (COALESCE(:searchValue, '') = '' OR 
             LOWER(ct.name) LIKE LOWER(CONCAT('%', :searchValue, '%')) OR 
             LOWER(ct.description) LIKE LOWER(CONCAT('%', :searchValue, '%')))
        """,
        nativeQuery = true)
    Page<CertificateTemplate> findTemplates(
            @Param("status") String status,
            @Param("searchValue") String searchValue,
            Pageable pageable
    );
    
    @Query("SELECT COUNT(c) FROM Course c WHERE c.certificateTemplate.id = :templateId")
    Long countCoursesUsingTemplate(@Param("templateId") Long templateId);
}

