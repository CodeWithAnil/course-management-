package com.nt.course_service_lms.dto.outDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data Transfer Object representing detailed quiz attempt information by a user for a specific course.
 * <p>
 * This DTO is typically used to return quiz attempt details of a user within the context of a course,
 * including personal identifiers and a list of individual quiz attempts.
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizAttemptDetailsByCourseIDOutDTO {

    /**
     * Unique identifier of the user who attempted the quiz.
     */
    private Long userId;

    /**
     * The full username of the user.
     */
    private String userName;

    /**
     * The first name of the user.
     */
    private String firstName;

    /**
     * The last name of the user.
     */
    private String lastName;

    /**
     * List of quiz attempt details made by the user.
     */
    private List<UserQuizAttemptDetailsOutDTO> userQuizAttemptDetailsOutDTOS;
}
