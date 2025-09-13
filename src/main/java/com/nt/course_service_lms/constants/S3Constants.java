package com.nt.course_service_lms.constants;

/**
 * A utility class containing constants used for AWS S3 file upload and download configurations.
 * <p>
 * These values are optimized for performance and compatibility with Amazon S3 multipart uploads.
 * </p>
 */
public final class S3Constants {

    /**
     * Private constructor to prevent instantiation.
     * <p>
     * This class is intended to be used only in a static context.
     * </p>
     *
     * @throws UnsupportedOperationException always, since this is a utility class
     */
    private S3Constants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * The minimum allowed part size for S3 multipart uploads (5MB).
     */
    public static final long MIN_PART_SIZE = 5 * 1024 * 1024L;

    /**
     * The optimal part size for balancing performance and memory usage (16MB).
     */
    public static final long OPTIMAL_PART_SIZE = 16 * 1024 * 1024L;

    /**
     * The maximum allowed part size for S3 multipart uploads (100MB).
     */
    public static final long MAX_PART_SIZE = 100 * 1024 * 1024L;

    /**
     * The threshold file size beyond which multipart upload should be used (8MB).
     */
    public static final long MULTIPART_THRESHOLD = 8 * 1024 * 1024L;

    /**
     * The chunk size used when streaming files to or from S3 (1MB).
     */
    public static final int STREAMING_CHUNK_SIZE = 1024 * 1024;

    /**
     * The buffer size used for temporary in-memory storage during S3 operations (2MB).
     */
    public static final int BUFFER_SIZE = 2 * 1024 * 1024;

    /**
     * The default chunk size used for S3 file transfer operations (5MB).
     */
    public static final long DEFAULT_CHUNK_SIZE = 1024 * 1024 * 5;
    /**
     * File size threshold for small files (100MB).
     * Files below this size use 8MB parts for optimal performance.
     */
    public static final long SMALL_FILE_THRESHOLD = 100 * 1024 * 1024L;

    /**
     * File size threshold for medium files (1GB).
     * Files below this size use 16MB parts for optimal performance.
     */
    public static final long MEDIUM_FILE_THRESHOLD = 1024 * 1024 * 1024L;

    /**
     * File size threshold for large files (10GB).
     * Files below this size use 32MB parts for optimal performance.
     */
    public static final long LARGE_FILE_THRESHOLD = 10L * 1024 * 1024 * 1024L;

    // Part sizes for different file categories

    /**
     * Part size for small files (8MB).
     */
    public static final long PART_SIZE_8MB = 8 * 1024 * 1024L;

    /**
     * Part size for medium files (16MB).
     */
    public static final long PART_SIZE_16MB = 16 * 1024 * 1024L;

    /**
     * Part size for large files (32MB).
     */
    public static final long PART_SIZE_32MB = 32 * 1024 * 1024L;

    // Progress and logging constants

    /**
     * Percentage multiplier for progress calculations (100).
     */
    public static final double PROGRESS_LOG_INTERVAL = 100.0;

    /**
     * Log progress every N parts during multipart upload (10).
     */
    public static final int LOG_EVERY_N_PARTS = 10;

    /**
     * Log progress every N parts during adaptive streaming upload (20).
     */
    public static final int ADAPTIVE_LOG_EVERY_N_PARTS = 20;

    // S3 service limits

    /**
     * Maximum number of parts allowed in S3 multipart upload (10,000).
     * Used to calculate part sizes for very large files.
     */
    public static final long MAX_PARTS_LIMIT = 9000L; // Using 9000 instead of 10000 for safety margin
}
