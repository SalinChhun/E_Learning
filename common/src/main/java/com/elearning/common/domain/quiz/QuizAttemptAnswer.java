package com.elearning.common.domain.quiz;

import com.elearning.common.domain.Auditable;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tb_quiz_attempt_answer")
@NoArgsConstructor
public class QuizAttemptAnswer extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id", nullable = false)
    private QuizAttempt attempt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_option_id")
    private QuestionOption selectedOption;

    @Column(name = "answer_text", columnDefinition = "TEXT")
    private String answerText; // For short answer or essay questions

    @Column(name = "is_correct")
    private Boolean isCorrect;

    @Column(name = "points_earned")
    private Integer pointsEarned;

    @Builder
    public QuizAttemptAnswer(Long id, QuizAttempt attempt, Question question,
                            QuestionOption selectedOption, String answerText,
                            Boolean isCorrect, Integer pointsEarned) {
        this.id = id;
        this.attempt = attempt;
        this.question = question;
        this.selectedOption = selectedOption;
        this.answerText = answerText;
        this.isCorrect = isCorrect;
        this.pointsEarned = pointsEarned;
    }
}

