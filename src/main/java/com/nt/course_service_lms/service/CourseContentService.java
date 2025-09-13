package com.nt.course_service_lms.service;

import com.nt.course_service_lms.dto.inDTO.CourseContentInDTO;
import com.nt.course_service_lms.dto.inDTO.CourseContentUrlInDTO;
import com.nt.course_service_lms.dto.inDTO.UpdateCourseContentInDTO;
import com.nt.course_service_lms.dto.outDTO.CourseContentOutDTO;

import java.util.List;

/**
 * Service interface for managing course content operations.
 * Defines the contract for course content CRUD operations.
 */
public interface CourseContentService {

    /**
     * Creates a new course content.
     *
     * @param courseContentInDTO the course content data transfer object
     * @return the created course content as DTO
     */
    CourseContentOutDTO createCourseContent(CourseContentInDTO courseContentInDTO);

    /**
     * Creates a new course content from a URL-based DTO.
     *
     * @param courseContentUrlInDTO the course content data from URL
     * @return the created course content as DTO
     */
    CourseContentOutDTO createCourseContent(CourseContentUrlInDTO courseContentUrlInDTO);

    /**
     * Retrieves all course contents.
     *
     * @return list of all course contents as DTOs
     */
    List<CourseContentOutDTO> getAllCourseContents();

    /**
     * Retrieves a course content by its ID.
     *
     * @param courseContentId the course content ID
     * @return the course content DTO
     */
    CourseContentOutDTO getCourseContentById(Long courseContentId);

    /**
     * Deletes a course content.
     *
     * @param courseContentId the course content ID to delete
     * @return success message
     */
    String deleteCourseContent(Long courseContentId);

    /**
     * Updates an existing course content.
     *
     * @param courseContentId          the course content ID to update
     * @param updateCourseContentInDTO the updated course content data
     * @return the updated course content as DTO
     */
    CourseContentOutDTO updateCourseContent(Long courseContentId, UpdateCourseContentInDTO updateCourseContentInDTO);

    /**
     * Retrieves all course contents for a specific course.
     *
     * @param courseId the course ID
     * @return list of course contents for the specified course as DTOs
     */
    List<CourseContentOutDTO> getAllCourseContentByCourseId(Long courseId);
}
