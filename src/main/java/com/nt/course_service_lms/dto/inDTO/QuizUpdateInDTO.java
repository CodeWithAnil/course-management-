package com.nt.course_service_lms.dto.inDTO;

import com.nt.course_service_lms.constants.QuizConstants;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Data Transfer Object for updating an existing quiz.
 * <p>
 * This class is used to partially update quiz fields like title, description, time limits, etc.
 * All fields are optional, allowing selective updates.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizUpdateInDTO {

    /**
     * Maximum number of questions that can be shown in a quiz attempt.
     * <p>
     * This limit is used to prevent performance or UI issues when a very large
     * number of questions are configured for a single quiz attempt.
     * </p>
     */
    private static final int MAX_QUESTIONS_TO_SHOW = 100;


    /**
     * Title of the quiz.
     * Maximum 255 characters.
     */
    @Size(max = QuizConstants.MAX_TITLE_LENGTH, message = "Title must not exceed "
            + QuizConstants.MAX_TITLE_LENGTH + " characters")
    private String title;

    /**
     * Description of the quiz.
     * Maximum 1000 characters.
     */
    @Size(max = QuizConstants.MAX_DESCRIPTION_LENGTH, message = "Description must not exceed "
            + QuizConstants.MAX_DESCRIPTION_LENGTH + " characters")
    private String description;

    /**
     * Time limit for completing the quiz, in minutes.
     * Must be between 1 and 600.
     */
    @Min(value = QuizConstants.MIN_TIME_LIMIT_MINUTES, message = "Time limit must be at least "
            + QuizConstants.MIN_TIME_LIMIT_MINUTES + " minute")
    @Max(value = QuizConstants.MAX_TIME_LIMIT_MINUTES, message = "Time limit must not exceed "
            + QuizConstants.MAX_TIME_LIMIT_MINUTES + " minutes (10 hours)")
    private Integer timeLimit;

    /**
     * Number of attempts allowed for the quiz.
     * Must be between 1 and 10.
     */
    @Min(value = QuizConstants.MIN_ATTEMPTS_ALLOWED, message = "At least "
            + QuizConstants.MIN_ATTEMPTS_ALLOWED + " attempt must be allowed")
    @Max(value = QuizConstants.MAX_ATTEMPTS_ALLOWED, message = "Maximum "
            + QuizConstants.MAX_ATTEMPTS_ALLOWED + " attempts allowed")
    private Integer attemptsAllowed;

    /**
     * Minimum score required to pass the quiz.
     * Must be a non-negative decimal with up to 4 integer digits and 2 decimal places.
     */
    @DecimalMin(value = QuizConstants.MIN_PASSING_SCORE, message = "Passing score must be at least "
            + QuizConstants.MIN_PASSING_SCORE)
    @Digits(integer = QuizConstants.MAX_INTEGER_DIGITS_FOR_SCORE,
            fraction = QuizConstants.MAX_FRACTION_DIGITS_FOR_SCORE,
            message = "Passing score must have at most " + QuizConstants.MAX_INTEGER_DIGITS_FOR_SCORE
                    + " integer digits and " + QuizConstants.MAX_FRACTION_DIGITS_FOR_SCORE + " decimal places")
    private BigDecimal passingScore;

    /**
     * Indicates whether the quiz should randomize the question order.
     */
    private Boolean randomizeQuestions;

    /**
     * Indicates whether quiz results should be shown after completion.
     */
    private Boolean showResults;

    /**
     * Indicates whether the quiz is currently active.
     */
    private Boolean isActive;

    /**
     * Number of questions to show during quiz attempt.
     * If null, keeps existing value.
     */
    @Min(value = 1, message = "Questions to show must be at least 1")
    @Max(value = MAX_QUESTIONS_TO_SHOW, message = "Questions to show cannot exceed 100")
    private Integer questionsToShow;

    /**
     * Checks if this object is equal to another.
     *
     * @param o object to compare
     * @return true if all fields are equal; false otherwise
     */
    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        QuizUpdateInDTO that = (QuizUpdateInDTO) o;
        return Objects.equals(title, that.title)
                && Objects.equals(description, that.description)
                && Objects.equals(timeLimit, that.timeLimit)
                && Objects.equals(attemptsAllowed, that.attemptsAllowed)
                && Objects.equals(passingScore, that.passingScore)
                && Objects.equals(randomizeQuestions, that.randomizeQuestions)
                && Objects.equals(showResults, that.showResults)
                && Objects.equals(isActive, that.isActive)
                && Objects.equals(questionsToShow, that.questionsToShow);
    }

    /**
     * Returns a hash code based on the DTO fields.
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(title, description, timeLimit, attemptsAllowed, passingScore,
                randomizeQuestions, showResults, isActive, questionsToShow);
    }
}
