package com.nt.course_service_lms.dto.outDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object representing detailed information about a user's quiz attempt.
 * <p>
 * This DTO encapsulates the quiz attempt metadata, user's responses along with correct answers,
 * and score-related details such as total score, maximum possible score, and percentages.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserQuizAttemptDetailsOutDTO {

    /**
     * The quiz attempt information including attempt ID, quiz ID, user ID, etc.
     */
    private QuizAttemptOutDTO quizAttempt;

    /**
     * A list of user's responses paired with the correct answers for each question.
     */
    private List<UserResponseWithCorrectAnswerOutDTO> userResponses;

    /**
     * The total score obtained by the user in this quiz attempt.
     */
    private BigDecimal totalScore;

    /**
     * The maximum possible score achievable in this quiz attempt.
     */
    private BigDecimal maxPossibleScore;

    /**
     * The number of correctly answered questions in this quiz attempt.
     */
    private Long correctAnswers;

    /**
     * The total number of questions included in this quiz attempt.
     */
    private Long totalQuestions;

    /**
     * The percentage score achieved in this quiz attempt.
     */
    private BigDecimal percentageScore;

    /**
     * The type of submission, e.g., "FINAL", "DRAFT", etc.
     */
    private String submissionType;

    /**
     * The timestamp when this quiz attempt was submitted.
     */
    private LocalDateTime submittedAt;
}
