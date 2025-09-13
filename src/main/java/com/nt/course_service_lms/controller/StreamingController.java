package com.nt.course_service_lms.controller;

import com.nt.course_service_lms.dto.inDTO.FileMetadata;
import com.nt.course_service_lms.exception.FileStreamingException;
import com.nt.course_service_lms.service.S3StreamingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controller for handling streaming of video, PDF, and other files from S3.
 * Supports HTTP range requests and metadata retrieval.
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/service-api/streaming")
public class StreamingController {

    /**
     * Service for handling streaming and file retrieval logic from S3.
     */
    @Autowired
    private S3StreamingService s3StreamingService;

    /**
     * Stream video/audio content with range support for ReactPlayer.
     * Supports HTTP Range requests for efficient streaming.
     *
     * @param fileName    The name of the video file to stream.
     * @param rangeHeader Optional HTTP Range header to enable byte-range streaming.
     * @return ResponseEntity with partial or full content stream.
     */
    @GetMapping("/video/{fileName:.+}")
    public ResponseEntity<?> streamVideo(
            @PathVariable final String fileName,
            @RequestHeader(value = "Range", required = false) final String rangeHeader) {
        try {
            String objectKey = "video/" + fileName;
            return s3StreamingService.streamFileChunk(objectKey, rangeHeader);
        } catch (FileStreamingException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("error", "Unexpected streaming error"));
        }
    }

    /**
     * Stream PDF content with optional range support.
     * If Range header is present, returns partial content.
     * Otherwise, returns full PDF content.
     *
     * @param filename    The name of the PDF file to stream.
     * @param rangeHeader Optional HTTP Range header to enable byte-range streaming.
     * @return ResponseEntity with partial or full content stream.
     */
    @GetMapping("/pdf/{filename:.+}")
    public ResponseEntity<?> streamPdf(
            @PathVariable final String filename,
            @RequestHeader(value = "Range", required = false) final String rangeHeader) {

        try {
            String objectKey = "pdf/" + filename;
            return s3StreamingService.streamFileChunk(objectKey, rangeHeader);
        } catch (FileStreamingException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("error", "Unexpected streaming error"));
        }
    }

    /**
     * Retrieve metadata (e.g., file size) for a given file.
     * Supports both video and PDF file lookups.
     *
     * @param filename The name of the file to retrieve metadata for.
     * @return ResponseEntity containing file metadata or error status.
     */
    @GetMapping("/metadata/{filename:.+}")
    public ResponseEntity<FileMetadata> getFileMetadata(@PathVariable final String filename) {
        try {
            String objectKey;
            long fileSize = -1;

            if (filename.toLowerCase().endsWith(".pdf")) {
                objectKey = "pdf/" + filename;
                fileSize = s3StreamingService.getFileSize(objectKey);
                System.out.println("Checking PDF: " + objectKey + ", Size: " + fileSize);
            } else {
                objectKey = "video/" + filename;
                fileSize = s3StreamingService.getFileSize(objectKey);
                System.out.println("Checking Video: " + objectKey + ", Size: " + fileSize);
            }

            if (fileSize == -1) {
                String alternateObjectKey;
                if (filename.toLowerCase().endsWith(".pdf")) {
                    alternateObjectKey = "video/" + filename;
                } else {
                    alternateObjectKey = "pdf/" + filename;
                }
                fileSize = s3StreamingService.getFileSize(alternateObjectKey);
                System.out.println("Checking alternate location: " + alternateObjectKey + ", Size: " + fileSize);

                if (fileSize != -1) {
                    objectKey = alternateObjectKey;
                }
            }

            if (fileSize == -1) {
                System.out.println("File not found: " + filename);
                return ResponseEntity.notFound().build();
            }

            FileMetadata metadata = new FileMetadata();
            metadata.setSize(fileSize);
            metadata.setObjectKey(filename);

            System.out.println("File metadata success - Name: " + filename + ", Size: " + fileSize + " bytes");
            return ResponseEntity.ok(metadata);

        } catch (Exception e) {
            System.err.println("Metadata fetch failed: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Streams the full content of a file without using byte-range chunking.
     * Suitable for small files or full downloads.
     *
     * @param objectKey The S3 object key of the file to download.
     * @return ResponseEntity containing the file as an InputStreamResource.
     */
    @GetMapping("/download/{objectKey:.+}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable final String objectKey) {
        return s3StreamingService.streamFullFile(objectKey);
    }
}
