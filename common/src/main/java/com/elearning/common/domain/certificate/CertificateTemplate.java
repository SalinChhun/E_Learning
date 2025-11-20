package com.elearning.common.domain.certificate;

import com.elearning.common.domain.Auditable;
import com.elearning.common.enums.CertificateTemplateStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;

@Getter
@Setter
@Entity
@Table(name = "tb_certificate_template")
@NoArgsConstructor
public class CertificateTemplate extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "template_image_url", columnDefinition = "TEXT")
    private String templateImageUrl;

    @Column(name = "status", nullable = false, length = Types.CHAR)
    @JdbcTypeCode(Types.CHAR)
    @Convert(converter = CertificateTemplateStatus.Converter.class)
    @ColumnDefault("'1'")
    private CertificateTemplateStatus status = CertificateTemplateStatus.DRAFT;

    @Builder
    public CertificateTemplate(Long id, String name, String description, 
                              String templateImageUrl, CertificateTemplateStatus status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.templateImageUrl = templateImageUrl;
        this.status = status;
    }
}

