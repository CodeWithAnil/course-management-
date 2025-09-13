package com.nt.course_service_lms.service.serviceImpl;

import com.nt.course_service_lms.constants.CommonConstants;
import com.nt.course_service_lms.constants.S3Constants;
import com.nt.course_service_lms.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.AbortMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompletedMultipartUpload;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;
import software.amazon.awssdk.services.s3.model.UploadPartResponse;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.nt.course_service_lms.constants.S3Constants.BUFFER_SIZE;
import static com.nt.course_service_lms.constants.S3Constants.MAX_PART_SIZE;
import static com.nt.course_service_lms.constants.S3Constants.MIN_PART_SIZE;
import static com.nt.course_service_lms.constants.S3Constants.MULTIPART_THRESHOLD;
import static com.nt.course_service_lms.constants.S3Constants.OPTIMAL_PART_SIZE;
import static com.nt.course_service_lms.constants.S3Constants.STREAMING_CHUNK_SIZE;

/**
 * Service implementation for handling AWS S3 file operations including upload, download, and management.
 *
 * <p>This service provides optimized file upload strategies based on file size:
 * <ul>
 *   <li>Small files (< 8MB): Direct upload for maximum efficiency</li>
 *   <li>Large files (>= 8MB): Multipart upload with intelligent part sizing</li>
 *   <li>Unknown size files: Adaptive streaming upload</li>
 * </ul>
 *
 * <p>The service automatically handles error recovery, cleanup operations, and provides
 * comprehensive logging for monitoring upload progress and troubleshooting.
 *
 * @author Course Service LMS Team
 * @version 1.0
 * @since 1.0
 */
@Service
@Slf4j
public class S3FileService {

    /**
     * AWS S3 client for performing S3 operations.
     */
    @Autowired
    private S3Client s3Client;

    /**
     * The name of the S3 bucket where files will be stored.
     * Configured via application properties.
     */
    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    /**
     * Uploads a file to S3 using the most efficient method based on file size.
     *
     * <p>This method automatically selects the optimal upload strategy:
     * <ul>
     *   <li>Direct upload for small files (< 8MB)</li>
     *   <li>Optimized multipart upload for large files (>= 8MB)</li>
     *   <li>Adaptive streaming for files with unknown size</li>
     * </ul>
     *
     * @param file   the multipart file to upload. Must not be empty.
     * @param folder the folder/prefix in the S3 bucket where the file should be stored
     * @return the generated unique filename of the uploaded file
     * @throws IOException               if file upload fails due to I/O errors
     * @throws ResourceNotFoundException if the file is empty
     * @throws IllegalArgumentException  if file or folder parameters are invalid
     */
    public String uploadFile(final MultipartFile file, final String folder) throws IOException {
        if (file.isEmpty()) {
            throw new ResourceNotFoundException("File not found");
        }

        String fileName = generateFileName(folder, file.getOriginalFilename());
        long fileSize = file.getSize();

        // Use size-based strategy for optimal performance
        if (fileSize <= 0) {
            // Unknown size - use adaptive streaming
            return uploadWithAdaptiveStreaming(file, fileName, folder);
        } else if (fileSize < MULTIPART_THRESHOLD) {
            // Small files - direct upload is most efficient
            return uploadSmallFile(file, fileName, folder);
        } else {
            // Large files - optimized multipart upload
            return uploadLargeFileOptimized(file, fileName, fileSize, folder);
        }
    }

    /**
     * Uploads small files (< 8MB) using direct upload for optimal performance.
     *
     * @param file     the multipart file to upload
     * @param fileName the generated unique filename
     * @param folder   the S3 folder/prefix
     * @return the uploaded filename
     * @throws IOException if the upload fails
     */
    private String uploadSmallFile(final MultipartFile file, final String fileName, final String folder) throws IOException {
        try {
            String fileNameWithFolder = folder + "/" + fileName;
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileNameWithFolder)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            // Direct upload with input stream - most efficient for small files
            s3Client.putObject(putRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            return fileName;

        } catch (S3Exception e) {
            log.error("S3 error during small file upload: {}", e.getMessage());
            throw new IOException("Failed to upload small file: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error during small file upload: {}", e.getMessage());
            throw new IOException("Failed to upload small file: " + e.getMessage(), e);
        }
    }

