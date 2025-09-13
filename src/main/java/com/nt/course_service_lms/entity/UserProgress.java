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
 * Entity representing a user's progress on course content.
 * Maps to the user_progress table in the database.
 */
@Entity
@Table(name = "user_progress")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProgress {

    /**
     * Unique identifier for the user progress record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long progressId;

    /**
     * ID of the user associated with this progress record.
     */
    @Column(nullable = false)
    private Long userId;

    /**
     * ID of the content (e.g., PDF or video) being tracked.
     */
    @Column(nullable = false)
    private Long contentId;

    /**
     * ID of the course to which the content belongs.
     */
    @Column(nullable = false)
    private Long courseId; // Added course ID to match the database table

    /**
     * Type of content, such as 'pdf' or 'video'.
     */
    @Column(nullable = false)
    private String contentType; // 'pdf' or 'video'

    /**
     * Last viewed position: PDF page number or video timestamp.
     */
    @Column(nullable = false)
    private double lastPosition = 0.0; // PDF page number or video timestamp

    /**
     * Percentage of the individual content completed by the user.
     */
    @Column(nullable = false)
    private double contentCompletionPercentage = 0.0; // Individual content completion

    /**
     * Overall percentage of the course completed by the user.
     */
    @Column(nullable = false)
    private double courseCompletionPercentage = 0.0; // Overall course completion

    /**
     * Indicates whether the user has completed the course.
     * Once true, it should not revert to false.
     */
    @Column(nullable = false)
    private boolean courseCompleted = false; // Ensures course doesn’t downgrade

    /**
     * Flag indicating whether the content has been acknowledged by the user.
     * This will be set to true when user clicks the acknowledgement button
     * and their completion percentage meets the minimum threshold.
     */
    @Column(name = "acknowledgement")
    private boolean acknowledgement;


    /**
     * Timestamp of the last progress update.
     */
    @Column(nullable = false)
    private LocalDateTime lastUpdated = LocalDateTime.now();

    /**
     * Timestamp when the course was first completed by the user.
     */
    @Column(name = "first_completed_at")
    private LocalDateTime firstCompletedAt;

    /**
     * Checks equality based on all fields.
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserProgress that = (UserProgress) o;
        return Double.compare(lastPosition, that.lastPosition) == 0
                && Double.compare(contentCompletionPercentage, that.contentCompletionPercentage) == 0
                && Double.compare(courseCompletionPercentage, that.courseCompletionPercentage) == 0
                && courseCompleted == that.courseCompleted && Objects.equals(progressId, that.progressId)
                && Objects.equals(userId, that.userId)
                && Objects.equals(contentId, that.contentId)
                && Objects.equals(courseId, that.courseId)
                && Objects.equals(contentType, that.contentType)
                && Objects.equals(lastUpdated, that.lastUpdated)
                && Objects.equals(firstCompletedAt, that.firstCompletedAt);
    }

    /**
     * Generates hash code based on all fields.
     */
    @Override
    public int hashCode() {
        return Objects.hash(
                progressId,
                userId,
                contentId,
                courseId,
                contentType,
                lastPosition,
                contentCompletionPercentage,
                courseCompletionPercentage,
                courseCompleted,
                lastUpdated,
                firstCompletedAt);
    }
}
