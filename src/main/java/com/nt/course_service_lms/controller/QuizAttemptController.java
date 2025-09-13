package com.nt.course_service_lms.controller;

import com.nt.course_service_lms.constants.CommonConstants;
import com.nt.course_service_lms.dto.inDTO.QuizAttemptCreateInDTO;
import com.nt.course_service_lms.dto.inDTO.QuizAttemptUpdateInDTO;
import com.nt.course_service_lms.dto.outDTO.QuizAttemptDetailsByCourseIDOutDTO;
import com.nt.course_service_lms.dto.outDTO.QuizAttemptDetailsByUserIDOutDTO;
import com.nt.course_service_lms.dto.outDTO.QuizAttemptOutDTO;
import com.nt.course_service_lms.dto.outDTO.StandardResponseOutDTO;
import com.nt.course_service_lms.dto.outDTO.UserQuizAttemptDetailsOutDTO;
import com.nt.course_service_lms.service.QuizAttemptService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing quiz attempts in the Learning Management System.
 * <p>
 * Provides endpoints to create, update, retrieve, delete, and analyze quiz attempts.
 * </p>
 */
@RestController
@RequestMapping("/api/service-api/quiz-attempt")
@Slf4j
public class QuizAttemptController {

    /**
     * Service layer dependency for quiz attempt operations.
     */
    @Autowired
    private QuizAttemptService quizAttemptService;

    /**
     * Creates a new quiz attempt.
     *
     * @param dto the quiz attempt creation data
     * @return ResponseEntity with created quiz attempt
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<QuizAttemptOutDTO> createQuizAttempt(@Valid @RequestBody final QuizAttemptCreateInDTO dto) {
        log.info("REST request to create QuizAttempt for user: {} and quiz: {}", dto.getUserId(), dto.getQuizId());

        final QuizAttemptOutDTO createdAttempt = quizAttemptService.createQuizAttempt(dto);
        return new ResponseEntity<>(createdAttempt, HttpStatus.OK);
    }

    /**
     * Updates an existing quiz attempt.
     *
     * @param quizAttemptId the ID of the quiz attempt
     * @param dto           the updated data
     * @return updated quiz attempt response
     */
    @PutMapping("/{quizAttemptId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<QuizAttemptOutDTO> updateQuizAttempt(
            @PathVariable final Long quizAttemptId,
            @Valid @RequestBody final QuizAttemptUpdateInDTO dto) {
        log.info("REST request to update QuizAttempt with ID: {}", quizAttemptId);

        final QuizAttemptOutDTO updatedAttempt = quizAttemptService.updateQuizAttempt(quizAttemptId, dto);
        return ResponseEntity.ok(updatedAttempt);
    }

