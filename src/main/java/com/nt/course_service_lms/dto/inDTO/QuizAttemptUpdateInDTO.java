package com.nt.course_service_lms.dto.inDTO;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * DTO for updating an existing quiz attempt.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizAttemptUpdateInDTO {

    /**
     * The timestamp indicating when the quiz attempt was finished.
     */
    private LocalDateTime finishedAt;

    /**
     * Detailed information about the score achieved in the quiz attempt.
     */
    private String scoreDetails;

    /**
     * The status of the quiz attempt.
     * <p>
     * Must be one of:
     * <ul>
     *     <li>IN_PROGRESS</li>
     *     <li>COMPLETED</li>
     *     <li>ABANDONED</li>
     *     <li>TIMED_OUT</li>
     * </ul>
     */
    @Pattern(regexp = "IN_PROGRESS|COMPLETED|ABANDONED|TIMED_OUT",
            message = "Status must be one of: IN_PROGRESS, COMPLETED, ABANDONED, TIMED_OUT")
    private String status;

    /**
     * Checks equality between this object and another.
     *
     * @param o the object to compare with
     * @return true if the given object is equal to this instance; false otherwise
     */
    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        QuizAttemptUpdateInDTO that = (QuizAttemptUpdateInDTO) o;
        return Objects.equals(finishedAt, that.finishedAt) && Objects.equals(scoreDetails, that.scoreDetails)
                && Objects.equals(status, that.status);
    }

    /**
     * Generates a hash code for this DTO.
     *
     * @return hash code based on finishedAt, scoreDetails, and status
     */
    @Override
    public int hashCode() {
        return Objects.hash(finishedAt, scoreDetails, status);
    }
}
