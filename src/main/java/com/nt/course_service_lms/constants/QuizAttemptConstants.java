package com.nt.course_service_lms.constants;

/**
 * Utility class that contains constant values used across the Quiz Attempt module.
 * <p>
 * This class is not meant to be instantiated.
 * All members are static and provide standardized values for quiz attempt operations.
 */
public final class QuizAttemptConstants {

    /**
     * Private constructor to prevent instantiation of this utility class.
     * Throws an {@link UnsupportedOperationException} if attempted.
     */
    private QuizAttemptConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Array index value for quiz attempt ID in query results.
     */
    public static final int INDEX_0 = 0;

    /**
     * Array index value for attempt number in query results.
     */
    public static final int INDEX_1 = 1;

    /**
     * Array index value for quiz ID in query results.
     */
    public static final int INDEX_2 = 2;

    /**
     * Array index value for started at timestamp in query results.
     */
    public static final int INDEX_3 = 3;

    /**
     * Array index value for finished at timestamp in query results.
     */
    public static final int INDEX_4 = 4;

    /**
     * Array index value for user ID in query results.
     */
    public static final int INDEX_5 = 5;

    /**
     * Array index value for score details in query results.
     */
    public static final int INDEX_6 = 6;

    /**
     * Array index value for status in query results.
     */
    public static final int INDEX_7 = 7;

    /**
     * Array index value for created at timestamp in query results.
     */
    public static final int INDEX_8 = 8;

    /**
     * Array index value for updated at timestamp in query results.
     */
    public static final int INDEX_9 = 9;

    /**
     * Array index value for response ID in query results.
     */
    public static final int INDEX_10 = 10;

    /**
     * Array index value for question ID in query results.
     */
    public static final int INDEX_11 = 11;

    /**
     * Array index value for user answer in query results.
     */
    public static final int INDEX_12 = 12;

    /**
     * Array index value for is correct flag in query results.
     */
    public static final int INDEX_13 = 13;

    /**
     * Array index value for points earned in query results.
     */
    public static final int INDEX_14 = 14;

    /**
     * Array index value for answered at timestamp in query results.
     */
    public static final int INDEX_15 = 15;
    /**
     * Array index value for user answer in query results.
     */
    public static final int INDEX_17 = 17;
    /**
     * Array index value for question text in query results.
     */
    public static final int INDEX_18 = 18;
    /**
     * Array index value for points earned in query results.
     */
    public static final int INDEX_19 = 19;
    /**
     * Array index value for options in query results.
     */
    public static final int INDEX_21 = 21;

    /**
     * Array index value for correct answer in query results.
     */
    public static final int INDEX_22 = 22;

    /**
     * Array index value for course ID in query results.
     */
    public static final int INDEX_23 = 23;

    /**
     * Array index value for owner ID in query results.
     */
    public static final int INDEX_24 = 24;

    /**
     * Array index value for course title in query results.
     */
    public static final int INDEX_25 = 25;

    /**
     * Array index value for course description in query results.
     */
    public static final int INDEX_26 = 26;

    /**
     * Array index value for course level in query results.
     */
    public static final int INDEX_27 = 27;

    /**
     * Array index value for course active status in query results.
     */
    public static final int INDEX_28 = 28;

    /**
     * Array index value for course created at timestamp in query results.
     */
    public static final int INDEX_29 = 29;

    /**
     * Array index value for course updated at timestamp in query results.
     */
    public static final int INDEX_30 = 30;

    /**
     * Maximum allowed points earned value.
     */
    public static final int MAX_POINTS_EARNED = 20;

    /**
     * Percentage calculation multiplier value.
     */
    public static final int PERCENTAGE_MULTIPLIER = 100;

    /**
     * String truncation limit for timestamp parsing.
     */
    public static final int TIMESTAMP_STRING_LIMIT = 23;

    /**
     * Scale value for BigDecimal calculations.
     */
    public static final int DECIMAL_SCALE = 2;

    /**
     * Higher precision scale for percentage calculations.
     */
    public static final int PERCENTAGE_SCALE = 4;

    /**
     * Default attempt number for first quiz attempt.
     */
    public static final long DEFAULT_ATTEMPT_NUMBER = 1;

    /**
     * Default points value when points are null.
     */
    public static final long DEFAULT_POINTS_VALUE = 0L;

    /**
     * Default points value as long for calculations.
     */
    public static final long POINTS_ZERO_VALUE = 0L;

    /**
     * Default successful response count.
     */
    public static final long SUCCESS_RESPONSE_COUNT = 1L;

    /**
     * First element index in list.
     */
    public static final int FIRST_ELEMENT_INDEX = 0;

    /**
     * String manipulation - first 500 characters limit.
     */
    public static final int STRING_PREVIEW_LIMIT = 500;
}
