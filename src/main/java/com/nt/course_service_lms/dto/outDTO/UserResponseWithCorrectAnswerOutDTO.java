package com.nt.course_service_lms.dto.outDTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object representing a user's response to a quiz question,
 * including the correct answer and grading details.
 * <p>
 * This DTO is used to transfer detailed response data from the backend to the client,
 * encompassing information such as the question, user's answer, correctness, points earned,
 * and timestamps.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseWithCorrectAnswerOutDTO {

    /**
     * Unique identifier of the user response record.
     */
    private Long responseId;

    /**
     * Identifier of the user who submitted this response.
     */
    private Long userId;

    /**
     * Identifier of the quiz this response belongs to.
     */
    private Long quizId;

    /**
     * Identifier of the question being answered.
     */
    private Long questionId;

    /**
     * The text of the quiz question.
     */
    private String questionText;

    /**
     * The attempt number of this quiz by the user.
     * Useful for tracking multiple attempts.
     */
    private Long attempt;

    /**
     * The possible options for the question, if applicable.
     * Typically, a serialized list or JSON string.
     */
    private String options;

    /**
     * The user's answer in JSON format.
     * This can represent various response types depending on question type.
     */
    private String userAnswer;

    /**
     * The correct answer to the question.
     * Used for comparison and feedback.
     */
    private String correctAnswer;

    /**
     * Indicates whether the user's answer is correct or not.
     */
    private Boolean isCorrect;

    /**
     * The points earned by the user for this response.
     */
    private BigDecimal pointsEarned;

    /**
     * Timestamp when this answer was submitted.
     * Formatted as an ISO 8601 date-time string.
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime answeredAt;
}
