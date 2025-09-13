package com.nt.course_service_lms.dto.inDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

import static com.nt.course_service_lms.constants.BundleConstants.INT_VALUE_3;
import static com.nt.course_service_lms.constants.CourseConstants.COURSE_LEVEL_REQUIRED;
import static com.nt.course_service_lms.constants.CourseConstants.DESCRIPTION_BLANK;
import static com.nt.course_service_lms.constants.CourseConstants.DESCRIPTION_MIN_LENGTH;
import static com.nt.course_service_lms.constants.CourseConstants.OWNER_ID_BLANK;
import static com.nt.course_service_lms.constants.CourseConstants.OWNER_ID_INVALID;
import static com.nt.course_service_lms.constants.CourseConstants.TITLE_BLANK;
import static com.nt.course_service_lms.constants.CourseConstants.TITLE_MIN_LENGTH;

/**
 * Data Transfer Object for updating course details.
 * <p>
 * This DTO is used when modifying course metadata such as title,
 * description, level, ownership, and active status.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCourseInDTO {

    /**
     * The title of the course.
     * <p>Must be non-blank and at least 3 characters long.</p>
     */
    @NotBlank(message = TITLE_BLANK)
    @Size(min = INT_VALUE_3, message = TITLE_MIN_LENGTH)
    private String title;

    /**
     * The ID of the user who owns or created the course.
     * <p>Must be non-null and greater than or equal to 0.</p>
     */
    @NotNull(message = OWNER_ID_BLANK)
    @Min(value = 0, message = OWNER_ID_INVALID)
    private Long ownerId;

    /**
     * A brief description of the course.
     * <p>Must be non-blank and at least 3 characters long.</p>
     */
    @NotBlank(message = DESCRIPTION_BLANK)
    @Size(min = INT_VALUE_3, message = DESCRIPTION_MIN_LENGTH)
    private String description;

    /**
     * The difficulty level of the course.
     * <p>Examples: BEGINNER, INTERMEDIATE, ADVANCED. Must be non-null.</p>
     */
    @NotNull(message = COURSE_LEVEL_REQUIRED)
    private String courseLevel;

    /**
     * Flag indicating whether the course is active.
     * <p>Used to enable or disable course visibility.</p>
     */
    @NotNull(message = "Is Active field is required")
    @JsonProperty("isActive")
    private boolean isActive;

    /**
     * Compares this object to another for equality.
     *
     * @param o the object to compare
     * @return true if all fields are equal; false otherwise
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UpdateCourseInDTO that = (UpdateCourseInDTO) o;
        return isActive == that.isActive
                && Objects.equals(title, that.title)
                && Objects.equals(ownerId, that.ownerId)
                && Objects.equals(description, that.description)
                && Objects.equals(courseLevel, that.courseLevel);
    }

    /**
     * Computes a hash code based on all fields of the object.
     *
     * @return a hash code integer
     */
    @Override
    public int hashCode() {
        return Objects.hash(title, ownerId, description, courseLevel, isActive);
    }
}
