package com.nt.course_service_lms.dto.inDTO;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

import static com.nt.course_service_lms.constants.CourseContentConstants.CONTENT_TYPE_NOT_BLANK;
import static com.nt.course_service_lms.constants.CourseContentConstants.COURSE_ID_NOT_NULL;
import static com.nt.course_service_lms.constants.CourseContentConstants.COURSE_ID_VALID;
import static com.nt.course_service_lms.constants.CourseContentConstants.DESCRIPTION_NOT_BLANK;
import static com.nt.course_service_lms.constants.CourseContentConstants.DESCRIPTION_SIZE_EXCEED;
import static com.nt.course_service_lms.constants.CourseContentConstants.DESCRIPTION_SIZE_EXCEED_VALUE;
import static com.nt.course_service_lms.constants.CourseContentConstants.TITLE_NOT_BLANK;
import static com.nt.course_service_lms.constants.CourseContentConstants.TITLE_SIZE_EXCEED;
import static com.nt.course_service_lms.constants.CourseContentConstants.TITLE_SIZE_EXCEED_VALUE;

/**
 * Data Transfer Object (DTO) for transferring course content data with a YouTube URL
 * between the controller and service layers in the LMS.
 *
 * <p>This DTO is typically used when adding or managing content such as video lectures
 * that are hosted externally (e.g., on YouTube), rather than uploaded directly as files.</p>
 *
 * <p><b>Validation Rules:</b></p>
 * <ul>
 *     <li>{@code courseId} - Must be non-null and ≥ 0</li>
 *     <li>{@code title} - Required, max 100 characters</li>
 *     <li>{@code description} - Required, max 1000 characters</li>
 *     <li>{@code contentType} - Required, not blank</li>
 *     <li>{@code youtubeUrl} - Required, not null</li>
 *     <li>{@code isActive} - Required (not null)</li>
 * </ul>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseContentUrlInDTO {

    /**
     * The ID of the course to which this content belongs.
     * <p>Must be non-null and greater than or equal to 0.</p>
     */
    @NotNull(message = COURSE_ID_NOT_NULL)
    @Min(value = 0, message = COURSE_ID_VALID)
    private long courseId;

    /**
     * The title of the course content.
     * <p>Required field with a maximum of 100 characters.</p>
     */
    @NotBlank(message = TITLE_NOT_BLANK)
    @Size(max = TITLE_SIZE_EXCEED_VALUE, message = TITLE_SIZE_EXCEED)
    private String title;

    /**
     * The description or summary of the content.
     * <p>Required field with a maximum of 1000 characters.</p>
     */
    @NotBlank(message = DESCRIPTION_NOT_BLANK)
    @Size(max = DESCRIPTION_SIZE_EXCEED_VALUE, message = DESCRIPTION_SIZE_EXCEED)
    private String description;

    /**
     * The type of the content (e.g., video, document).
     * <p>This is a required field and must not be blank.</p>
     */
    @NotBlank(message = CONTENT_TYPE_NOT_BLANK)
    private String contentType;

    /**
     * Minimum completion percentage required before user can acknowledge completion.
     * Admin can set this value (e.g., 80.0 means user must complete 80% before acknowledging).
     */
    @Column(name = "min_completion_percentage")
    private float minCompletionPercentage;

    /**
     * The URL of the YouTube video representing the content.
     * <p>This field is required and must not be null.</p>
     */
    @NotNull(message = "Video link should not be empty")
    private String youtubeUrl;

    /**
     * Indicates whether the content is currently active.
     * <p>This field is required and must not be null.</p>
     */
    @NotNull(message = "Is Active field is required")
    private Boolean isActive;

    /**
     * Compares this DTO to another object for equality.
     *
     * @param o the other object to compare
     * @return true if both objects are equal in all fields, false otherwise
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CourseContentUrlInDTO that = (CourseContentUrlInDTO) o;
        return courseId == that.courseId
                && isActive == that.isActive && Objects.equals(title, that.title)
                && Objects.equals(description, that.description) && Objects.equals(contentType, that.contentType)
                && Objects.equals(youtubeUrl, that.youtubeUrl)
                && Objects.equals(minCompletionPercentage, that.minCompletionPercentage);
    }

    /**
     * Generates a hash code for the object.
     *
     * @return hash code based on all relevant fields
     */
    @Override
    public int hashCode() {
        return Objects.hash(courseId, title, description, contentType, youtubeUrl, isActive, minCompletionPercentage);
    }
}
