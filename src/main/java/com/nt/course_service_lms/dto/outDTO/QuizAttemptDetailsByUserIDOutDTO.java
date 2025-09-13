package com.nt.course_service_lms.dto.outDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data Transfer Object representing quiz attempt details for a specific user across a course.
 * <p>
 * This DTO contains the course information and a list of the user's quiz attempts within that course.
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizAttemptDetailsByUserIDOutDTO {

    /**
     * The course information associated with the quiz attempts.
     */
    private CourseOutDTO courseOutDTO;

    /**
     * List of quiz attempt details made by the user.
     */
    private List<UserQuizAttemptDetailsOutDTO> userQuizAttemptDetailsOutDTOS;
}
