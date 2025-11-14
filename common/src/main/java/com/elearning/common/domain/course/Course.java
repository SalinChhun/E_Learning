package com.elearning.common.domain.course;

import com.elearning.common.domain.Auditable;
import com.elearning.common.enums.CourseStatus;
import com.elearning.common.enums.Status;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "tb_course")
@NoArgsConstructor
public class Course extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private CourseCategory category;

    @Column(name = "duration_hours")
    private Integer durationHours;

    @Column(name = "estimated_days")
    private Integer estimatedDays;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "status", nullable = false, length = Types.CHAR)
    @JdbcTypeCode(Types.CHAR)
    @Convert(converter = CourseStatus.Converter.class)
    @ColumnDefault("'1'")
    private CourseStatus status = CourseStatus.DRAFT;

    @Column(name = "is_public", nullable = false)
    @ColumnDefault("false")
    private Boolean isPublic = false;

    @Column(name = "image_url")
    private String imageUrl;

    @Builder
    public Course(Long id, String title, String description, CourseCategory category,
                  Integer durationHours, Integer estimatedDays, LocalDate dueDate,
                  CourseStatus status, Boolean isPublic, String imageUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.durationHours = durationHours;
        this.estimatedDays = estimatedDays;
        this.dueDate = dueDate;
        this.status = status;
        this.isPublic = isPublic;
        this.imageUrl = imageUrl;
    }
}