    /**
     * Retrieves a quiz attempt by ID.
     *
     * @param quizAttemptId the attempt ID
     * @return ResponseEntity with quiz attempt or 404
     */
    @GetMapping("/{quizAttemptId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<QuizAttemptOutDTO> getQuizAttemptById(@PathVariable final Long quizAttemptId) {
        log.info("REST request to get QuizAttempt with ID: {}", quizAttemptId);

        final Optional<QuizAttemptOutDTO> quizAttempt = quizAttemptService.getQuizAttemptById(quizAttemptId);
        return quizAttempt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Retrieves all quiz attempts with pagination.
     *
     * @param pageable pagination info
     * @return paginated quiz attempts
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<QuizAttemptOutDTO>> getAllQuizAttempts(
            @PageableDefault(size = CommonConstants.NUMBER_TWENTY, sort = "createdAt") final Pageable pageable) {
        log.info("REST request to get all QuizAttempts with pagination");

        final Page<QuizAttemptOutDTO> quizAttempts = quizAttemptService.getAllQuizAttempts(pageable);
        return ResponseEntity.ok(quizAttempts);
    }

    /**
     * Retrieves quiz attempts by user ID.
     *
     * @param userId the user ID
     * @return list of quiz attempts
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<List<QuizAttemptOutDTO>> getQuizAttemptsByUserId(@PathVariable final Long userId) {
        log.info("REST request to get QuizAttempts for user: {}", userId);

        final List<QuizAttemptOutDTO> quizAttempts = quizAttemptService.getQuizAttemptsByUserId(userId);
        return ResponseEntity.ok(quizAttempts);
    }

    /**
     * Retrieves quiz attempts by quiz ID.
     *
     * @param quizId the quiz ID
     * @return list of quiz attempts
     */
    @GetMapping("/quiz/{quizId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<QuizAttemptOutDTO>> getQuizAttemptsByQuizId(@PathVariable final Long quizId) {
        log.info("REST request to get QuizAttempts for quiz: {}", quizId);

        final List<QuizAttemptOutDTO> quizAttempts = quizAttemptService.getQuizAttemptsByQuizId(quizId);
        return ResponseEntity.ok(quizAttempts);
    }

    /**
     * Retrieves quiz attempts for a specific user and quiz.
     *
     * @param userId the user ID
     * @param quizId the quiz ID
     * @return list of matching quiz attempts
     */
    @GetMapping("/user/{userId}/quiz/{quizId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<List<QuizAttemptOutDTO>> getQuizAttemptsByUserAndQuiz(
            @PathVariable final Long userId,
            @PathVariable final Long quizId) {
        log.info("REST request to get QuizAttempts for user: {} and quiz: {}", userId, quizId);

        final List<QuizAttemptOutDTO> quizAttempts = quizAttemptService.getQuizAttemptsByUserAndQuiz(userId, quizId);
        return ResponseEntity.ok(quizAttempts);
    }

    /**
     * Retrieves quiz attempts by status.
     *
     * @param status the attempt status
     * @return list of attempts with given status
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<QuizAttemptOutDTO>> getQuizAttemptsByStatus(@PathVariable final String status) {
        log.info("REST request to get QuizAttempts with status: {}", status);

        final List<QuizAttemptOutDTO> quizAttempts = quizAttemptService.getQuizAttemptsByStatus(status);
        return ResponseEntity.ok(quizAttempts);
    }

    /**
     * Retrieves the latest attempt by user and quiz.
     *
     * @param userId the user ID
     * @param quizId the quiz ID
     * @return latest attempt or 404
     */
    @GetMapping("/user/{userId}/quiz/{quizId}/latest")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<QuizAttemptOutDTO> getLatestAttemptByUserAndQuiz(
            @PathVariable final Long userId,
            @PathVariable final Long quizId) {
        log.info("REST request to get latest QuizAttempt for user: {} and quiz: {}", userId, quizId);

        final Optional<QuizAttemptOutDTO> latestAttempt = quizAttemptService.getLatestAttemptByUserAndQuiz(userId, quizId);
        return latestAttempt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Deletes a quiz attempt by ID.
     *
     * @param quizAttemptId the attempt ID
     * @return no content response
     */
    @DeleteMapping("/{quizAttemptId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteQuizAttempt(@PathVariable final Long quizAttemptId) {
        log.info("REST request to delete QuizAttempt with ID: {}", quizAttemptId);

        quizAttemptService.deleteQuizAttempt(quizAttemptId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Marks a quiz attempt as completed.
     *
     * @param quizAttemptId the attempt ID
     * @param scoreDetails  optional score data
     * @return completed attempt response
     */
    @PatchMapping("/{quizAttemptId}/complete")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<QuizAttemptOutDTO> completeAttempt(
            @PathVariable final Long quizAttemptId,
            @RequestBody(required = false) final String scoreDetails) {
        log.info("REST request to complete QuizAttempt with ID: {}", quizAttemptId);

        final QuizAttemptOutDTO completedAttempt = quizAttemptService.completeAttempt(quizAttemptId, scoreDetails);
        return ResponseEntity.ok(completedAttempt);
    }

    /**
     * Marks a quiz attempt as abandoned.
     *
     * @param quizAttemptId the attempt ID
     * @return abandoned attempt response
     */
    @PatchMapping("/{quizAttemptId}/abandon")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<QuizAttemptOutDTO> abandonAttempt(@PathVariable final Long quizAttemptId) {
        log.info("REST request to abandon QuizAttempt with ID: {}", quizAttemptId);

        final QuizAttemptOutDTO abandonedAttempt = quizAttemptService.abandonAttempt(quizAttemptId);
        return ResponseEntity.ok(abandonedAttempt);
    }

    /**
     * Marks a quiz attempt as timed out.
     *
     * @param quizAttemptId the attempt ID
     * @return timed-out attempt response
     */
    @PatchMapping("/{quizAttemptId}/timeout")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<QuizAttemptOutDTO> timeOutAttempt(@PathVariable final Long quizAttemptId) {
        log.info("REST request to time out QuizAttempt with ID: {}", quizAttemptId);

        final QuizAttemptOutDTO timedOutAttempt = quizAttemptService.timeOutAttempt(quizAttemptId);
        return ResponseEntity.ok(timedOutAttempt);
    }

    /**
     * Checks if a quiz attempt exists.
     *
     * @param quizAttemptId the attempt ID
     * @return 200 if exists, 404 otherwise
     */
    @RequestMapping(value = "/{quizAttemptId}", method = RequestMethod.HEAD)
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<Void> checkQuizAttemptExists(@PathVariable final Long quizAttemptId) {
        log.info("REST request to check if QuizAttempt exists with ID: {}", quizAttemptId);

        final boolean exists = quizAttemptService.existsById(quizAttemptId);
        return exists ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    /**
     * Counts quiz attempts for a user and quiz.
     *
     * @param userId the user ID
     * @param quizId the quiz ID
     * @return count of attempts
     */
    @GetMapping("/user/{userId}/quiz/{quizId}/count")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<Long> countAttemptsByUserAndQuiz(
            @PathVariable final Long userId,
            @PathVariable final Long quizId) {
        log.info("REST request to count QuizAttempts for user: {} and quiz: {}", userId, quizId);

        final long count = quizAttemptService.countAttemptsByUserAndQuiz(userId, quizId);
        return ResponseEntity.ok(count);
    }

    /**
     * Health check endpoint.
     *
     * @return OK response indicating controller is operational
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("QuizAttempt Controller is healthy");
    }
    /**
     * Retrieves quiz attempt details for a specific user in a specific course.
     * Accessible by users with ADMIN or EMPLOYEE roles.
     *
     * @param userId   the ID of the user whose attempt details are to be fetched
     * @param courseId the ID of the course for which quiz attempts are to be fetched
     * @return StandardResponseOutDTO containing a list of UserQuizAttemptDetailsOutDTO
     */
    @GetMapping("/user/{userId}/quiz/course/{courseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public StandardResponseOutDTO<List<UserQuizAttemptDetailsOutDTO>> getUserAttemptDetails(
            @PathVariable final Long userId, @PathVariable final Long courseId) {
        String userRole = getCurrentUserRole();
        List<UserQuizAttemptDetailsOutDTO> quizSubmissionResultOutDTOS =
                quizAttemptService.getUserAttemptDetails(userId, courseId, userRole);
        return StandardResponseOutDTO.success(quizSubmissionResultOutDTOS, "Fetched user attempt details");
    }
    /**
     * Retrieves all quiz attempt details made by a specific user across all courses.
     * Accessible by users with ADMIN or EMPLOYEE roles.
     *
     * @param userId the ID of the user whose quiz attempt details are to be retrieved
     * @return StandardResponseOutDTO containing a list of QuizAttemptDetailsByUserIDOutDTO
     */

    @GetMapping("/quiz-attempt-details/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public StandardResponseOutDTO<List<QuizAttemptDetailsByUserIDOutDTO>> getQuizAttemptDetailsByUserId(
            @PathVariable final Long userId) {
        String userRole = getCurrentUserRole();
        List<QuizAttemptDetailsByUserIDOutDTO> quizAttemptDetailsByUserIDOutDTOS =
                quizAttemptService.getQuizAttemptDetailsByUserID(userId, userRole);
        return StandardResponseOutDTO.success(quizAttemptDetailsByUserIDOutDTOS, "User Attempt Details Fetched Successfully");
    }
    /**
     * Retrieves quiz attempt details for all users for a specific course.
     * Only accessible by users with the ADMIN role.
     *
     * @param courseId the ID of the course for which attempt details are to be fetched
     * @return StandardResponseOutDTO containing a list of QuizAttemptDetailsByCourseIDOutDTO
     */
    @GetMapping("/quiz-attempt-details/course/{courseId}")
    @PreAuthorize("hasRole('ADMIN')")
    public StandardResponseOutDTO<List<QuizAttemptDetailsByCourseIDOutDTO>> getQuizAttemptDetailsByCourseId(
            @PathVariable final Long courseId) {
        String userRole = getCurrentUserRole();
        List<QuizAttemptDetailsByCourseIDOutDTO> quizAttemptDetailsByUserIDOutDTOS =
                quizAttemptService.getQuizAttemptDetailsByCourseID(courseId, userRole);
        return StandardResponseOutDTO.success(quizAttemptDetailsByUserIDOutDTOS, "User Attempt Details Fetched Successfully");
    }
    /**
     * Extracts the current user's role from Spring Security context.
     * @return the user's highest role (ADMIN takes precedence over EMPLOYEE)
     */
    private String getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getAuthorities() != null) {
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority())
                            || "ADMIN".equals(authority.getAuthority()));
            if (isAdmin) {
                return "ADMIN";
            }

            boolean isEmployee = authentication.getAuthorities().stream()
                    .anyMatch(authority -> "ROLE_EMPLOYEE".equals(authority.getAuthority())
                            || "EMPLOYEE".equals(authority.getAuthority()));
            if (isEmployee) {
                return "EMPLOYEE";
            }
        }
        return "USER";
    }
}