    /**
     * Uploads large files using optimized multipart upload with intelligent part sizing.
     *
     * <p>This method calculates optimal part sizes based on file size to minimize
     * upload time while staying within S3 limits. Progress is logged periodically.
     *
     * @param file     the multipart file to upload
     * @param fileName the generated unique filename
     * @param fileSize the size of the file in bytes
     * @param folder   the S3 folder/prefix
     * @return the uploaded filename
     * @throws IOException if the upload fails
     */
    private String uploadLargeFileOptimized(final MultipartFile file, final String fileName,
                                            final long fileSize, final String folder) throws IOException {
        // Calculate optimal part size based on file size
        long partSize = calculateOptimalPartSize(fileSize);
        String fileNameWithFolder = folder + "/" + fileName;
        String uploadId = null;

        try {
            CreateMultipartUploadRequest createRequest = CreateMultipartUploadRequest.builder()
                    .bucket(bucketName)
                    .key(fileNameWithFolder)
                    .contentType(file.getContentType())
                    .build();

            CreateMultipartUploadResponse createResponse = s3Client.createMultipartUpload(createRequest);
            uploadId = createResponse.uploadId();

            List<CompletedPart> completedParts = new ArrayList<>();

            try (BufferedInputStream bufferedInput = new BufferedInputStream(file.getInputStream(), BUFFER_SIZE)) {

                int partNumber = 1;
                long remainingBytes = fileSize;

                while (remainingBytes > 0) {
                    // Calculate current part size
                    long currentPartSize = Math.min(partSize, remainingBytes);

                    // Read part data efficiently
                    byte[] partData = readPartData(bufferedInput, currentPartSize);
                    if (partData.length == 0) {
                        break;
                    }
                    // Upload part
                    UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                            .bucket(bucketName)
                            .key(fileNameWithFolder)
                            .uploadId(uploadId)
                            .partNumber(partNumber)
                            .contentLength((long) partData.length)
                            .build();

                    UploadPartResponse uploadPartResponse = s3Client.uploadPart(uploadPartRequest,
                            RequestBody.fromBytes(partData));

                    completedParts.add(CompletedPart.builder()
                            .partNumber(partNumber)
                            .eTag(uploadPartResponse.eTag())
                            .build());

                    remainingBytes -= partData.length;
                    partNumber++;
                    // Progress logging (optional)
                    double progress = ((double) (fileSize - remainingBytes) / fileSize) * S3Constants.PROGRESS_LOG_INTERVAL;
                    if (partNumber % S3Constants.LOG_EVERY_N_PARTS == 0) { // Log every 10th part to avoid spam
                        System.out.printf("Upload progress: %.1f%% (%d/%d parts)%n",
                                progress, partNumber - 1, (int) Math.ceil((double) fileSize / partSize));
                    }
                }

                // Complete upload
                CompleteMultipartUploadRequest completeRequest = CompleteMultipartUploadRequest.builder()
                        .bucket(bucketName)
                        .key(fileNameWithFolder)
                        .uploadId(uploadId)
                        .multipartUpload(CompletedMultipartUpload.builder().parts(completedParts).build())
                        .build();

                s3Client.completeMultipartUpload(completeRequest);

                System.out.printf("Upload completed: %s (%d parts, %d bytes)%n",
                        fileNameWithFolder, completedParts.size(), fileSize);

                return fileName;
            }

        } catch (S3Exception e) {
            log.error("S3 error during large file upload: {}", e.getMessage());
            // Cleanup on failure
            if (uploadId != null) {
                abortMultipartUpload(bucketName, fileNameWithFolder, uploadId);
            }
            throw new IOException("Failed to upload large file: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error during large file upload: {}", e.getMessage());
            // Cleanup on failure
            if (uploadId != null) {
                abortMultipartUpload(bucketName, fileNameWithFolder, uploadId);
            }
            throw new IOException("Failed to upload large file: " + e.getMessage(), e);
        }
    }

