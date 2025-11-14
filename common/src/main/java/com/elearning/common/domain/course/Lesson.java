package com.elearning.common.domain.course;

import com.elearning.common.domain.Auditable;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tb_lesson")
@NoArgsConstructor
public class Lesson extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "video_url")
    private String videoUrl;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "order_sequence")
    private Integer orderSequence;

    @Builder
    public Lesson(Long id, Course course, String title, String description, String content,
                  String videoUrl, Integer durationMinutes, Integer orderSequence) {
        this.id = id;
        this.course = course;
        this.title = title;
        this.description = description;
        this.content = content;
        this.videoUrl = videoUrl;
        this.durationMinutes = durationMinutes;
        this.orderSequence = orderSequence;
    }
}

