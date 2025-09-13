package com.nt.course_service_lms.dto.inDTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

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
 * Data Transfer Object (DTO) for transferring course content data between the
 * controller and service layers in the LMS.
 *
 * <p>This DTO is used when adding or retrieving course content (such as
 * video lectures, descriptions, and resource links) for a specific course.</p>
 *
 * <p><b>Validation Rules:</b></p>
 * <ul>
 *     <li>{@code courseId} - Must be non-null and ≥ 0</li>
 *     <li>{@code title} - Required, max 100 characters</li>
 *     <li>{@code description} - Required, max 1000 characters</li>
 *     <li>{@code contentType} - Must not be blank</li>
 *     <li>{@code file} - Must not be null</li>
 *     <li>{@code isActive} - Required</li>
 * </ul>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseContentInDTO {

    /**
     * The ID of the course to which this content belongs.
     * <p>Must be non-null and greater than or equal to 0.</p>
     */
    @NotNull(message = COURSE_ID_NOT_NULL)
    @Min(value = 0, message = COURSE_ID_VALID)
    private long courseId;

    /**
     * The title of the course content.
     * <p>This is a required field with a maximum of 100 characters.</p>
     */
    @NotBlank(message = TITLE_NOT_BLANK)
    @Size(max = TITLE_SIZE_EXCEED_VALUE, message = TITLE_SIZE_EXCEED)
    private String title;

    /**
     * The description or summary of the course content.
     * <p>This is a required field with a maximum of 1000 characters.</p>
     */
    @NotBlank(message = DESCRIPTION_NOT_BLANK)
    @Size(max = DESCRIPTION_SIZE_EXCEED_VALUE, message = DESCRIPTION_SIZE_EXCEED)
    private String description;

    /**
     * The type of content (e.g., video, PDF, etc.).
     * <p>This is a required field and must not be blank.</p>
     */
    @NotBlank(message = CONTENT_TYPE_NOT_BLANK)
    private String contentType;

    /**
     * The uploaded file associated with the content (e.g., video file, PDF).
     * <p>This is a required field and must not be null.</p>
     */
    @NotNull(message = "Resource file should not be empty")
    private MultipartFile file;

    /**
     * Flag indicating whether the content is active or not.
     * <p>This is a required field.</p>
     */
    @NotNull(message = "Is Active field is required")
    private Boolean isActive;

    /**
     * The minimum percentage of course completion required.
     * <p>This value is mandatory and must be specified to determine completion criteria.</p>
     */
    @NotNull(message = "Completion threshold is required")
    private float minCompletionPercentage;

    /**
     * Checks if this CourseContentInDTO is equal to another object.
     *
     * @param o the other object to compare
     * @return true if both objects have the same field values, false otherwise
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CourseContentInDTO that = (CourseContentInDTO) o;
        return courseId == that.courseId
                && isActive == that.isActive
                && Objects.equals(title, that.title)
                && Objects.equals(description, that.description)
                && Objects.equals(contentType, that.contentType)
                && Objects.equals(file, that.file)
                && Objects.equals(minCompletionPercentage, that.minCompletionPercentage);
    }

    /**
     * Generates a hash code for the current CourseContentInDTO.
     *
     * @return hash code based on courseId, title, description, contentType, file, and isActive
     */
    @Override
    public int hashCode() {
        return Objects.hash(courseId, title, description, contentType, file, isActive, minCompletionPercentage);
    }
}
