package com.nt.course_service_lms.dto.inDTO;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

/**
 * Data Transfer Object for quiz submission input containing user responses.
 * <p>
 * This DTO is used to capture the responses submitted by a user when completing a quiz.
 * It may also include optional metadata like notes and the total time spent on the quiz.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizSubmissionInDTO {

    /**
     * A list of user responses for the quiz questions.
     * Each response must be valid according to {@link UserResponseInDTO} validation constraints.
     */
    @Valid
    private List<UserResponseInDTO> userResponses;

    /**
     * Optional notes submitted along with the quiz.
     */
    private String notes;

    /**
     * Total time spent on the quiz, in seconds.
     */
    private Long timeSpent;

    /**
     * Checks equality between this object and another.
     *
     * @param o the object to compare with
     * @return true if both objects are equal based on their fields; false otherwise
     */
    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        QuizSubmissionInDTO that = (QuizSubmissionInDTO) o;
        return Objects.equals(userResponses, that.userResponses)
                && Objects.equals(notes, that.notes)
                && Objects.equals(timeSpent, that.timeSpent);
    }

    /**
     * Generates a hash code based on the object's fields.
     *
     * @return hash code of the object
     */
    @Override
    public int hashCode() {
        return Objects.hash(userResponses, notes, timeSpent);
    }
}
