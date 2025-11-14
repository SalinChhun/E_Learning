package com.elearning.common.domain.quiz;

import com.elearning.common.domain.Auditable;
import com.elearning.common.domain.user.User;
import com.elearning.common.enums.Status;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "tb_quiz_attempt")
@NoArgsConstructor
public class QuizAttempt extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "score")
    private Integer score;

    @Column(name = "total_points")
    private Integer totalPoints;

    @Column(name = "percentage_score")
    private Double percentageScore;

    @Column(name = "is_passed")
    private Boolean isPassed;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "time_spent_seconds")
    private Long timeSpentSeconds;

    @Column(name = "status", nullable = false, length = Types.CHAR)
    @JdbcTypeCode(Types.CHAR)
    @Convert(converter = Status.Converter.class)
    private Status status = Status.NORMAL;

    @Builder
    public QuizAttempt(Long id, Quiz quiz, User user, Integer score, Integer totalPoints,
                      Double percentageScore, Boolean isPassed, Instant startedAt,
                      Instant completedAt, Long timeSpentSeconds, Status status) {
        this.id = id;
        this.quiz = quiz;
        this.user = user;
        this.score = score;
        this.totalPoints = totalPoints;
        this.percentageScore = percentageScore;
        this.isPassed = isPassed;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.timeSpentSeconds = timeSpentSeconds;
        this.status = status;
    }
}

