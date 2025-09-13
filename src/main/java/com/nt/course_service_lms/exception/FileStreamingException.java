package com.nt.course_service_lms.exception;

/**
 * Exception thrown when an error occurs during file streaming operations.
 * <p>
 * This runtime exception indicates issues related to streaming files,
 * such as read/write failures or interruptions in the streaming process.
 * </p>
 */
public class FileStreamingException extends RuntimeException {

    /**
     * Constructs a new {@code FileStreamingException} with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public FileStreamingException(final String message) {
        super(message);
    }

    /**
     * Constructs a new {@code FileStreamingException} with the specified detail message and cause.
     *
     * @param message the detail message explaining the reason for the exception
     * @param cause   the underlying cause of the exception
     */
    public FileStreamingException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
