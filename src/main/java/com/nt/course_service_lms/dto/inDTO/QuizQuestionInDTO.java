package com.nt.course_service_lms.dto.inDTO;

import com.nt.course_service_lms.constants.QuizConstants;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Data Transfer Object for incoming quiz question data.
 * Used for creating and updating quiz questions.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizQuestionInDTO {

    /**
     * ID of the quiz to which this question belongs.
     */
    @NotNull(message = "Quiz ID is required")
    @Positive(message = "Quiz ID must be positive")
    private Long quizId;

    /**
     * The main text/content of the question.
     */
    @NotBlank(message = "Question text is required")
    @Size(max = QuizConstants.MAX_QUESTION_TEXT_LENGTH, message = "Question text cannot exceed "
            + QuizConstants.MAX_QUESTION_TEXT_LENGTH + " characters")
    private String questionText;

    /**
     * The type of the question (e.g., MCQ_SINGLE, MCQ_MULTIPLE, SHORT_ANSWER).
     */
    @NotBlank(message = "Question type is required")
    @Size(max = QuizConstants.MAX_QUESTION_TYPE_LENGTH, message = "Question type cannot exceed "
            + QuizConstants.MAX_QUESTION_TYPE_LENGTH + " characters")
    @Pattern(regexp = "^(MCQ_SINGLE|MCQ_MULTIPLE|SHORT_ANSWER)$",
            message = "Question type must be one of: MCQ_SINGLE, MCQ_MULTIPLE, SHORT_ANSWER")
    private String questionType;

    /**
     * The options for the question, stored as a JSON string.
     */
    @Size(max = QuizConstants.MAX_OPTIONS_LENGTH, message = "Options cannot exceed "
            + QuizConstants.MAX_OPTIONS_LENGTH + " characters")
    private String options; // JSON string for question options

    /**
     * The correct answer(s) for the question, stored as a JSON string.
     */
    @NotBlank(message = "Correct answer is required")
    @Size(max = QuizConstants.MAX_CORRECT_ANSWER_LENGTH, message = "Correct answer cannot exceed "
            + QuizConstants.MAX_CORRECT_ANSWER_LENGTH + " characters")
    private String correctAnswer;

    /**
     * The number of points awarded for this question.
     */
    @NotNull(message = "Points are required")
    @DecimalMin(value = QuizConstants.MIN_QUESTION_POINTS, message = "Points cannot be negative")
    @DecimalMax(value = QuizConstants.MAX_QUESTION_POINTS, message = "Points cannot exceed " + QuizConstants.MAX_QUESTION_POINTS)
    private BigDecimal points;

    /**
     * Optional explanation for the question or answer.
     */
    @Size(max = QuizConstants.MAX_EXPLANATION_LENGTH, message = "Explanation cannot exceed "
            + QuizConstants.MAX_EXPLANATION_LENGTH + " characters")
    private String explanation;

    /**
     * Indicates whether answering this question is mandatory.
     */
    @NotNull(message = "Required field must be specified")
    private Boolean required = true;

    /**
     * Checks equality between this object and another.
     *
     * @param o the object to compare with
     * @return true if the given object is equal to this instance; false otherwise
     */
    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        QuizQuestionInDTO that = (QuizQuestionInDTO) o;
        return Objects.equals(quizId, that.quizId) && Objects.equals(questionText, that.questionText)
                && Objects.equals(questionType, that.questionType) && Objects.equals(options, that.options)
                && Objects.equals(correctAnswer, that.correctAnswer) && Objects.equals(points, that.points)
                && Objects.equals(explanation, that.explanation) && Objects.equals(required, that.required);
    }

    /**
     * Generates a hash code for this DTO.
     *
     * @return hash code based on all fields
     */
    @Override
    public int hashCode() {
        return Objects.hash(quizId, questionText, questionType, options, correctAnswer, points, explanation, required);
    }
}
