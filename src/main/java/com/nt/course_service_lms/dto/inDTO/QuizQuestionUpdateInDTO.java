package com.nt.course_service_lms.dto.inDTO;

import com.nt.course_service_lms.constants.QuizConstants;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Data Transfer Object for updating an existing quiz question.
 *
 * <p>This DTO is used when updating an existing question within a quiz.
 * Note that quizId is not included as questions cannot be moved between quizzes.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizQuestionUpdateInDTO {

    /**
     * The text content of the question.
     * Cannot be null or empty.
     */
    @NotBlank(message = "Question text is required")
    @Size(max = QuizConstants.MAX_QUESTION_TEXT_LENGTH_UPDATE, message = "Question text is too long")
    private String questionText;

    /**
     * The type of question (e.g., "SINGLE-MULTIPLE_CHOICE", "MULTI-SELECT", "TEXT").
     * Cannot be null or empty.
     */
    @NotBlank(message = "Question type is required")
    @Size(max = QuizConstants.MAX_QUESTION_TYPE_LENGTH, message = "Question type must not exceed "
            + QuizConstants.MAX_QUESTION_TYPE_LENGTH + " characters")
    private String questionType;

    /**
     * The point value for this question.
     * Must be a positive value with up to 2 decimal places.
     */
    @NotNull(message = "Points are required")
    @DecimalMin(value = QuizConstants.MIN_QUESTION_POINTS_UPDATE, message = "Points must be at least "
            + QuizConstants.MIN_QUESTION_POINTS_UPDATE)
    @DecimalMax(value = QuizConstants.MAX_QUESTION_POINTS_UPDATE, message = "Points must not exceed "
            + QuizConstants.MAX_QUESTION_POINTS_UPDATE)
    @Digits(integer = QuizConstants.MAX_INTEGER_DIGITS_FOR_POINTS,
            fraction = QuizConstants.MAX_FRACTION_DIGITS_FOR_POINTS,
            message = "Points must have at most " + QuizConstants.MAX_INTEGER_DIGITS_FOR_POINTS
                    + " digits before decimal and " + QuizConstants.MAX_FRACTION_DIGITS_FOR_POINTS + " after")
    private BigDecimal points;

    /**
     * Explanation or feedback for the question.
     * Optional field.
     */
    private String explanation;

    /**
     * Flag indicating whether this question is required to be answered.
     */
    @NotNull(message = "Required field is required")
    private Boolean required;

    /**
     * Position/order of the question within the quiz.
     * Must be a positive integer.
     */
    @NotNull(message = "Position is required")
    @Positive(message = "Position must be positive")
    private Integer position;

    /**
     * Compares this object with another for equality.
     *
     * @param o the object to compare with
     * @return true if all fields are equal, false otherwise
     */
    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        QuizQuestionUpdateInDTO that = (QuizQuestionUpdateInDTO) o;
        return Objects.equals(questionText, that.questionText)
                && Objects.equals(questionType, that.questionType)
                && Objects.equals(points, that.points)
                && Objects.equals(explanation, that.explanation)
                && Objects.equals(required, that.required)
                && Objects.equals(position, that.position);
    }

    /**
     * Returns a hash code based on all fields.
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(questionText, questionType, points, explanation, required, position);
    }
}
