package com.nt.course_service_lms.dto.outDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object representing course content information.
 * <p>
 * This DTO is used to expose the details of individual course content blocks,
 * such as lessons, videos, readings, or external resources associated with a course.
 * It includes metadata like title, description, status, and timestamps.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseContentOutDTO {

    /**
     * Unique identifier of the course content.
     */
    private long courseContentId;

    /**
     * The ID of the course to which this content belongs.
     */
    private long courseId;

    /**
     * The title or name of the course content.
     */
    private String title;

    /**
     * A brief description explaining the content or objective of this resource.
     */
    private String description;

    /**
     * A content type indicating the nature of this resource (e.g., video, article, quiz).
     */
    private String contentType;

    /**
     * A link to the content resource.
     * <p>
     * Can be a URL to a video, document, or any other learning material.
     * </p>
     */
    private String resourceLink;

    /**
     * Indicates whether the content is currently active (visible and accessible).
     */
    private boolean isActive;

    /**
     * Timestamp indicating when this content was created.
     */
    private LocalDateTime createdAt;

    /**
     * Timestamp indicating the last time this content was updated.
     */
    private LocalDateTime updatedAt;

    /**
     * Partial constructor used when creating or mapping course content
     * without needing timestamps or content ID.
     *
     * @param courseId     the ID of the associated course
     * @param title        the title of the course content
     * @param description  a description of the content
     * @param contentType  the type of content (e.g., VIDEO, TEXT, QUIZ)
     * @param resourceLink the link to the content resource
     * @param active       flag indicating whether the content is active
     */
    public CourseContentOutDTO(final long courseId, final String title, final String description, final String contentType,
                               final String resourceLink, final boolean active) {
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.contentType = contentType;
        this.resourceLink = resourceLink;
        this.isActive = active;
    }
}
