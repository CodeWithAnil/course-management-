package com.nt.course_service_lms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing the course content in the Learning Management System (LMS).
 *
 * <p>This entity maps to the {@code course_content} table and stores
 * individual content sections for a course such as titles, descriptions,
 * video links, and optional resources.</p>
 */
@Entity
@Table(name = "course_content")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseContent {

    /**
     * Unique identifier for each course content section.
     * <p>This is the primary key and is auto-generated.</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_content_id")
    private long courseContentId;

    /**
     * The ID of the course to which this content belongs.
     */
    @Column(name = "course_id")
    private long courseId;

    /**
     * The title of the course content section.
     */
    @Column(name = "title")
    private String title;

    /**
     * A detailed description of the content section.
     */
    @Column(name = "description")
    private String description;

    /**
     * A type of the content section.
     */
    @Column(name = "content_type")
    private String contentType;

    /**
     * An optional URL link to an external resource (e.g., reading material).
     */
    @Column(name = "resource_link")
    private String resourceLink;

    /**
     * Minimum completion percentage required before user can acknowledge completion.
     * Admin can set this value (e.g., 80.0 means user must complete 80% before acknowledging).
     */
    @Column(name = "min_completion_percentage")
    private float minCompletionPercentage;

    /**
     * Flag indicating whether the content is currently active and visible to learners.
     */
    @Column(name = "is_active")
    private boolean isActive;

    /**
     * Timestamp when the content record was first created.
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * Timestamp for the last update made to the content record.
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Checks if this CourseContent is equal to another object.
     *
     * @param o the object to compare with this CourseContent instance.
     * @return true if the other object is a CourseContent with the same ID,
     * course ID, title, description, resource link, active status, and timestamps; false otherwise.
     */
    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CourseContent that = (CourseContent) o;
        return courseContentId == that.courseContentId
                && courseId == that.courseId
                && isActive == that.isActive
                && Objects.equals(title, that.title)
                && Objects.equals(description, that.description)
                && Objects.equals(resourceLink, that.resourceLink)
                && Objects.equals(minCompletionPercentage, that.minCompletionPercentage)
                && Objects.equals(createdAt, that.createdAt)
                && Objects.equals(updatedAt, that.updatedAt);
    }

    /**
     * Generates a hash code for this CourseContent instance.
     *
     * @return a hash code based on the course content ID, course ID, title, description,
     * resource link, active status, created at, and updated at timestamps.
     */
    @Override
    public int hashCode() {
        return Objects.hash(
                courseContentId,
                courseId,
                title,
                description,
                resourceLink,
                minCompletionPercentage,
                isActive,
                createdAt,
                updatedAt
        );
    }
}