    /**
     * Uploads files with unknown sizes using adaptive streaming approach.
     *
     * <p>This method reads the file in chunks and uploads parts as they become available,
     * making it suitable for streaming uploads where the total file size is not known upfront.
     *
     * @param file     the multipart file to upload
     * @param fileName the generated unique filename
     * @param folder   the S3 folder/prefix
     * @return the uploaded filename
     * @throws IOException if the upload fails
     */
    private String uploadWithAdaptiveStreaming(final MultipartFile file, final String fileName,
                                               final String folder) throws IOException {
        String fileNameWithFolder = folder + "/" + fileName;
        String uploadId = null;

        try {
            CreateMultipartUploadRequest createRequest = CreateMultipartUploadRequest.builder()
                    .bucket(bucketName)
                    .key(fileNameWithFolder)
                    .contentType(file.getContentType())
                    .build();

            CreateMultipartUploadResponse createResponse = s3Client.createMultipartUpload(createRequest);
            uploadId = createResponse.uploadId();

            List<CompletedPart> completedParts = new ArrayList<>();

            try (BufferedInputStream bufferedInput = new BufferedInputStream(file.getInputStream(), BUFFER_SIZE)) {

                int partNumber = 1;
                long totalBytesRead = 0;

                while (true) {
                    // Use optimal part size for unknown file sizes
                    byte[] partData = readPartData(bufferedInput, OPTIMAL_PART_SIZE);

                    if (partData.length == 0) {
                        break;
                    }

                    // Ensure minimum part size (except for last part)
                    if (partData.length < MIN_PART_SIZE) {
                        // Try to read more data to meet minimum
                        byte[] additionalData = readPartData(bufferedInput, MIN_PART_SIZE - partData.length);
                        if (additionalData.length > 0) {
                            byte[] combinedData = new byte[partData.length + additionalData.length];
                            System.arraycopy(partData, 0, combinedData, 0, partData.length);
                            System.arraycopy(additionalData, 0, combinedData, partData.length, additionalData.length);
                            partData = combinedData;
                        }
                    }

                    UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                            .bucket(bucketName)
                            .key(fileNameWithFolder)
                            .uploadId(uploadId)
                            .partNumber(partNumber)
                            .contentLength((long) partData.length)
                            .build();

                    UploadPartResponse uploadPartResponse = s3Client.uploadPart(uploadPartRequest,
                            RequestBody.fromBytes(partData));

                    completedParts.add(CompletedPart.builder()
                            .partNumber(partNumber)
                            .eTag(uploadPartResponse.eTag())
                            .build());

                    totalBytesRead += partData.length;
                    partNumber++;

                    if (partNumber % CommonConstants.NUMBER_TWENTY == 0) {
                        System.out.printf("Uploaded %d parts, %d bytes%n", partNumber - 1, totalBytesRead);
                    }
                }

                // Complete upload
                CompleteMultipartUploadRequest completeRequest = CompleteMultipartUploadRequest.builder()
                        .bucket(bucketName)
                        .key(fileNameWithFolder)
                        .uploadId(uploadId)
                        .multipartUpload(CompletedMultipartUpload.builder().parts(completedParts).build())
                        .build();

                s3Client.completeMultipartUpload(completeRequest);

                System.out.printf("Adaptive upload completed: %s (%d parts, %d bytes)%n",
                        fileNameWithFolder, completedParts.size(), totalBytesRead);

                return fileName;
            }

        } catch (S3Exception e) {
            log.error("S3 error during adaptive streaming upload: {}", e.getMessage());
            if (uploadId != null) {
                abortMultipartUpload(bucketName, fileNameWithFolder, uploadId);
            }
            throw new IOException("Failed adaptive upload: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error during adaptive streaming upload: {}", e.getMessage());
            if (uploadId != null) {
                abortMultipartUpload(bucketName, fileNameWithFolder, uploadId);
            }
            throw new IOException("Failed adaptive upload: " + e.getMessage(), e);
        }
    }

