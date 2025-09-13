package com.nt.course_service_lms.controller;

import com.nt.course_service_lms.dto.inDTO.CourseContentInDTO;
import com.nt.course_service_lms.dto.inDTO.CourseContentUrlInDTO;
import com.nt.course_service_lms.dto.inDTO.UpdateCourseContentInDTO;
import com.nt.course_service_lms.dto.outDTO.CourseContentOutDTO;
import com.nt.course_service_lms.dto.outDTO.StandardResponseOutDTO;
import com.nt.course_service_lms.service.CourseContentService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Controller for managing Course Content in the Course Service of the LMS.
 * Provides endpoints for CRUD operations on course content with proper error handling
 * and standardized response format.
 */
@Slf4j
@RestController
@RequestMapping("/api/service-api/course-content")
public class CourseContentController {

    /**
     * Service layer component responsible for handling course content-related business logic.
     * This service encapsulates all business rules and data access logic for course content management.
     */
    private final CourseContentService courseContentService;

    /**
     * Constructor-based dependency injection for better testability.
     *
     * @param courseContentService service for handling course-content related business logic
     */
    @Autowired
    public CourseContentController(final CourseContentService courseContentService) {
        this.courseContentService = courseContentService;
    }

    /**
     * Creates a new CourseContent for a given course.
     *
     * @param courseContentInDTO DTO containing course content details
     * @return ResponseEntity containing the created CourseContent DTO
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseOutDTO<CourseContentOutDTO>> createCourseContent(
            @ModelAttribute @Valid final CourseContentInDTO courseContentInDTO) {

        log.info("Received request to create course content: {} for course ID: {}",
                courseContentInDTO.getTitle(), courseContentInDTO.getCourseId());

        CourseContentOutDTO courseContentOutDTO = courseContentService.createCourseContent(courseContentInDTO);
        StandardResponseOutDTO<CourseContentOutDTO> response = StandardResponseOutDTO
                .success(courseContentOutDTO, "Course Content Created Successfully");

        log.info("Course content created with ID: {}", courseContentOutDTO.getCourseContentId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Creates a new CourseContent for a given course.
     *
     * @param courseContentUrlInDTO DTO containing course content details
     * @return ResponseEntity containing the created CourseContent DTO
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseOutDTO<CourseContentOutDTO>> createCourseContent(
            @Valid @RequestBody final CourseContentUrlInDTO courseContentUrlInDTO) {

        log.info("Received request to create course content: {} for course ID: {}",
                courseContentUrlInDTO.getTitle(), courseContentUrlInDTO.getCourseId());

        CourseContentOutDTO courseContentOutDTO = courseContentService.createCourseContent(courseContentUrlInDTO);
        StandardResponseOutDTO<CourseContentOutDTO> response = StandardResponseOutDTO
                .success(courseContentOutDTO, "Course Content Created Successfully");

        log.info("Course content created with ID: {}", courseContentOutDTO.getCourseContentId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieves all course content entries.
     *
     * @return ResponseEntity containing a list of CourseContent DTOs
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<StandardResponseOutDTO<List<CourseContentOutDTO>>> getAllCourseContents() {
        log.info("Received request to fetch all course contents");

        List<CourseContentOutDTO> courseContents = courseContentService.getAllCourseContents();
        StandardResponseOutDTO<List<CourseContentOutDTO>> response = StandardResponseOutDTO
                .success(courseContents, "Course Contents Retrieved Successfully");

        log.info("Total course contents fetched: {}", courseContents.size());
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves course content by its unique ID.
     *
     * @param id ID of the course content to retrieve
     * @return ResponseEntity containing the CourseContent DTO
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<StandardResponseOutDTO<CourseContentOutDTO>> getCourseContentById(
            @PathVariable final Long id) {

        log.info("Received request to get course content by ID: {}", id);

        CourseContentOutDTO courseContent = courseContentService.getCourseContentById(id);
        StandardResponseOutDTO<CourseContentOutDTO> response = StandardResponseOutDTO
                .success(courseContent, "Course Content Retrieved Successfully");

        log.info("Course content retrieved with ID: {}", id);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves all course content entries associated with a specific course.
     *
     * @param courseId ID of the course whose contents are to be fetched
     * @return ResponseEntity containing a list of CourseContent DTOs
     */
    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<StandardResponseOutDTO<List<CourseContentOutDTO>>> getCourseContentByCourseId(
            @PathVariable final Long courseId) {

        log.info("Received request to fetch all course contents for course ID: {}", courseId);

        List<CourseContentOutDTO> courseContents = courseContentService.getAllCourseContentByCourseId(courseId);
        StandardResponseOutDTO<List<CourseContentOutDTO>> response = StandardResponseOutDTO
                .success(courseContents, "Course Contents Retrieved Successfully");

        log.info("Total course contents fetched for course ID {}: {}", courseId, courseContents.size());
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes a course content entry by its ID.
     *
     * @param id ID of the course content to delete
     * @return ResponseEntity containing a confirmation message
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseOutDTO<Void>> deleteCourseContent(@PathVariable final Long id) {
        log.info("Received request to delete course content with ID: {}", id);

        String message = courseContentService.deleteCourseContent(id);
        StandardResponseOutDTO<Void> response = StandardResponseOutDTO.success(null, message);

        log.info("Course content deleted with ID: {}", id);
        return ResponseEntity.ok(response);
    }

    /**
     * Updates a course content entry.
     *
     * @param id                       ID of the course content to update
     * @param updateCourseContentInDTO DTO containing updated course content information
     * @return ResponseEntity containing the updated CourseContent DTO
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseOutDTO<CourseContentOutDTO>> updateCourseContent(
            @PathVariable final Long id,
            @Valid @RequestBody final UpdateCourseContentInDTO updateCourseContentInDTO) {

        log.info("Received request to update course content with ID: {}", id);

        CourseContentOutDTO updatedCourseContent = courseContentService.updateCourseContent(id, updateCourseContentInDTO);
        StandardResponseOutDTO<CourseContentOutDTO> response = StandardResponseOutDTO
                .success(updatedCourseContent, "Course Content Updated Successfully");

        log.info("Course content updated with ID: {}", id);
        return ResponseEntity.ok(response);
    }

    /**
     * Health check endpoint for the course content service.
     *
     * @return ResponseEntity indicating service health
     */
    @GetMapping("/health")
    public ResponseEntity<StandardResponseOutDTO<String>> healthCheck() {
        log.debug("Health check requested for course content service");

        StandardResponseOutDTO<String> response = StandardResponseOutDTO
                .success("UP", "Course Content Service is running");

        return ResponseEntity.ok(response);
    }

    /**
     * Get course content count for a specific course.
     *
     * @param courseId ID of the course
     * @return ResponseEntity containing the count of course contents
     */
    @GetMapping("/course/{courseId}/count")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<StandardResponseOutDTO<Integer>> getCourseContentCount(
            @PathVariable final Long courseId) {

        log.info("Received request to get course content count for course ID: {}", courseId);

        List<CourseContentOutDTO> courseContents = courseContentService.getAllCourseContentByCourseId(courseId);
        int count = courseContents.size();

        StandardResponseOutDTO<Integer> response = StandardResponseOutDTO
                .success(count, "Course Content Count Retrieved Successfully");

        log.info("Course content count for course ID {}: {}", courseId, count);
        return ResponseEntity.ok(response);
    }
}
