package com.nt.course_service_lms.dto.inDTO;

import com.nt.course_service_lms.constants.QuizConstants;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
 * DTO for creating a new quiz.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizCreateInDTO {

    /**
     * Maximum number of questions that can be shown in a quiz attempt.
     * <p>
     * This limit is used to prevent performance or UI issues when a very large
     * number of questions are configured for a single quiz attempt.
     * </p>
     */
    private static final int MAX_QUESTIONS_TO_SHOW = 100;

    /**
     * The type of the parent entity (course, bundle, or course-content).
     */
    @NotBlank(message = "Parent type is required")
    @Size(max = QuizConstants.MAX_PARENT_TYPE_LENGTH, message = "Parent type must not exceed "
            + QuizConstants.MAX_PARENT_TYPE_LENGTH + " characters")
    @Pattern(regexp = "^(course|bundle|course-content)$", message = "Parent type must be 'course', 'bundle', or 'course-content'")
    private String parentType;

    /**
     * The ID of the parent entity.
     */
    @NotNull(message = "Parent ID is required")
    @Positive(message = "Parent ID must be positive")
    private Long parentId;

    /**
     * The title of the quiz.
     */
    @NotBlank(message = "Title is required")
    @Size(max = QuizConstants.MAX_TITLE_LENGTH, message = "Title must not exceed "
            + QuizConstants.MAX_TITLE_LENGTH + " characters")
    private String title;

    /**
     * The description of the quiz.
     */
    @Size(max = QuizConstants.MAX_DESCRIPTION_LENGTH, message = "Description must not exceed "
            + QuizConstants.MAX_DESCRIPTION_LENGTH + " characters")
    private String description;

    /**
     * The time limit for the quiz in minutes.
     */
    @Min(value = QuizConstants.MIN_TIME_LIMIT_MINUTES, message = "Time limit must be at least "
            + QuizConstants.MIN_TIME_LIMIT_MINUTES + " minute")
    @Max(value = QuizConstants.MAX_TIME_LIMIT_MINUTES, message = "Time limit must not exceed "
            + QuizConstants.MAX_TIME_LIMIT_MINUTES + " minutes (10 hours)")
    private Integer timeLimit;

    /**
     * The number of attempts allowed for the quiz.
     */
    @NotNull(message = "Attempts allowed is required")
    @Min(value = QuizConstants.MIN_ATTEMPTS_ALLOWED, message = "At least "
            + QuizConstants.MIN_ATTEMPTS_ALLOWED + " attempt must be allowed")
    @Max(value = QuizConstants.MAX_ATTEMPTS_ALLOWED, message = "Maximum "
            + QuizConstants.MAX_ATTEMPTS_ALLOWED + " attempts allowed")
    private Integer attemptsAllowed = QuizConstants.DEFAULT_ATTEMPTS_ALLOWED;

    /**
     * The minimum score required to pass the quiz.
     */
    @DecimalMin(value = QuizConstants.MIN_PASSING_SCORE, message = "Passing score must be at least "
            + QuizConstants.MIN_PASSING_SCORE)
    @Digits(integer = QuizConstants.MAX_INTEGER_DIGITS_FOR_SCORE, fraction = QuizConstants.MAX_FRACTION_DIGITS_FOR_SCORE,
            message = "Passing score must have at most " + QuizConstants.MAX_INTEGER_DIGITS_FOR_SCORE
                    + " integer digits and " + QuizConstants.MAX_FRACTION_DIGITS_FOR_SCORE + " decimal places")
    private BigDecimal passingScore;

    /**
     * Indicates whether the questions should be randomized.
     */
    private Boolean randomizeQuestions = false;

    /**
     * Indicates whether the quiz results should be shown.
     */
    private Boolean showResults = false;

    /**
     * Indicates whether the quiz is active.
     */
    @NotNull(message = "Active status is required")
    private Boolean isActive = true;

    /**
     * ID of the user who created the quiz.
     */
    @Positive(message = "Created by must be positive")
    private Integer createdBy;

    /**
     * Number of questions to show during quiz attempt.
     * If null, defaults to 10 questions. If total questions < 10, shows all.
     */
    @Min(value = 1, message = "Questions to show must be at least 1")
    @Max(value = MAX_QUESTIONS_TO_SHOW, message = "Questions to show cannot exceed 100")
    private Integer questionsToShow;

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
        QuizCreateInDTO that = (QuizCreateInDTO) o;
        return Objects.equals(parentType, that.parentType)
                && Objects.equals(parentId, that.parentId) && Objects.equals(title, that.title)
                && Objects.equals(description, that.description)
                && Objects.equals(timeLimit, that.timeLimit) && Objects.equals(attemptsAllowed, that.attemptsAllowed)
                && Objects.equals(passingScore, that.passingScore)
                && Objects.equals(randomizeQuestions, that.randomizeQuestions) && Objects.equals(showResults, that.showResults)
                && Objects.equals(isActive, that.isActive) && Objects.equals(createdBy, that.createdBy)
                && Objects.equals(questionsToShow, that.questionsToShow);
    }

    /**
     * Generates a hash code for this DTO.
     *
     * @return hash code based on all fields
     */
    @Override
    public int hashCode() {
        return Objects.hash(parentType, parentId, title, description, timeLimit, attemptsAllowed,
                passingScore, randomizeQuestions, showResults, isActive, createdBy, questionsToShow);
    }
}
