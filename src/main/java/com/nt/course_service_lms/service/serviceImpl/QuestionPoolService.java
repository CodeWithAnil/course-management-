package com.nt.course_service_lms.service.serviceImpl;

import com.nt.course_service_lms.entity.Quiz;
import com.nt.course_service_lms.entity.QuizQuestion;
import com.nt.course_service_lms.exception.ResourceNotFoundException;
import com.nt.course_service_lms.repository.QuizQuestionRepository;
import com.nt.course_service_lms.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Service responsible for retrieving a pool of quiz questions for user attempts.
 * <p>
 * Supports both randomized and sequential selection of questions based on quiz settings.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionPoolService {

    /**
     * Repository for accessing {@link QuizQuestion} entities from the database.
     */
    private final QuizQuestionRepository quizQuestionRepository;

    /**
     * Repository for accessing {@link Quiz} entities from the database.
     */
    private final QuizRepository quizRepository;

    /**
     * Default number of questions to show in a quiz attempt when not explicitly set by admin.
     */
    private static final int DEFAULT_QUESTIONS_TO_SHOW = 10;
    /**
     * Retrieves a list of questions for a user's quiz attempt.
     * <p>
     * Logic:
     * - If {@code questionsToShow} is set by admin: show that many (or fewer if not enough available)
     * - If not set: default to 10 questions or all if fewer than 10
     * - If randomize flag is true: select questions randomly
     * - Otherwise, select sequentially using round-robin logic across attempts
     *
     * @param quizId        ID of the quiz
     * @param userId        ID of the user attempting the quiz
     * @param attemptNumber Attempt number (used for sequential selection)
     * @return List of {@link QuizQuestion} selected for the attempt
     * @throws ResourceNotFoundException if quiz or questions are not found
     */
    public List<QuizQuestion> getQuestionsForAttempt(final Long quizId, final Long userId, final Long attemptNumber) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));

        List<QuizQuestion> allQuestions = quizQuestionRepository.findByQuizIdOrderByPosition(quizId);

        if (allQuestions.isEmpty()) {
            throw new ResourceNotFoundException("No questions found for quiz");
        }

        int totalQuestions = allQuestions.size();
        int questionsToShow = determineQuestionsToShow(quiz.getQuestionsToShow(), totalQuestions);

        log.info("Quiz {}: Total questions = {}, Questions to show = {}, Randomized = {}",
                quizId, totalQuestions, questionsToShow, quiz.getRandomizeQuestions());

        if (questionsToShow >= totalQuestions) {
            // Show all questions
            return quiz.getRandomizeQuestions() ? randomizeQuestions(allQuestions) : allQuestions;
        }

        if (quiz.getRandomizeQuestions()) {
            return getRandomQuestions(allQuestions, questionsToShow);
        } else {
            return getSequentialQuestions(allQuestions, questionsToShow, attemptNumber);
        }
    }

    /**
     * Determines the number of questions to show based on admin settings and question availability.
     * <p>
     * Logic:
     * - If admin provides a valid number: use the minimum of that and total available
     * - If not set: default to {@code DEFAULT_QUESTIONS_TO_SHOW} or total available (whichever is smaller)
     *
     * @param adminSetting   Admin-configured number of questions to show (nullable)
     * @param totalAvailable Total available questions in the quiz
     * @return Number of questions to show in the attempt
     */
    private int determineQuestionsToShow(final Integer adminSetting, final int totalAvailable) {
        if (adminSetting != null && adminSetting > 0) {
            return Math.min(adminSetting, totalAvailable);
        }
        return Math.min(DEFAULT_QUESTIONS_TO_SHOW, totalAvailable);
    }

    /**
     * Returns a shuffled list of all quiz questions.
     *
     * @param questions List of questions to shuffle
     * @return Randomized list of questions
     */
    private List<QuizQuestion> randomizeQuestions(final List<QuizQuestion> questions) {
        List<QuizQuestion> shuffled = new ArrayList<>(questions);
        Collections.shuffle(shuffled);
        return shuffled;
    }

    /**
     * Returns a randomly selected sublist of questions from the full list.
     *
     * @param allQuestions List of all quiz questions
     * @param count        Number of questions to randomly select
     * @return Randomly selected list of questions of specified size
     */
    private List<QuizQuestion> getRandomQuestions(final List<QuizQuestion> allQuestions, final int count) {
        List<QuizQuestion> shuffled = new ArrayList<>(allQuestions);
        Collections.shuffle(shuffled);
        return shuffled.subList(0, Math.min(count, shuffled.size()));
    }

    /**
     * Returns a list of sequentially selected questions using a round-robin pattern.
     * <p>
     * Example: 15 total questions, 10 to show
     * - Attempt 1: 1–10
     * - Attempt 2: 11–15, then 1–5
     * - Attempt 3: 6–15, then 1
     *
     * @param allQuestions   Full list of quiz questions
     * @param questionsToShow Number of questions to include in the result
     * @param attemptNumber   User's current attempt number
     * @return List of sequentially selected quiz questions
     */
    private List<QuizQuestion> getSequentialQuestions(final List<QuizQuestion> allQuestions,
                                                      final int questionsToShow, final Long attemptNumber) {
        int totalQuestions = allQuestions.size();
        int startIndex = (int) ((attemptNumber - 1) * questionsToShow) % totalQuestions;

        List<QuizQuestion> selectedQuestions = new ArrayList<>();

        log.debug("Sequential selection: attempt={}, startIndex={}, questionsToShow={}, totalQuestions={}",
                attemptNumber, startIndex, questionsToShow, totalQuestions);

        for (int i = 0; i < questionsToShow; i++) {
            int currentIndex = (startIndex + i) % totalQuestions;
            selectedQuestions.add(allQuestions.get(currentIndex));

            log.debug("Added question at index {} (question position: {})",
                    currentIndex, allQuestions.get(currentIndex).getPosition());
        }

        return selectedQuestions;
    }
}
