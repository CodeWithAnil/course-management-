package com.nt.course_service_lms.service.serviceImpl;

import com.nt.course_service_lms.constants.CommonConstants;
import com.nt.course_service_lms.exception.FileStreamingException;
import com.nt.course_service_lms.service.S3StreamingService;
import io.jsonwebtoken.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.InputStream;

import static com.nt.course_service_lms.constants.S3Constants.DEFAULT_CHUNK_SIZE;

/**
 * Implementation of S3StreamingService that provides file streaming functionality from Amazon S3.
 * This service supports both partial content streaming (range requests) and full file streaming.
 *
 * @author Course Service LMS Team
 * @version 1.0
 * @since 1.0
 */
@Service
public class S3StreamingServiceImpl implements S3StreamingService {

    /**
     * The name of the S3 bucket configured in application properties.
     * Falls back to empty string if not configured.
     */
    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    /**
     * AWS S3 client for performing S3 operations.
     */
    @Autowired
    private S3Client s3Client;

    /**
     * Streams a specific chunk/range of a file from S3 based on the provided range header.
     * Supports HTTP range requests for partial content delivery, enabling features like
     * video streaming, resume downloads, and bandwidth optimization.
     *
     * @param objectKey   the S3 object key (file path) to stream
     * @param rangeHeader the HTTP Range header value (e.g., "bytes=0-1023")
     * @return ResponseEntity containing InputStreamResource with partial content and appropriate headers
     * @throws FileStreamingException if file is not found, S3 errors occur, or IO errors happen
     */
    @Override
    public ResponseEntity<InputStreamResource> streamFileChunk(final String objectKey, final String rangeHeader) {
        try {
            HeadObjectResponse objectMetadata = s3Client.headObject(
                    HeadObjectRequest.builder()
                            .bucket(bucketName)
                            .key(objectKey)
                            .build()
            );
            long contentLength = objectMetadata.contentLength();
            String contentType = objectMetadata.contentType();

            RangeInfo rangeInfo = parseRangeHeader(rangeHeader, contentLength);

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .range("bytes=" + rangeInfo.getStart() + "-" + rangeInfo.getEnd())
                    .build();

            InputStream inputStream = s3Client.getObject(getObjectRequest);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Range", "bytes " + rangeInfo.getStart() + "-" + rangeInfo.getEnd() + "/" + contentLength);
            headers.add("Accept-Ranges", "bytes");
            headers.add("Content-Length", String.valueOf(rangeInfo.getEnd() - rangeInfo.getStart() + 1));
            headers.add("Cache-Control", "no-cache");

            if (contentType != null) {
                headers.setContentType(MediaType.parseMediaType(contentType));
            }

            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .headers(headers)
                    .body(new InputStreamResource(inputStream));

        } catch (NoSuchKeyException e) {
            throw new FileStreamingException("File not found in S3: " + objectKey, e);
        } catch (S3Exception e) {
            throw new FileStreamingException("S3 error while streaming file: " + objectKey, e);
        } catch (IOException e) {
            throw new FileStreamingException("IO error while streaming file: " + objectKey, e);
        } catch (Exception e) {
            throw new FileStreamingException("Unexpected error while streaming file: " + objectKey, e);
        }
    }

