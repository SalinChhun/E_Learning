package com.elearning.common.domain.course;

import com.elearning.common.domain.Auditable;
import com.elearning.common.domain.user.User;
import com.elearning.common.enums.EnrollmentStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "tb_course_enrollment", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"course_id", "user_id"}))
@NoArgsConstructor
public class CourseEnrollment extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "status", nullable = false, length = Types.CHAR)
    @JdbcTypeCode(Types.CHAR)
    @Convert(converter = EnrollmentStatus.Converter.class)
    @ColumnDefault("'1'")
    private EnrollmentStatus status = EnrollmentStatus.PENDING;

    @Column(name = "progress_percentage")
    @ColumnDefault("0")
    private Integer progressPercentage = 0;

    @Column(name = "enrolled_date")
    private Instant enrolledDate;

    @Column(name = "completed_date")
    private Instant completedDate;

    @Column(name = "time_spent_seconds")
    @ColumnDefault("0")
    private Long timeSpentSeconds = 0L;

    @Builder
    public CourseEnrollment(Long id, Course course, User user, EnrollmentStatus status,
                            Integer progressPercentage, Instant enrolledDate, Instant completedDate,
                            Long timeSpentSeconds) {
        this.id = id;
        this.course = course;
        this.user = user;
        this.status = status;
        this.progressPercentage = progressPercentage;
        this.enrolledDate = enrolledDate;
        this.completedDate = completedDate;
        this.timeSpentSeconds = timeSpentSeconds;
    }
}

