package com.elearning.common.domain.quiz;

import com.elearning.common.domain.Auditable;
import com.elearning.common.domain.course.Course;
import com.elearning.common.enums.QuizType;
import com.elearning.common.enums.Status;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;

@Getter
@Setter
@Entity
@Table(name = "tb_quiz")
@NoArgsConstructor
public class Quiz extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "type", nullable = false, length = Types.CHAR)
    @JdbcTypeCode(Types.CHAR)
    @Convert(converter = QuizType.Converter.class)
    private QuizType type = QuizType.QUIZ;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "passing_score")
    private Integer passingScore;

    @Column(name = "status", nullable = false, length = Types.CHAR)
    @JdbcTypeCode(Types.CHAR)
    @Convert(converter = Status.Converter.class)
    private Status status = Status.NORMAL;

    @Builder
    public Quiz(Long id, String title, String description, QuizType type, Course course,
                Integer durationMinutes, Integer passingScore, Status status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.type = type;
        this.course = course;
        this.durationMinutes = durationMinutes;
        this.passingScore = passingScore;
        this.status = status;
    }
}