    /**
     * Streams the complete file from S3 without any range restrictions.
     * This method is suitable for downloading entire files or when range requests are not needed.
     *
     * @param objectKey the S3 object key (file path) to stream
     * @return ResponseEntity containing InputStreamResource with full file content and appropriate headers
     * @throws FileStreamingException if file is not found, S3 errors occur, or IO errors happen
     */
    @Override
    public ResponseEntity<InputStreamResource> streamFullFile(final String objectKey) {
        try {
            // Get object metadata
            HeadObjectResponse objectMetadata = s3Client.headObject(
                    HeadObjectRequest.builder()
                            .bucket(bucketName)
                            .key(objectKey)
                            .build()
            );

            // Get the object stream
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            InputStream inputStream = s3Client.getObject(getObjectRequest);

            // Create response headers
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Length", String.valueOf(objectMetadata.contentLength()));
            headers.add("Accept-Ranges", "bytes");

            if (objectMetadata.contentType() != null) {
                headers.setContentType(MediaType.parseMediaType(objectMetadata.contentType()));
            }

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new InputStreamResource(inputStream));

        } catch (NoSuchKeyException e) {
            throw new FileStreamingException("File not found in S3: " + objectKey, e);
        } catch (S3Exception e) {
            throw new FileStreamingException("S3 error while streaming file: " + objectKey, e);
        } catch (IOException e) {
            throw new FileStreamingException("IO error while streaming file: " + objectKey, e);
        } catch (Exception e) {
            throw new FileStreamingException("Unexpected error while streaming file: " + objectKey, e);
        }
    }

    /**
     * Retrieves the size (content length) of a file stored in S3.
     * This method performs a HEAD request to get file metadata without downloading the actual content.
     *
     * @param objectKey the S3 object key (file path) to get the size for
     * @return the size of the file in bytes
     * @throws FileStreamingException if unable to retrieve file metadata or if the file doesn't exist
     */
    @Override
    public long getFileSize(final String objectKey) {
        try {
            HeadObjectResponse objectMetadata = s3Client.headObject(
                    HeadObjectRequest.builder()
                            .bucket(bucketName)
                            .key(objectKey)
                            .build()
            );
            return objectMetadata.contentLength();
        } catch (Exception e) {
            throw new FileStreamingException("Failed to retrieve file size for: " + objectKey, e);
        }
    }

    /**
     * Parses the HTTP Range header to determine the byte range to be streamed.
     * If no valid range header is provided, defaults to streaming the first chunk
     * of the file up to DEFAULT_CHUNK_SIZE.
     *
     * @param rangeHeader   the HTTP Range header value (e.g., "bytes=0-1023")
     * @param contentLength the total length of the file content in bytes
     * @return RangeInfo object containing the start and end byte positions
     */
    private RangeInfo parseRangeHeader(final String rangeHeader, final long contentLength) {
        if (rangeHeader == null || !rangeHeader.startsWith("bytes=")) {
            return new RangeInfo(0, Math.min(DEFAULT_CHUNK_SIZE - 1, contentLength - 1));
        }

        try {
            String range = rangeHeader.substring(CommonConstants.NUMBER_SIX); // Remove "bytes="
            String[] parts = range.split("-");

            long start = Long.parseLong(parts[0]);
            long end;

            if (parts.length > 1 && !parts[1].isEmpty()) {
                end = Long.parseLong(parts[1]);
            } else {
                end = Math.min(start + DEFAULT_CHUNK_SIZE - 1, contentLength - 1);
            }

            return new RangeInfo(start, Math.min(end, contentLength - 1));
        } catch (Exception e) {
            return new RangeInfo(0, Math.min(DEFAULT_CHUNK_SIZE - 1, contentLength - 1));
        }
    }

    /**
     * Inner class to hold range information for partial content requests.
     * Contains the start and end byte positions for the requested range.
     */
    private static class RangeInfo {
        /**
         * The starting byte position (inclusive) of the range.
         */
        private final long start;

        /**
         * The ending byte position (inclusive) of the range.
         */
        private final long end;

        /**
         * Constructs a new RangeInfo with the specified start and end positions.
         *
         * @param start the starting byte position (inclusive)
         * @param end   the ending byte position (inclusive)
         */
        RangeInfo(final long start, final long end) {
            this.start = start;
            this.end = end;
        }

        /**
         * Gets the starting byte position of the range.
         *
         * @return the starting byte position (inclusive)
         */
        public long getStart() {
            return start;
        }

        /**
         * Gets the ending byte position of the range.
         *
         * @return the ending byte position (inclusive)
         */
        public long getEnd() {
            return end;
        }
    }
}
