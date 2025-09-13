package com.nt.course_service_lms.dto.inDTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * DTO for creating a new quiz attempt.
 * <p>
 * This DTO is used to initiate an attempt for a quiz by a specific user.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizAttemptCreateInDTO {

    /**
     * The ID of the quiz to be attempted.
     * <p>Must be a non-null and positive value.</p>
     */
    @NotNull(message = "Quiz ID is required")
    @Positive(message = "Quiz ID must be positive")
    private Long quizId;

    /**
     * The ID of the user attempting the quiz.
     * <p>Must be a non-null and positive value.</p>
     */
    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be positive")
    private Long userId;

    /**
     * Compares this object with the specified object for equality.
     * <p>
     * Returns {@code true} if the given object is of the same class and
     * has the same {@code quizId} and {@code userId}.
     * </p>
     *
     * @param o the object to compare with
     * @return {@code true} if the objects are equal, {@code false} otherwise
     */
    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        QuizAttemptCreateInDTO that = (QuizAttemptCreateInDTO) o;
        return Objects.equals(quizId, that.quizId) && Objects.equals(userId, that.userId);
    }

    /**
     * Computes the hash code for this object based on {@code quizId} and {@code userId}.
     *
     * @return the hash code value
     */
    @Override
    public int hashCode() {
        return Objects.hash(quizId, userId);
    }
}
