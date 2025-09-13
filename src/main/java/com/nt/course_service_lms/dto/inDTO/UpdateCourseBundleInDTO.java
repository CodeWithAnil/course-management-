package com.nt.course_service_lms.dto.inDTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

import static com.nt.course_service_lms.constants.CourseBundleConstants.BUNDLE_ID_NOT_NULL;
import static com.nt.course_service_lms.constants.CourseBundleConstants.BUNDLE_ID_POSITIVE;
import static com.nt.course_service_lms.constants.CourseBundleConstants.COURSE_ID_NOT_NULL;
import static com.nt.course_service_lms.constants.CourseBundleConstants.COURSE_ID_POSITIVE;

/**
 * Data Transfer Object for updating the association between a course and a bundle.
 * <p>
 * This DTO includes identifiers for the course and bundle, as well as an active status flag.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCourseBundleInDTO {

    /**
     * The ID of the bundle to which the course belongs.
     * <p>Must not be null and must be a positive number.</p>
     */
    @NotNull(message = BUNDLE_ID_NOT_NULL)
    @Positive(message = BUNDLE_ID_POSITIVE)
    private Long bundleId;

    /**
     * The ID of the course to be associated with the bundle.
     * <p>Must not be null and must be a positive number.</p>
     */
    @NotNull(message = COURSE_ID_NOT_NULL)
    @Positive(message = COURSE_ID_POSITIVE)
    private Long courseId;

    /**
     * Indicates whether the course is active in the bundle.
     * <p>This field is required.</p>
     */
    @NotNull(message = "Is Active field is required")
    private boolean isActive;

    /**
     * Checks equality between this object and another.
     *
     * @param o the object to compare with
     * @return true if the given object is equal to this instance; false otherwise
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UpdateCourseBundleInDTO that = (UpdateCourseBundleInDTO) o;
        return isActive == that.isActive
                && Objects.equals(bundleId, that.bundleId)
                && Objects.equals(courseId, that.courseId);
    }

    /**
     * Generates a hash code for this DTO based on its fields.
     *
     * @return hash code value
     */
    @Override
    public int hashCode() {
        return Objects.hash(bundleId, courseId, isActive);
    }
}
