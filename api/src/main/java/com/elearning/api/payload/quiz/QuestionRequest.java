package com.elearning.api.payload.quiz;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "Request payload for creating or updating a question")
public class QuestionRequest {
    @Schema(description = "Question ID (optional - if provided, updates existing question; if not provided, creates new question)")
    private Long id;

    @NotBlank(message = "Question text cannot be empty")
    @Schema(description = "Question text", example = "Which leadership style is most effective in crisis situations?", required = true)
    private String questionText;

    @NotBlank(message = "Question type cannot be empty")
    @Schema(description = "Question type (MULTIPLE_CHOICE, TRUE_FALSE, SHORT_ANSWER, ESSAY)", example = "MULTIPLE_CHOICE", required = true)
    private String questionType;

    @Schema(description = "Points for this question", example = "10")
    private Integer points;

    @Schema(description = "Answer explanation", example = "Autocratic leadership is most effective in crisis situations because it allows for quick decision-making.")
    private String answerExplanation;

    @Schema(description = "Order sequence", example = "1")
    private Integer orderSequence;

    @Schema(description = "Image URL", example = "https://example.com/image.jpg")
    private String imageUrl;

    @Schema(description = "Video URL", example = "https://example.com/video.mp4")
    private String videoUrl;

    @Schema(description = "File URL", example = "https://example.com/file.pdf")
    private String fileUrl;

    @Schema(description = "Voice URL", example = "https://example.com/audio.mp3")
    private String voiceUrl;

    @Schema(description = "Answer options (for multiple choice questions)", example = "[{\"option_text\": \"Option 1\", \"is_correct\": true, \"order_sequence\": 1}]")
    private List<QuestionOptionRequest> options;
}

