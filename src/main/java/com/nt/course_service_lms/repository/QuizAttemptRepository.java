package com.nt.course_service_lms.repository;

import com.nt.course_service_lms.entity.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing {@link QuizAttempt} entities.
 * <p>
 * Provides database access methods for quiz attempts, supporting both
 * standard and custom queries related to quiz attempt history,
 * statuses, user participation, and more.
 */
@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {

    /**
     * Retrieves all quiz attempts made by a specific user, ordered by creation date descending.
     *
     * @param userId the ID of the user
     * @return list of quiz attempts
     */
    List<QuizAttempt> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Retrieves all quiz attempts for a specific quiz, ordered by creation date descending.
     *
     * @param quizId the ID of the quiz
     * @return list of quiz attempts
     */
    List<QuizAttempt> findByQuizIdOrderByCreatedAtDesc(Long quizId);

    /**
     * Retrieves all quiz attempts for a user and quiz, ordered by attempt number descending.
     *
     * @param userId the ID of the user
     * @param quizId the ID of the quiz
     * @return list of quiz attempts
     */
    List<QuizAttempt> findByUserIdAndQuizIdOrderByAttemptDesc(Long userId, Long quizId);

    /**
     * Retrieves quiz attempts with a specific status, ordered by creation date descending.
     *
     * @param status the attempt status (e.g., "IN_PROGRESS", "COMPLETED")
     * @return list of quiz attempts
     */
    List<QuizAttempt> findByStatusOrderByCreatedAtDesc(String status);

    /**
     * Retrieves the most recent quiz attempt for a user and quiz.
     *
     * @param userId the ID of the user
     * @param quizId the ID of the quiz
     * @return the latest quiz attempt if found
     */
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.userId = :userId AND qa.quizId = :quizId ORDER BY qa.attempt DESC LIMIT 1")
    Optional<QuizAttempt> findLatestAttemptByUserAndQuiz(@Param("userId") Long userId, @Param("quizId") Long quizId);

    /**
     * Counts the number of quiz attempts made by a user for a specific quiz.
     *
     * @param userId the ID of the user
     * @param quizId the ID of the quiz
     * @return number of attempts
     */
    long countByUserIdAndQuizId(Long userId, Long quizId);

    /**
     * Retrieves all quiz attempts for a user and quiz with a specific status.
     *
     * @param userId the ID of the user
     * @param quizId the ID of the quiz
     * @param status the status of the attempt
     * @return list of quiz attempts
     */
    List<QuizAttempt> findByUserIdAndQuizIdAndStatus(Long userId, Long quizId, String status);

    /**
     * Checks if the user has any active (IN_PROGRESS) attempts for a specific quiz.
     *
     * @param userId the ID of the user
     * @param quizId the ID of the quiz
     * @return true if active attempt exists, false otherwise
     */
    @Query("SELECT COUNT(qa) > 0 FROM QuizAttempt qa WHERE qa.userId = :userId AND qa.quizId = :quizId AND qa.status IN"
            + " ('IN_PROGRESS')")
    boolean hasActiveAttempt(@Param("userId") Long userId, @Param("quizId") Long quizId);

    /**
     * Finds the top (most recent) attempt by user and quiz, ordered by attempt descending.
     *
     * @param userId the ID of the user
     * @param quizId the ID of the quiz
     * @return latest quiz attempt
     */
    QuizAttempt findTopByUserIdAndQuizIdOrderByAttemptDesc(Long userId, Long quizId);

    /**
     * Finds an active (IN_PROGRESS) quiz attempt for a specific user and quiz.
     *
     * @param userId the ID of the user
     * @param quizId the ID of the quiz
     * @return optional active attempt
     */
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.userId = :userId AND qa.quizId = :quizId AND qa.status IN ('IN_PROGRESS')")
    Optional<QuizAttempt> findActiveAttemptByUserAndQuiz(@Param("userId") Long userId, @Param("quizId") Long quizId);

    /**
     * Retrieves user quiz attempt details along with quiz metadata for a given course.
     *
     * @param userId   the ID of the user
     * @param courseId the ID of the course
     * @return list of object arrays containing attempt and quiz data
     */
    @Query(value = """
            SELECT qa.quiz_attempt_id, qa.attempt, qa.quiz_id, qa.started_at, qa.finished_at,
                   qa.score_details, qa.status, qa.created_at, qa.updated_at,
                   q.title, q.description, q.time_limit, q.attempts_allowed, q.passing_score
            FROM quiz_attempt qa
            JOIN quiz q ON qa.quiz_id = q.quiz_id
            WHERE qa.user_id = :userId
              AND q.parent_type = 'course'
              AND q.parent_id = :courseId
              AND q.is_active = true
            ORDER BY qa.attempt DESC""",
            nativeQuery = true)
    List<Object[]> findUserAttemptDetailsWithQuizInfo(@Param("userId") Long userId,
                                                      @Param("courseId") Long courseId);

    /**
     * Retrieves all quiz attempt details for a specific user, including responses, questions, and course info.
     *
     * @param userId the ID of the user
     * @return list of object arrays containing detailed quiz attempt data
     */
    @Query(value = """
            SELECT
                qa.quiz_attempt_id,
                qa.attempt,
                qa.quiz_id,
                qa.user_id,
                qa.started_at,
                qa.finished_at,
                qa.score_details,
                qa.status as attempt_status,
                qa.created_at as attempt_created_at,
                qa.updated_at as attempt_updated_at,
                ur.response_id,
                ur.question_id,
                ur.user_answer,
                ur.is_correct,
                ur.points_earned,
                ur.answered_at,
                q.title as quiz_title,
                q.parent_id as course_id,
                qq.question_text,
                qq.question_type,
                qq.points as max_points,
                qq.options,
                qq.correct_answer,
                c.course_id,
                c.owner_id,
                c.title as course_title,
                c.description as course_description,
                c.level as course_level,
                c.is_active as course_active,
                c.created_at as course_created_at,
                c.updated_at as course_updated_at
            FROM quiz_attempt qa
            LEFT JOIN user_response ur ON (
                qa.user_id = ur.user_id
                AND qa.quiz_id = ur.quiz_id
                AND qa.attempt = ur.attempt
            )
            LEFT JOIN quiz q ON qa.quiz_id = q.quiz_id
            LEFT JOIN quiz_question qq ON ur.question_id = qq.question_id
            LEFT JOIN course c ON (q.parent_id = c.course_id AND q.parent_type = 'course')
            WHERE qa.user_id = :userId
                AND qa.status = 'COMPLETED'
            ORDER BY
                qa.quiz_id,
                qa.attempt,
                qq.question_position,
                ur.answered_at""", nativeQuery = true)
    List<Object[]> findQuizAttemptDetailsByUserId(@Param("userId") Long userId);

    /**
     * Retrieves all quiz attempt details for a given course, including user info, quiz metadata, and responses.
     *
     * @param courseId the ID of the course
     * @return list of object arrays containing complete quiz attempt details for the course
     */
    @Query(value = """
            SELECT
                qa.quiz_attempt_id,
                qa.attempt,
                qa.quiz_id,
                q.title AS quiz_title,
                qa.user_id,
                u.username,
                u.email,
                u.firstname,
                u.lastname,
                qa.started_at,
                qa.finished_at,
                qa.score_details,
                qa.status AS attempt_status,
                ur.response_id,
                ur.question_id,
                qq.question_text,
                qq.question_type,
                ur.user_answer,
                ur.is_correct,
                ur.points_earned,
                ur.answered_at,
                qq.options,
                qq.correct_answer
            FROM quiz q
            INNER JOIN quiz_attempt qa ON q.quiz_id = qa.quiz_id
            INNER JOIN users u ON qa.user_id = u.user_id
            LEFT JOIN user_response ur ON qa.quiz_id = ur.quiz_id
                AND qa.user_id = ur.user_id
                AND qa.attempt = ur.attempt
            LEFT JOIN quiz_question qq ON ur.question_id = qq.question_id
            WHERE q.parent_type = 'course'
                AND q.parent_id = :courseId
                AND q.is_active = TRUE
                AND qa.status = 'COMPLETED'
            ORDER BY
                qa.quiz_attempt_id,
                qa.attempt,
                qq.question_position,
                ur.answered_at
            """, nativeQuery = true)
    List<Object[]> findQuizAttemptDetailsByCourseId(@Param("courseId") Long courseId);

    /**
     * Checks if quiz results should be shown based on quiz configuration.
     *
     * @param quizId the ID of the quiz
     * @return true if results should be shown, false otherwise
     */
    @Query("SELECT q.showResults FROM Quiz q WHERE q.quizId = :quizId")
    Boolean shouldShowQuizResults(@Param("quizId") Long quizId);

}
