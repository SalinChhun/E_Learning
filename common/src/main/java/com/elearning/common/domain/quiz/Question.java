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
@Table(name = "tb_question")
@NoArgsConstructor
public class Question extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    private String questionText;

    @Column(name = "question_type", nullable = false, length = 50)
    private String questionType; // MULTIPLE_CHOICE, TRUE_FALSE, SHORT_ANSWER, ESSAY

    @Column(name = "points", nullable = false)
    private Integer points = 10;

    @Column(name = "answer_explanation", columnDefinition = "TEXT")
    private String answerExplanation;

    @Column(name = "order_sequence", nullable = false)
    private Integer orderSequence;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "video_url")
    private String videoUrl;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "voice_url")
    private String voiceUrl;

    @Builder
    public Question(Long id, Quiz quiz, String questionText, String questionType,
                    Integer points, String answerExplanation, Integer orderSequence,
                    String imageUrl, String videoUrl, String fileUrl, String voiceUrl) {
        this.id = id;
        this.quiz = quiz;
        this.questionText = questionText;
        this.questionType = questionType;
        this.points = points;
        this.answerExplanation = answerExplanation;
        this.orderSequence = orderSequence;
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
        this.fileUrl = fileUrl;
        this.voiceUrl = voiceUrl;
    }
}

