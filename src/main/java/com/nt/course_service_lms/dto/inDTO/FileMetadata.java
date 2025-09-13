package com.nt.course_service_lms.dto.inDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.nt.course_service_lms.constants.S3Constants.DEFAULT_CHUNK_SIZE;

/**
 * DTO for representing metadata information about a file stored in the system.
 * Used primarily for streaming or downloading content like videos or PDFs.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileMetadata {

    /**
     * The unique key or path identifying the file in the storage system (e.g., S3).
     */
    @JsonProperty("objectKey")
    private String objectKey;

    /**
     * The total size of the file in bytes.
     */
    @JsonProperty("size")
    private long size;

    /**
     * The size of each chunk to be used when streaming the file.
     * <p>Default is 5MB (5 * 1024 * 1024 bytes).</p>
     */
    @JsonProperty("chunkSize")
    private long chunkSize = DEFAULT_CHUNK_SIZE;
}
