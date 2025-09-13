package com.nt.course_service_lms.dto.inDTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

import static com.nt.course_service_lms.constants.CourseContentConstants.COURSE_ID_NOT_NULL;
import static com.nt.course_service_lms.constants.CourseContentConstants.COURSE_ID_VALID;
import static com.nt.course_service_lms.constants.CourseContentConstants.DESCRIPTION_NOT_BLANK;
import static com.nt.course_service_lms.constants.CourseContentConstants.DESCRIPTION_SIZE_EXCEED;
import static com.nt.course_service_lms.constants.CourseContentConstants.DESCRIPTION_SIZE_EXCEED_VALUE;
import static com.nt.course_service_lms.constants.CourseContentConstants.RESOURCE_LINK_INVALID;
import static com.nt.course_service_lms.constants.CourseContentConstants.TITLE_NOT_BLANK;
import static com.nt.course_service_lms.constants.CourseContentConstants.TITLE_SIZE_EXCEED;
import static com.nt.course_service_lms.constants.CourseContentConstants.TITLE_SIZE_EXCEED_VALUE;

/**
 * Data Transfer Object for updating an existing course content entry.
 * <p>
 * This DTO encapsulates the necessary data to modify the content within a course,
 * including its title, description, associated resource link, and active status.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCourseContentInDTO {

    /**
     * The ID of the course to which this content belongs.
     * <p>Must be a non-null and non-negative value.</p>
     */
    @NotNull(message = COURSE_ID_NOT_NULL)
    @Min(value = 0, message = COURSE_ID_VALID)
    private long courseId;

    /**
     * The title of the course content.
     * <p>Required and must not exceed 100 characters.</p>
     */
    @NotBlank(message = TITLE_NOT_BLANK)
    @Size(max = TITLE_SIZE_EXCEED_VALUE, message = TITLE_SIZE_EXCEED)
    private String title;

    /**
     * The description or summary of the content.
     * <p>Required and must not exceed 1000 characters.</p>
     */
    @NotBlank(message = DESCRIPTION_NOT_BLANK)
    @Size(max = DESCRIPTION_SIZE_EXCEED_VALUE, message = DESCRIPTION_SIZE_EXCEED)
    private String description;

    /**
     * The optional link to additional resources related to the content.
     * <p>If provided, must be a valid HTTP, HTTPS, or FTP URL. Empty string is allowed.</p>
     */
    @Pattern(
            regexp = "^$|^(https?|ftp)://.*$",
            message = RESOURCE_LINK_INVALID
    )
    private String resourceLink;

    /**
     * The minimum percentage of course completion required.
     * <p>This value is mandatory and must be specified to determine completion criteria.</p>
     */
    @NotNull(message = "Completion threshold is required")
    private float minCompletionPercentage;

    /**
     * Flag indicating whether the course content is active.
     * <p>Required field.</p>
     */
    @NotNull(message = "Is Active field is required")
    private boolean isActive;

    /**
     * Checks equality between this object and another object.
     *
     * @param o the object to compare with
     * @return true if all fields match, false otherwise
     */
    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UpdateCourseContentInDTO that = (UpdateCourseContentInDTO) o;
        return courseId == that.courseId
                && isActive == that.isActive
                && Objects.equals(title, that.title)
                && Objects.equals(description, that.description)
                && Objects.equals(resourceLink, that.resourceLink)
                && Objects.equals(minCompletionPercentage, that.minCompletionPercentage);
    }

    /**
     * Generates a hash code for this object based on its fields.
     *
     * @return hash code integer
     */
    @Override
    public int hashCode() {
        return Objects.hash(courseId, title, description, resourceLink, minCompletionPercentage, isActive);
    }
}
