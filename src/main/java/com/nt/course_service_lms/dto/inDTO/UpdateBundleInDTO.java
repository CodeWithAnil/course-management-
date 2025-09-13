package com.nt.course_service_lms.dto.inDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

import static com.nt.course_service_lms.constants.BundleConstants.BUNDLE_NAME_INVALID;
import static com.nt.course_service_lms.constants.BundleConstants.BUNDLE_NAME_MIN_LENGTH;
import static com.nt.course_service_lms.constants.BundleConstants.BUNDLE_NAME_NOT_BLANK;
import static com.nt.course_service_lms.constants.BundleConstants.INT_VALUE_3;

/**
 * Data Transfer Object for updating a bundle.
 * <p>
 * Contains fields for updating the bundle name and active status.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateBundleInDTO {

    /**
     * Name of the bundle.
     * <p>
     * Must not be blank, must be at least 3 characters long, and must match the allowed pattern:
     * - Cannot start with a digit or whitespace
     * - Must start with an alphabet
     * - Can include alphabets, digits, and spaces
     * - Cannot end with whitespace
     */
    @NotBlank(message = BUNDLE_NAME_NOT_BLANK)
    @Size(min = INT_VALUE_3, message = BUNDLE_NAME_MIN_LENGTH)
    @Pattern(
            regexp = "^(?!\\d)(?!\\s)[A-Za-z][A-Za-z0-9 ]*(?<!\\s)$",
            message = BUNDLE_NAME_INVALID
    )
    private String bundleName;

    /**
     * Indicates whether the bundle is active.
     */
    @NotNull(message = "Is Active field is required")
    @JsonProperty("isActive")
    private boolean isActive;

    /**
     * Compares this object to another for equality.
     *
     * @param o the object to compare
     * @return true if both objects have the same values; false otherwise
     */
    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UpdateBundleInDTO that = (UpdateBundleInDTO) o;
        return isActive == that.isActive && Objects.equals(bundleName, that.bundleName);
    }

    /**
     * Generates the hash code for this DTO.
     *
     * @return hash code based on bundleName and isActive
     */
    @Override
    public int hashCode() {
        return Objects.hash(bundleName, isActive);
    }
}
