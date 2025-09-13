package com.nt.course_service_lms.controller;

import com.nt.course_service_lms.converters.QuizQuestionConverter;
import com.nt.course_service_lms.dto.inDTO.QuizQuestionInDTO;
import com.nt.course_service_lms.dto.inDTO.UpdateQuizQuestionInDTO;
import com.nt.course_service_lms.dto.outDTO.QuizQuestionOutDTO;
import com.nt.course_service_lms.dto.outDTO.StandardResponseOutDTO;
import com.nt.course_service_lms.entity.QuizQuestion;
import com.nt.course_service_lms.service.QuizQuestionService;
import com.nt.course_service_lms.service.serviceImpl.QuestionPoolService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for managing quiz questions.
 *
 * <p>This controller provides endpoints for creating, retrieving, updating,
 * and deleting quiz questions.</p>
 */
@RestController
@RequestMapping("/api/service-api/quiz-questions")
@RequiredArgsConstructor
@Slf4j
public class QuizQuestionController {

    /**
     * Service for handling quiz question-related business logic.
     * This service provides methods for creating, retrieving, updating,
     * and deleting quiz questions.
     */
    private final QuizQuestionService quizQuestionService;

    /**
     * Service responsible for managing and retrieving quiz question pools,
     * including logic for randomization, sequencing, and question limits per attempt.
     */
    private final QuestionPoolService questionPoolService;

    /**
     * Creates a new quiz question.
     *
     * @param questionCreateInDTO The DTO containing question creation data
     * @return ResponseEntity containing the created question and success message
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseOutDTO<QuizQuestionOutDTO>> createQuestion(
            @Valid @RequestBody final QuizQuestionInDTO questionCreateInDTO) {
        log.info("Received request to create question for quiz ID: {}", questionCreateInDTO.getQuizId());

        QuizQuestionOutDTO createdQuestion = quizQuestionService.createQuestion(questionCreateInDTO);

        log.info("Question created successfully with ID: {}", createdQuestion.getQuestionId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(StandardResponseOutDTO.success(createdQuestion, "Question created successfully"));
    }

    /**
     * Retrieves all questions for a specific quiz.
     *
     * @param quizId The ID of the quiz
     * @return ResponseEntity containing the list of questions and success message
     */
    @GetMapping("/quiz/{quizId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<StandardResponseOutDTO<List<QuizQuestionOutDTO>>> getAllQuestionsByQuizId(
            @PathVariable final Long quizId) {
        log.info("Received request to get all questions for quiz ID: {}", quizId);

        List<QuizQuestionOutDTO> questions = quizQuestionService.getQuestionsByQuizId(quizId);

        log.info("Retrieved {} questions for quiz ID: {}", questions.size(), quizId);
        return ResponseEntity.ok(StandardResponseOutDTO.success(questions,
                String.format("Retrieved %d questions successfully", questions.size())));
    }

    /**
     * Retrieves a specific question by its ID.
     *
     * @param questionId The ID of the question to retrieve
     * @return ResponseEntity containing the question and success message
     */
    @GetMapping("/{questionId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<StandardResponseOutDTO<QuizQuestionOutDTO>> getQuestionById(
            @PathVariable final Long questionId) {
        log.info("Received request to get question with ID: {}", questionId);

        QuizQuestionOutDTO question = quizQuestionService.getQuestionById(questionId);

        log.info("Question retrieved successfully with ID: {}", questionId);
        return ResponseEntity.ok(StandardResponseOutDTO.success(question, "Question retrieved successfully"));
    }

    /**
     * Updates an existing quiz question.
     *
     * @param questionId          The ID of the question to update
     * @param questionUpdateInDTO The DTO containing updated question data
     * @return ResponseEntity containing the updated question and success message
     */
    @PutMapping("/{questionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseOutDTO<QuizQuestionOutDTO>> updateQuestion(
            @PathVariable final Long questionId,
            @Valid @RequestBody final UpdateQuizQuestionInDTO questionUpdateInDTO) {
        log.info("Received request to update question with ID: {}", questionId);

        QuizQuestionOutDTO updatedQuestion = quizQuestionService.updateQuestion(questionId, questionUpdateInDTO);

        log.info("Question updated successfully with ID: {}", questionId);
        return ResponseEntity.ok(StandardResponseOutDTO.success(updatedQuestion, "Question updated successfully"));
    }

    /**
     * Deletes a quiz question by its ID.
     *
     * @param questionId The ID of the question to delete
     * @return ResponseEntity containing success message
     */
    @DeleteMapping("/{questionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseOutDTO<Void>> deleteQuestion(
            @PathVariable final Long questionId) {
        log.info("Received request to delete question with ID: {}", questionId);

        quizQuestionService.deleteQuestion(questionId);

        log.info("Question deleted successfully with ID: {}", questionId);
        return ResponseEntity.ok(StandardResponseOutDTO.success(null, "Question deleted successfully"));
    }

    /**
     * Retrieves a list of quiz questions for a user attempting a quiz.
     *
     * <p>This method dynamically selects questions based on quiz configuration
     * such as randomization or sequential ordering and number of questions to display.
     * The logic is handled in {@link QuestionPoolService}.</p>
     *
     * @param quizId        The ID of the quiz being attempted
     * @param attemptNumber The current attempt number for the quiz
     * @param userId        The ID of the user attempting the quiz
     * @return ResponseEntity containing the list of selected questions
     */
    @GetMapping("/quiz/{quizId}/attempt/{attemptNumber}/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<StandardResponseOutDTO<List<QuizQuestionOutDTO>>> getQuestionsForAttempt(
            @PathVariable final Long quizId,
            @PathVariable final Long attemptNumber,
            @PathVariable final Long userId) {

        log.info("Getting questions for quiz: {}, attempt: {}, user: {}", quizId, attemptNumber, userId);

        List<QuizQuestion> questions = questionPoolService.getQuestionsForAttempt(quizId, userId, attemptNumber);
        List<QuizQuestionOutDTO> questionDTOs = questions.stream()
                .map(QuizQuestionConverter::convertToOutDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(StandardResponseOutDTO.success(questionDTOs,
                "Questions retrieved successfully for attempt"));
    }
}
