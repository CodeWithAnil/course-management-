package com.nt.course_service_lms.constants;

/**
 * Utility class that contains constant values used across the Quiz module.
 * <p>
 * This class is not meant to be instantiated.
 * All members are static and provide standardized messages for validations and exceptions.
 */
public final class QuizConstants {

    /**
     * Private constructor to prevent instantiation of this utility class.
     * Throws an {@link UnsupportedOperationException} if attempted.
     */
    private QuizConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Generic error message for unexpected situations.
     */
    public static final String GENERAL_ERROR = "Something went wrong";

    /**
     * Message used when a quiz already exists.
     */
    public static final String QUIZ_EXISTS = "Quiz Exists";

    /**
     * Message used when no quiz is found.
     */
    public static final String NO_QUIZ_FOUND = "No Quiz Found";

    /**
     * Message used when no quiz is found with a specific ID.
     */
    public static final String NO_QUIZ_WITH_ID = "No Quiz With ID";

    /**
     * Message used when no quiz is found for a specific course ID.
     */
    public static final String NO_QUIZ_FOR_COURSE_ID = "No Quiz Found for this CourseID";

    /**
     * Message used when no quiz is found for a specific course content.
     */
    public static final String NO_QUIZ_FOR_COURSE_CONTENT = "No Quiz Found for this Coursecontent";

    // Quiz time limits (in minutes)

    /**
     * Minimum allowed time limit for a quiz, in minutes.
     */
    public static final int MIN_TIME_LIMIT_MINUTES = 1;

    /**
     * Maximum allowed time limit for a quiz, in minutes.
     */
    public static final int MAX_TIME_LIMIT_MINUTES = 600;
    /**
     * Minimum number of attempts allowed for a quiz.
     */
    public static final int MIN_ATTEMPTS_ALLOWED = 1;

    /**
     * Maximum number of attempts allowed for a quiz.
     */
    public static final int MAX_ATTEMPTS_ALLOWED = 10;

    /**
     * Default number of attempts allowed if not specified.
     */
    public static final int DEFAULT_ATTEMPTS_ALLOWED = 1;
    /**
     * Minimum passing score for a quiz as a string (used for @DecimalMin).
     */
    public static final String MIN_PASSING_SCORE = "0.00";

    /**
     * Maximum number of integer digits allowed in the passing score.
     */
    public static final int MAX_INTEGER_DIGITS_FOR_SCORE = 4;

    /**
     * Maximum number of fractional digits allowed in the passing score.
     */
    public static final int MAX_FRACTION_DIGITS_FOR_SCORE = 2;
    /**
     * Maximum number of characters allowed for the parent type.
     */
    public static final int MAX_PARENT_TYPE_LENGTH = 16;

    /**
     * Maximum number of characters allowed for the quiz title.
     */
    public static final int MAX_TITLE_LENGTH = 255;

    /**
     * Maximum number of characters allowed for the quiz description.
     */
    public static final int MAX_DESCRIPTION_LENGTH = 1000;
    // Quiz Question Constants

    /**
     * Maximum length for the question text.
     */
    public static final int MAX_QUESTION_TEXT_LENGTH = 5000;

    /**
     * Maximum length for the question type.
     */
    public static final int MAX_QUESTION_TYPE_LENGTH = 20;

    /**
     * Maximum length for the options JSON string.
     */
    public static final int MAX_OPTIONS_LENGTH = 10000;

    /**
     * Maximum length for the correct answer JSON string.
     */
    public static final int MAX_CORRECT_ANSWER_LENGTH = 5000;

    /**
     * Minimum value allowed for question points.
     */
    public static final String MIN_QUESTION_POINTS = "0.0";

    /**
     * Maximum value allowed for question points.
     */
    public static final String MAX_QUESTION_POINTS = "999.99";

    /**
     * Maximum length for the explanation text.
     */
    public static final int MAX_EXPLANATION_LENGTH = 5000;
// Quiz Question Update Constants

    /**
     * Maximum length for question text when updating a question.
     */
    public static final int MAX_QUESTION_TEXT_LENGTH_UPDATE = 10000;

    /**
     * Minimum allowed point value for a question.
     */
    public static final String MIN_QUESTION_POINTS_UPDATE = "1.00";

    /**
     * Maximum allowed point value for a question.
     */
    public static final String MAX_QUESTION_POINTS_UPDATE = "1000.00";

    /**
     * Maximum integer digits allowed in the points field.
     */
    public static final int MAX_INTEGER_DIGITS_FOR_POINTS = 4;

    /**
     * Maximum fractional digits allowed in the points field.
     */
    public static final int MAX_FRACTION_DIGITS_FOR_POINTS = 2;
}