    /**
     * Reads part data from input stream efficiently with proper buffer management.
     *
     * @param inputStream the input stream to read from
     * @param maxSize     the maximum number of bytes to read
     * @return byte array containing the read data
     * @throws IOException if reading fails
     */
    private byte[] readPartData(final InputStream inputStream, final long maxSize) throws IOException {
        ByteArrayOutputStream partData = new ByteArrayOutputStream();
        byte[] buffer = new byte[STREAMING_CHUNK_SIZE];
        long bytesRead = 0;

        while (bytesRead < maxSize) {
            int readSize = inputStream.read(buffer, 0,
                    (int) Math.min(buffer.length, maxSize - bytesRead));

            if (readSize == -1) {
                break; // End of stream
            }
            partData.write(buffer, 0, readSize);
            bytesRead += readSize;
        }

        return partData.toByteArray();
    }

    /**
     * Calculates optimal part size based on file size and network conditions.
     *
     * <p>This method uses intelligent sizing to balance upload performance with memory usage:
     * <ul>
     *   <li>Files < 100MB: 8MB parts</li>
     *   <li>Files < 1GB: 16MB parts</li>
     *   <li>Files < 10GB: 32MB parts</li>
     *   <li>Files >= 10GB: Calculated to stay under 10,000 parts limit</li>
     * </ul>
     *
     * @param fileSize the size of the file in bytes
     * @return the optimal part size in bytes
     */
    private long calculateOptimalPartSize(final long fileSize) {
        if (fileSize <= S3Constants.SMALL_FILE_THRESHOLD) { // < 100MB
            return S3Constants.PART_SIZE_8MB; // 8MB parts
        } else if (fileSize <= S3Constants.MEDIUM_FILE_THRESHOLD) { // < 1GB
            return S3Constants.PART_SIZE_16MB; // 16MB parts
        } else if (fileSize <= S3Constants.LARGE_FILE_THRESHOLD) { // < 10GB
            return S3Constants.PART_SIZE_32MB; // 32MB parts
        } else {
            // For very large files, calculate to stay under 10,000 parts limit
            long partSize = Math.max(fileSize / S3Constants.MAX_PARTS_LIMIT, MIN_PART_SIZE);
            return Math.min(partSize, MAX_PART_SIZE);
        }
    }

    /**
     * Generates a unique filename by combining UUID with original filename.
     *
     * @param folder           the folder prefix
     * @param originalFilename the original filename from the uploaded file
     * @return a unique filename string
     */
    private String generateFileName(final String folder, final String originalFilename) {
        return UUID.randomUUID().toString() + "_" + originalFilename;
    }

    /**
     * Aborts a multipart upload in case of failure, cleaning up partial uploads.
     *
     * @param bucket   the S3 bucket name
     * @param key      the object key
     * @param uploadId the multipart upload ID to abort
     */
    private void abortMultipartUpload(final String bucket, final String key, final String uploadId) {
        try {
            AbortMultipartUploadRequest abortRequest = AbortMultipartUploadRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .uploadId(uploadId)
                    .build();
            s3Client.abortMultipartUpload(abortRequest);
            log.info("Successfully aborted multipart upload for key: {}, uploadId: {}", key, uploadId);
        } catch (Exception e) {
            log.error("Failed to abort multipart upload for key: {}, uploadId: {} - Error: {}", key, uploadId, e.getMessage());
        }
    }

    /**
     * Checks if a file exists in the S3 bucket.
     *
     * @param fileName the key/name of the file to check
     * @return true if file exists, false otherwise
     */
    public boolean fileExists(final String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            log.warn("File name is null or empty");
            return false;
        }

