package com.nt.course_service_lms.dto.outDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object representing the result of a quiz submission.
 * <p>
 * Contains details about the quiz attempt, user responses, scoring, and submission metadata.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizSubmissionResultOutDTO {

    /**
     * Details of the quiz attempt.
     */
    private QuizAttemptOutDTO quizAttempt;

    /**
     * List of user responses for the quiz questions.
     */
    private List<UserResponseOutDTO> userResponses;

    /**
     * The total score achieved by the user in the quiz.
     */
    private BigDecimal totalScore;

    /**
     * The maximum possible score for the quiz.
     */
    private BigDecimal maxPossibleScore;

    /**
     * Number of correctly answered questions.
     */
    private Long correctAnswers;

    /**
     * Total number of questions in the quiz.
     */
    private Long totalQuestions;

    /**
     * Percentage score achieved in the quiz.
     */
    private BigDecimal percentageScore;

    /**
     * The type of submission (e.g., manual, auto).
     */
    private String submissionType;

    /**
     * Timestamp when the quiz was submitted.
     */
    private LocalDateTime submittedAt;
}
