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
@Table(name = "tb_question_option")
@NoArgsConstructor
public class QuestionOption extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "option_text", nullable = false, columnDefinition = "TEXT")
    private String optionText;

    @Column(name = "is_correct", nullable = false)
    private Boolean isCorrect = false;

    @Column(name = "order_sequence", nullable = false)
    private Integer orderSequence;

    @Builder
    public QuestionOption(Long id, Question question, String optionText,
                          Boolean isCorrect, Integer orderSequence) {
        this.id = id;
        this.question = question;
        this.optionText = optionText;
        this.isCorrect = isCorrect;
        this.orderSequence = orderSequence;
    }
}