        try {
            HeadObjectRequest headRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName.trim())
                    .build();
            s3Client.headObject(headRequest);
            log.debug("File exists in S3: {}", fileName);
            return true;
        } catch (NoSuchKeyException e) {
            log.debug("File does not exist in S3: {}", fileName);
            return false;
        } catch (S3Exception e) {
            log.error("S3 error while checking file existence: {} - Error: {}", fileName, e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Unexpected error while checking file existence: {} - Error: {}", fileName, e.getMessage());
            return false;
        }
    }

    /**
     * Deletes a file from the S3 bucket with comprehensive error handling.
     *
     * @param fileName the key/name of the file to delete
     * @return true if file was deleted successfully, false if file didn't exist
     * @throws RuntimeException         if deletion fails due to S3 errors
     * @throws IllegalArgumentException if fileName is null or empty
     */
    public boolean deleteFile(final String fileName) {
        // Input validation
        if (fileName == null || fileName.trim().isEmpty()) {
            log.warn("Cannot delete file: filename is null or empty");
            throw new IllegalArgumentException("File name cannot be null or empty");
        }

        String trimmedFileName = fileName.trim();

        // Check if file exists before attempting deletion
        if (!fileExists(trimmedFileName)) {
            log.info("File does not exist in S3, skipping deletion: {}", trimmedFileName);
            return false;
        }

        try {
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(trimmedFileName)
                    .build();

            DeleteObjectResponse response = s3Client.deleteObject(deleteRequest);

            // S3 deleteObject always returns success even if file doesn't exist
            // So we rely on our pre-check for accurate reporting
            log.info("Successfully deleted file from S3: {} (RequestId: {})",
                    trimmedFileName, response.responseMetadata().requestId());
            return true;

        } catch (S3Exception e) {
            String errorMessage = String.format("S3 error while deleting file: %s - ErrorCode: %s, ErrorMessage: %s",
                    trimmedFileName, e.awsErrorDetails().errorCode(), e.getMessage());
            log.error(errorMessage);
            throw new RuntimeException(errorMessage, e);
        } catch (Exception e) {
            String errorMessage = String.format("Unexpected error while deleting file: %s - Error: %s",
                    trimmedFileName, e.getMessage());
            log.error(errorMessage);
            throw new RuntimeException(errorMessage, e);
        }
    }

    /**
     * Retrieves metadata for a file stored in S3.
     *
     * @param fileName the key/name of the file
     * @return HeadObjectResponse containing file metadata
     * @throws RuntimeException if file is not found or metadata retrieval fails
     */
    public HeadObjectResponse getFileMetadata(final String fileName) {
        try {
            HeadObjectRequest headRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();
            return s3Client.headObject(headRequest);
        } catch (Exception e) {
            throw new RuntimeException("File not found: " + fileName, e);
        }
    }

    /**
     * Lists files in a specific folder with pagination support.
     *
     * @param folderPrefix the folder prefix to search within
     * @param maxKeys      the maximum number of files to return
     * @return list of file keys (names) found in the folder
     */
    public List<String> listFiles(final String folderPrefix, final int maxKeys) {
        List<String> fileNames = new ArrayList<>();

        ListObjectsV2Request.Builder requestBuilder = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(folderPrefix)
                .maxKeys(maxKeys);

        ListObjectsV2Request request = requestBuilder.build();
        ListObjectsV2Response result;

        do {
            result = s3Client.listObjectsV2(request);
            for (S3Object s3Object : result.contents()) {
                fileNames.add(s3Object.key());
            }

            if (result.isTruncated() && fileNames.size() < maxKeys) {
                request = request.toBuilder()
                        .continuationToken(result.nextContinuationToken())
                        .build();
            }
        } while (result.isTruncated() && fileNames.size() < maxKeys);

        return fileNames;
    }

    /**
     * Functional interface for tracking upload progress during file uploads.
     *
     * <p>Implementations can use this callback to provide real-time progress
     * updates to users or monitoring systems.
     */
    @FunctionalInterface
    public interface UploadProgressCallback {
        /**
         * Called when upload progress changes.
         *
         * @param percentage    the upload progress as a percentage (0-100)
         * @param uploadedBytes the number of bytes uploaded so far
         * @param totalBytes    the total number of bytes to upload
         */
        void onProgress(int percentage, long uploadedBytes, long totalBytes);
    }
}
