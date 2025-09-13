package com.nt.course_service_lms.service;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;

/**
 * Service interface for streaming files stored in Amazon S3.
 *
 * <p>Provides methods to stream full files or partial content (chunks) from S3 objects,
 * supporting HTTP range requests for efficient media streaming or large file handling.
 * Also provides file metadata retrieval like file size.</p>
 *
 * <p>This interface abstracts S3 access and streaming logic for use in controllers or other services.</p>
 */
public interface S3StreamingService {

    /**
     * Streams a specific byte range chunk of a file from S3.
     *
     * <p>This method supports HTTP range requests, allowing clients to request
     * partial content of a file. Useful for video/audio streaming or resuming downloads.
     *
     * @param objectKey   the unique key identifying the file in S3 storage.
     * @param rangeHeader the HTTP Range header value specifying the byte range to stream.
     *                    Example: "bytes=0-1023"
     * @return a {@link ResponseEntity} containing an {@link InputStreamResource} for the requested file chunk,
     * with appropriate HTTP headers (e.g., Content-Range, Content-Length).
     * @throws IllegalArgumentException if objectKey or rangeHeader is null or invalid.
     */
    ResponseEntity<InputStreamResource> streamFileChunk(String objectKey, String rangeHeader);

    /**
     * Streams the entire file from S3.
     *
     * <p>This method returns the full content of the file identified by the given object key.
     *
     * @param objectKey the unique key identifying the file in S3 storage.
     * @return a {@link ResponseEntity} containing an {@link InputStreamResource} for the full file,
     * with appropriate HTTP headers such as Content-Length.
     * @throws IllegalArgumentException if objectKey is null or invalid.
     */
    ResponseEntity<InputStreamResource> streamFullFile(String objectKey);

    /**
     * Retrieves the size of the file stored in S3.
     *
     * @param objectKey the unique key identifying the file in S3 storage.
     * @return the size of the file in bytes.
     * @throws IllegalArgumentException if objectKey is null or invalid.
     */
    long getFileSize(String objectKey);
}
