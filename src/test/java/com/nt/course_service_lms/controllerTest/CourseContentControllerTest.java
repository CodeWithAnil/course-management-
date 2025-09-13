package com.nt.course_service_lms.controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.course_service_lms.config.JwtUtil;
import com.nt.course_service_lms.config.TestSecurityConfig;
import com.nt.course_service_lms.controller.CourseContentController;
import com.nt.course_service_lms.dto.inDTO.CourseContentInDTO;
import com.nt.course_service_lms.dto.inDTO.CourseContentUrlInDTO;
import com.nt.course_service_lms.dto.inDTO.UpdateCourseContentInDTO;
import com.nt.course_service_lms.dto.outDTO.CourseContentOutDTO;
import com.nt.course_service_lms.exception.ResourceNotFoundException;
import com.nt.course_service_lms.service.CourseContentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CourseContentController.class)
@ExtendWith(MockitoExtension.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class CourseContentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CourseContentService courseContentService;

    @MockitoBean
    private JwtUtil jwtUtil;

    private CourseContentInDTO courseContentInDTO;
    private CourseContentUrlInDTO courseContentUrlInDTO;
    private UpdateCourseContentInDTO updateCourseContentInDTO;
    private CourseContentOutDTO courseContentOutDTO;
    private MockMultipartFile mockFile;

    @BeforeEach
    void setUp() throws Exception {
        // Initialize test data
        mockFile = new MockMultipartFile("file", "test.pdf", "application/pdf", "test content".getBytes());

        courseContentInDTO = CourseContentInDTO.builder()
                .courseId(1L)
                .title("Test Course Content")
                .description("This is a test course content description")
                .contentType("PDF")
                .file(mockFile)
                .isActive(true)
                .build();

        courseContentUrlInDTO = CourseContentUrlInDTO.builder()
                .courseId(1L)
                .title("Test Video Content")
                .description("This is a test video content description")
                .contentType("VIDEO")
                .youtubeUrl("https://www.youtube.com/watch?v=example")
                .isActive(true)
                .build();

        updateCourseContentInDTO = UpdateCourseContentInDTO.builder()
                .courseId(2L)
                .title("Updated Course Content")
                .description("This is an updated course content description")
                .resourceLink("https://updated-example.com/resource")
                .isActive(false)
                .build();

        courseContentOutDTO = CourseContentOutDTO.builder()
                .courseContentId(1L)
                .courseId(1L)
                .title("Test Course Content")
                .description("This is a test course content description")
                .resourceLink("https://example.com/resource")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // CREATE COURSE CONTENT WITH FILE TESTS
    @Test
    void createCourseContentWithFile_ShouldReturnCreatedCourseContent_WhenValidInput() throws Exception {
        // Given
        when(courseContentService.createCourseContent(any(CourseContentInDTO.class))).thenReturn(courseContentOutDTO);

        // When & Then
        mockMvc.perform(multipart("/api/service-api/course-content")
                        .file(mockFile)
                        .param("courseId", "1")
                        .param("title", "Test Course Content")
                        .param("description", "This is a test course content description")
                        .param("contentType", "PDF")
                        .param("isActive", "true")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Course Content Created Successfully"))
                .andExpect(jsonPath("$.data.courseContentId").value(1L))
                .andExpect(jsonPath("$.data.courseId").value(1L))
                .andExpect(jsonPath("$.data.title").value("Test Course Content"))
                .andExpect(jsonPath("$.data.description").value("This is a test course content description"))
                .andExpect(jsonPath("$.data.active").value(true));
    }

    @Test
    void createCourseContentWithFile_ShouldReturnBadRequest_WhenNoFile() throws Exception {
        // When & Then
        mockMvc.perform(multipart("/api/service-api/course-content")
                        .param("courseId", "1")
                        .param("title", "Test Course Content")
                        .param("description", "This is a test course content description")
                        .param("contentType", "PDF")
                        .param("isActive", "true")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isBadRequest());
    }

    // GET ALL COURSE CONTENTS TESTS
    @Test
    void getAllCourseContents_ShouldReturnListOfCourseContents_WhenCourseContentsExist() throws Exception {
        // Given
        List<CourseContentOutDTO> courseContents = Arrays.asList(courseContentOutDTO);
        when(courseContentService.getAllCourseContents()).thenReturn(courseContents);

        // When & Then
        mockMvc.perform(get("/api/service-api/course-content")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Course Contents Retrieved Successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].courseContentId").value(1L))
                .andExpect(jsonPath("$.data[0].title").value("Test Course Content"));
    }

    @Test
    void getAllCourseContents_ShouldReturnListOfCourseContents_WhenEmployeeRole() throws Exception {
        // Given
        List<CourseContentOutDTO> courseContents = Arrays.asList(courseContentOutDTO);
        when(courseContentService.getAllCourseContents()).thenReturn(courseContents);

        // When & Then
        mockMvc.perform(get("/api/service-api/course-content")
                        .header("X-Test-Role", "EMPLOYEE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    void getAllCourseContents_ShouldReturnNotFound_WhenNoCourseContentsExist() throws Exception {
        // Given
        when(courseContentService.getAllCourseContents())
                .thenThrow(new ResourceNotFoundException("No course contents found"));

        // When & Then
        mockMvc.perform(get("/api/service-api/course-content")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isNotFound());
    }

    // GET COURSE CONTENT BY ID TESTS
    @Test
    void getCourseContentById_ShouldReturnCourseContent_WhenCourseContentExists() throws Exception {
        // Given
        when(courseContentService.getCourseContentById(1L)).thenReturn(courseContentOutDTO);

        // When & Then
        mockMvc.perform(get("/api/service-api/course-content/1")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Course Content Retrieved Successfully"))
                .andExpect(jsonPath("$.data.courseContentId").value(1L))
                .andExpect(jsonPath("$.data.title").value("Test Course Content"));
    }

    @Test
    void getCourseContentById_ShouldReturnCourseContent_WhenEmployeeRole() throws Exception {
        // Given
        when(courseContentService.getCourseContentById(1L)).thenReturn(courseContentOutDTO);

        // When & Then
        mockMvc.perform(get("/api/service-api/course-content/1")
                        .header("X-Test-Role", "EMPLOYEE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    void getCourseContentById_ShouldReturnNotFound_WhenCourseContentDoesNotExist() throws Exception {
        // Given
        when(courseContentService.getCourseContentById(999L))
                .thenThrow(new ResourceNotFoundException("Course content not found"));

        // When & Then
        mockMvc.perform(get("/api/service-api/course-content/999")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isNotFound());
    }

    // GET COURSE CONTENT BY COURSE ID TESTS
    @Test
    void getCourseContentByCourseId_ShouldReturnCourseContents_WhenCourseContentExists() throws Exception {
        // Given
        List<CourseContentOutDTO> courseContents = Arrays.asList(courseContentOutDTO);
        when(courseContentService.getAllCourseContentByCourseId(1L)).thenReturn(courseContents);

        // When & Then
        mockMvc.perform(get("/api/service-api/course-content/course/1")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Course Contents Retrieved Successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].courseId").value(1L));
    }

    @Test
    void getCourseContentByCourseId_ShouldReturnCourseContents_WhenEmployeeRole() throws Exception {
        // Given
        List<CourseContentOutDTO> courseContents = Arrays.asList(courseContentOutDTO);
        when(courseContentService.getAllCourseContentByCourseId(1L)).thenReturn(courseContents);

        // When & Then
        mockMvc.perform(get("/api/service-api/course-content/course/1")
                        .header("X-Test-Role", "EMPLOYEE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    void getCourseContentByCourseId_ShouldReturnNotFound_WhenCourseNotFound() throws Exception {
        // Given
        when(courseContentService.getAllCourseContentByCourseId(999L))
                .thenThrow(new ResourceNotFoundException("Course not found"));

        // When & Then
        mockMvc.perform(get("/api/service-api/course-content/course/999")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getCourseContentByCourseId_ShouldReturnNotFound_WhenNoCourseContentsFound() throws Exception {
        // Given
        when(courseContentService.getAllCourseContentByCourseId(1L))
                .thenThrow(new ResourceNotFoundException("No course contents found for course"));

        // When & Then
        mockMvc.perform(get("/api/service-api/course-content/course/1")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isNotFound());
    }

    // DELETE COURSE CONTENT TESTS
    @Test
    void deleteCourseContent_ShouldReturnSuccess_WhenCourseContentExists() throws Exception {
        // Given
        when(courseContentService.deleteCourseContent(1L)).thenReturn("Course content deleted successfully");

        // When & Then
        mockMvc.perform(delete("/api/service-api/course-content/1")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Course content deleted successfully"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void deleteCourseContent_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/service-api/course-content/1")
                        .header("X-Test-Role", "EMPLOYEE"))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteCourseContent_ShouldReturnNotFound_WhenCourseContentDoesNotExist() throws Exception {
        // Given
        when(courseContentService.deleteCourseContent(999L))
                .thenThrow(new ResourceNotFoundException("Course content not found"));

        // When & Then
        mockMvc.perform(delete("/api/service-api/course-content/999")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isNotFound());
    }

    // UPDATE COURSE CONTENT TESTS
    @Test
    void updateCourseContent_ShouldReturnUpdatedCourseContent_WhenValidInput() throws Exception {
        // Given
        CourseContentOutDTO updatedCourseContent = CourseContentOutDTO.builder()
                .courseContentId(1L)
                .courseId(2L)
                .title("Updated Course Content")
                .description("This is an updated course content description")
                .resourceLink("https://updated-example.com/resource")
                .isActive(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(courseContentService.updateCourseContent(anyLong(), any(UpdateCourseContentInDTO.class)))
                .thenReturn(updatedCourseContent);

        // When & Then
        mockMvc.perform(put("/api/service-api/course-content/1")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCourseContentInDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Course Content Updated Successfully"))
                .andExpect(jsonPath("$.data.courseContentId").value(1L))
                .andExpect(jsonPath("$.data.title").value("Updated Course Content"))
                .andExpect(jsonPath("$.data.active").value(false));
    }

    @Test
    void updateCourseContent_ShouldReturnBadRequest_WhenInvalidInput() throws Exception {
        // Given - Invalid update with blank title
        UpdateCourseContentInDTO invalidUpdate = UpdateCourseContentInDTO.builder()
                .courseId(1L)
                .title("")
                .description("Updated description")
                .resourceLink("https://example.com")
                .isActive(true)
                .build();

        // When & Then
        mockMvc.perform(put("/api/service-api/course-content/1")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUpdate)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateCourseContent_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/service-api/course-content/1")
                        .header("X-Test-Role", "EMPLOYEE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCourseContentInDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateCourseContent_ShouldReturnNotFound_WhenCourseContentDoesNotExist() throws Exception {
        // Given
        when(courseContentService.updateCourseContent(anyLong(), any(UpdateCourseContentInDTO.class)))
                .thenThrow(new ResourceNotFoundException("Course content not found"));

        // When & Then
        mockMvc.perform(put("/api/service-api/course-content/999")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCourseContentInDTO)))
                .andExpect(status().isNotFound());
    }

//    @Test
//    void updateCourseContent_ShouldReturnConflict_WhenDuplicateFound() throws Exception {
//        // Given
//        when(courseContentService.updateCourseContent(anyLong(), any(UpdateCourseContentInDTO.class)))
//                .thenThrow(new ResourceAlreadyExistsException("Course content with same title already exists"));
//
//        // When & Then
//        mockMvc.perform(put("/api/service-api/course-content/1")
//                        .header("X-Test-Role", "ADMIN")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updateCourseContentInDTO)))
//                .andExpect(status().isConflict());
//    }

    // GET COURSE CONTENT COUNT TESTS
    @Test
    void getCourseContentCount_ShouldReturnCount_WhenCourseContentsExist() throws Exception {
        // Given
        List<CourseContentOutDTO> courseContents = Arrays.asList(courseContentOutDTO, courseContentOutDTO);
        when(courseContentService.getAllCourseContentByCourseId(1L)).thenReturn(courseContents);

        // When & Then
        mockMvc.perform(get("/api/service-api/course-content/course/1/count")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Course Content Count Retrieved Successfully"))
                .andExpect(jsonPath("$.data").value(2));
    }

    @Test
    void getCourseContentCount_ShouldReturnCount_WhenEmployeeRole() throws Exception {
        // Given
        List<CourseContentOutDTO> courseContents = Collections.singletonList(courseContentOutDTO);
        when(courseContentService.getAllCourseContentByCourseId(1L)).thenReturn(courseContents);

        // When & Then
        mockMvc.perform(get("/api/service-api/course-content/course/1/count")
                        .header("X-Test-Role", "EMPLOYEE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data").value(1));
    }

    @Test
    void getCourseContentCount_ShouldReturnZero_WhenNoCourseContents() throws Exception {
        // Given
        when(courseContentService.getAllCourseContentByCourseId(1L))
                .thenThrow(new ResourceNotFoundException("No course contents found"));

        // When & Then
        mockMvc.perform(get("/api/service-api/course-content/course/1/count")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isNotFound());
    }

//    // EDGE CASES AND ERROR SCENARIOS FOR FILE UPLOAD
//    @Test
//    void createCourseContentWithFile_ShouldReturnBadRequest_WhenMissingContentType() throws Exception {
//        // When & Then
//        mockMvc.perform(multipart("/api/service-api/course-content")
//                        .file(mockFile)
//                        .param("courseId", "1")
//                        .param("title", "Test Course Content")
//                        .param("description", "This is a test course content description")
//                        .param("isActive", "true")
//                        .header("X-Test-Role", "ADMIN"))
//                .andExpect(status().isBadRequest());
//    }

    @Test
    void createCourseContentWithFile_ShouldReturnBadRequest_WhenDescriptionTooLong() throws Exception {
        // Given - Description exceeding max length
        String longDescription = String.join("", Collections.nCopies(1001, "a")); // Assuming max is 1000

        // When & Then
        mockMvc.perform(multipart("/api/service-api/course-content")
                        .file(mockFile)
                        .param("courseId", "1")
                        .param("title", "Test Title")
                        .param("description", longDescription)
                        .param("contentType", "PDF")
                        .param("isActive", "true")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createCourseContentWithFile_ShouldReturnBadRequest_WhenTitleTooLong() throws Exception {
        // Given - Title exceeding max length
        String longTitle = String.join("", Collections.nCopies(101, "a")); // Assuming max is 100

        // When & Then
        mockMvc.perform(multipart("/api/service-api/course-content")
                        .file(mockFile)
                        .param("courseId", "1")
                        .param("title", longTitle)
                        .param("description", "Test Description")
                        .param("contentType", "PDF")
                        .param("isActive", "true")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isBadRequest());
    }

//    @Test
//    void createCourseContentWithFile_ShouldReturnConflict_WhenResourceAlreadyExists() throws Exception {
//        // Given
//        when(courseContentService.createCourseContent(any(CourseContentInDTO.class)))
//                .thenThrow(new ResourceAlreadyExistsException("Course content already exists"));
//
//        // When & Then
//        mockMvc.perform(multipart("/api/service-api/course-content")
//                        .file(mockFile)
//                        .param("courseId", "1")
//                        .param("title", "Test Course Content")
//                        .param("description", "This is a test course content description")
//                        .param("contentType", "PDF")
//                        .param("isActive", "true")
//                        .header("X-Test-Role", "ADMIN"))
//                .andExpect(status().isConflict());
//    }

    @Test
    void createCourseContentWithFile_ShouldReturnInternalServerError_WhenUnexpectedError() throws Exception {
        // Given
        when(courseContentService.createCourseContent(any(CourseContentInDTO.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        // When & Then
        mockMvc.perform(multipart("/api/service-api/course-content")
                        .file(mockFile)
                        .param("courseId", "1")
                        .param("title", "Test Course Content")
                        .param("description", "This is a test course content description")
                        .param("contentType", "PDF")
                        .param("isActive", "true")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isInternalServerError());
    }

    // EDGE CASES AND ERROR SCENARIOS FOR URL CONTENT
    @Test
    void createCourseContentWithUrl_ShouldReturnInternalServerError_WhenUnexpectedError() throws Exception {
        // Given
        when(courseContentService.createCourseContent(any(CourseContentUrlInDTO.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        // When & Then
        mockMvc.perform(post("/api/service-api/course-content")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseContentUrlInDTO)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void createCourseContentWithUrl_ShouldReturnBadRequest_WhenBlankContentType() throws Exception {
        // Given - Blank content type
        CourseContentUrlInDTO invalidCourseContent = CourseContentUrlInDTO.builder()
                .courseId(1L)
                .title("Test Title")
                .description("Test Description")
                .contentType("")
                .youtubeUrl("https://www.youtube.com/watch?v=example")
                .isActive(true)
                .build();

        // When & Then
        mockMvc.perform(post("/api/service-api/course-content")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCourseContent)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createCourseContentWithUrl_ShouldReturnBadRequest_WhenBlankDescription() throws Exception {
        // Given - Blank description
        CourseContentUrlInDTO invalidCourseContent = CourseContentUrlInDTO.builder()
                .courseId(1L)
                .title("Test Title")
                .description("")
                .contentType("VIDEO")
                .youtubeUrl("https://www.youtube.com/watch?v=example")
                .isActive(true)
                .build();

        // When & Then
        mockMvc.perform(post("/api/service-api/course-content")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCourseContent)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createCourseContentWithUrl_ShouldReturnBadRequest_WhenNullIsActive() throws Exception {
        // Given - Null isActive field
        CourseContentUrlInDTO invalidCourseContent = CourseContentUrlInDTO.builder()
                .courseId(1L)
                .title("Test Title")
                .description("Test Description")
                .contentType("VIDEO")
                .youtubeUrl("https://www.youtube.com/watch?v=example")
                .isActive(null)
                .build();

        // When & Then
        mockMvc.perform(post("/api/service-api/course-content")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCourseContent)))
                .andExpect(status().isBadRequest());
    }

    // VALIDATION TESTS FOR FILE UPLOAD WITH EDGE CASES
    @Test
    void createCourseContentWithFile_ShouldReturnBadRequest_WhenNegativeCourseId() throws Exception {
        // When & Then
        mockMvc.perform(multipart("/api/service-api/course-content")
                        .file(mockFile)
                        .param("courseId", "-1")
                        .param("title", "Test Course Content")
                        .param("description", "This is a test course content description")
                        .param("contentType", "PDF")
                        .param("isActive", "true")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createCourseContentWithFile_ShouldReturnBadRequest_WhenBlankTitle() throws Exception {
        // When & Then
        mockMvc.perform(multipart("/api/service-api/course-content")
                        .file(mockFile)
                        .param("courseId", "1")
                        .param("title", "")
                        .param("description", "This is a test course content description")
                        .param("contentType", "PDF")
                        .param("isActive", "true")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createCourseContentWithFile_ShouldReturnBadRequest_WhenBlankDescription() throws Exception {
        // When & Then
        mockMvc.perform(multipart("/api/service-api/course-content")
                        .file(mockFile)
                        .param("courseId", "1")
                        .param("title", "Test Course Content")
                        .param("description", "")
                        .param("contentType", "PDF")
                        .param("isActive", "true")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createCourseContentWithFile_ShouldReturnBadRequest_WhenBlankContentType() throws Exception {
        // When & Then
        mockMvc.perform(multipart("/api/service-api/course-content")
                        .file(mockFile)
                        .param("courseId", "1")
                        .param("title", "Test Course Content")
                        .param("description", "This is a test course content description")
                        .param("contentType", "")
                        .param("isActive", "true")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isBadRequest());
    }

    // SUCCESSFUL SCENARIOS WITH DIFFERENT CONTENT TYPES
    @Test
    void createCourseContentWithFile_ShouldReturnCreated_WhenValidVideoContent() throws Exception {
        // Given
        MockMultipartFile videoFile = new MockMultipartFile("file", "test.mp4", "video/mp4", "test video content".getBytes());
        when(courseContentService.createCourseContent(any(CourseContentInDTO.class))).thenReturn(courseContentOutDTO);

        // When & Then
        mockMvc.perform(multipart("/api/service-api/course-content")
                        .file(videoFile)
                        .param("courseId", "1")
                        .param("title", "Test Video Content")
                        .param("description", "This is a test video course content")
                        .param("contentType", "VIDEO")
                        .param("isActive", "true")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    void createCourseContentWithUrl_ShouldReturnCreated_WhenValidYoutubeUrl() throws Exception {
        // Given
        CourseContentUrlInDTO validContent = CourseContentUrlInDTO.builder()
                .courseId(1L)
                .title("YouTube Tutorial")
                .description("This is a YouTube tutorial content")
                .contentType("VIDEO")
                .youtubeUrl("https://www.youtube.com/watch?v=dQw4w9WgXcQ")
                .isActive(true)
                .build();

        when(courseContentService.createCourseContent(any(CourseContentUrlInDTO.class))).thenReturn(courseContentOutDTO);

        // When & Then
        mockMvc.perform(post("/api/service-api/course-content")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validContent)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    void createCourseContentWithUrl_ShouldReturnCreated_WhenValidYoutubeShortUrl() throws Exception {
        // Given
        CourseContentUrlInDTO validContent = CourseContentUrlInDTO.builder()
                .courseId(1L)
                .title("YouTube Short Tutorial")
                .description("This is a YouTube short URL tutorial content")
                .contentType("VIDEO")
                .youtubeUrl("https://youtu.be/dQw4w9WgXcQ")
                .isActive(true)
                .build();

        when(courseContentService.createCourseContent(any(CourseContentUrlInDTO.class))).thenReturn(courseContentOutDTO);

        // When & Then
        mockMvc.perform(post("/api/service-api/course-content")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validContent)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    void createCourseContentWithFile_ShouldAcceptDifferentFileTypes() throws Exception {
        // Given
        MockMultipartFile[] files = {
                new MockMultipartFile("file", "test.pdf", "application/pdf", "PDF content".getBytes()),
                new MockMultipartFile("file", "test.docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "DOCX content".getBytes()),
                new MockMultipartFile("file", "test.pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation", "PPTX content".getBytes())
        };

        String[] contentTypes = {"PDF", "DOCUMENT", "PRESENTATION"};

        when(courseContentService.createCourseContent(any(CourseContentInDTO.class))).thenReturn(courseContentOutDTO);

        for (int i = 0; i < files.length; i++) {
            mockMvc.perform(multipart("/api/service-api/course-content")
                            .file(files[i])
                            .param("courseId", "1")
                            .param("title", "Test " + contentTypes[i] + " Content")
                            .param("description", "This is a test " + contentTypes[i] + " content")
                            .param("contentType", contentTypes[i])
                            .param("isActive", "true")
                            .header("X-Test-Role", "ADMIN"))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.status").value("SUCCESS"));
        }
    }

//    // PARAMETER VALIDATION TESTS
//    @Test
//    void createCourseContentWithFile_ShouldReturnBadRequest_WhenMissingRequiredParams() throws Exception {
//        // Test missing courseId
//        mockMvc.perform(multipart("/api/service-api/course-content")
//                        .file(mockFile)
//                        .param("title", "Test Course Content")
//                        .param("description", "This is a test course content description")
//                        .param("contentType", "PDF")
//                        .param("isActive", "true")
//                        .header("X-Test-Role", "ADMIN"))
//                .andExpect(status().isBadRequest());
//
//        // Test missing title
//        mockMvc.perform(multipart("/api/service-api/course-content")
//                        .file(mockFile)
//                        .param("courseId", "1")
//                        .param("description", "This is a test course content description")
//                        .param("contentType", "PDF")
//                        .param("isActive", "true")
//                        .header("X-Test-Role", "ADMIN"))
//                .andExpect(status().isBadRequest());
//
//        // Test missing description
//        mockMvc.perform(multipart("/api/service-api/course-content")
//                        .file(mockFile)
//                        .param("courseId", "1")
//                        .param("title", "Test Course Content")
//                        .param("contentType", "PDF")
//                        .param("isActive", "true")
//                        .header("X-Test-Role", "ADMIN"))
//                .andExpect(status().isBadRequest());
//    }


    @Test
    void createCourseContentWithUrl_ShouldReturnBadRequest_WhenDescriptionTooLong() throws Exception {
        // Given - Description exceeding max length
        String longDescription = String.join("", Collections.nCopies(1001, "a")); // Assuming max is 1000
        CourseContentUrlInDTO invalidCourseContent = CourseContentUrlInDTO.builder()
                .courseId(1L)
                .title("Test Title")
                .description(longDescription)
                .contentType("VIDEO")
                .youtubeUrl("https://www.youtube.com/watch?v=example")
                .isActive(true)
                .build();

        // When & Then
        mockMvc.perform(post("/api/service-api/course-content")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCourseContent)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createCourseContentWithUrl_ShouldReturnBadRequest_WhenTitleTooLong() throws Exception {
        // Given - Title exceeding max length
        String longTitle = String.join("", Collections.nCopies(101, "a")); // Assuming max is 100
        CourseContentUrlInDTO invalidCourseContent = CourseContentUrlInDTO.builder()
                .courseId(1L)
                .title(longTitle)
                .description("Test Description")
                .contentType("VIDEO")
                .youtubeUrl("https://www.youtube.com/watch?v=example")
                .isActive(true)
                .build();

        // When & Then
        mockMvc.perform(post("/api/service-api/course-content")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCourseContent)))
                .andExpect(status().isBadRequest());
    }

//    @Test
//    void createCourseContentWithUrl_ShouldRet

    @Test
    void createCourseContentWithFile_ShouldReturnBadRequest_WhenInvalidInput() throws Exception {
        // When & Then - Missing title
        mockMvc.perform(multipart("/api/service-api/course-content")
                        .file(mockFile)
                        .param("courseId", "1")
                        .param("title", "")
                        .param("description", "This is a test description")
                        .param("contentType", "PDF")
                        .param("isActive", "true")
                        .header("X-Test-Role", "ADMIN"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createCourseContentWithFile_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(multipart("/api/service-api/course-content")
                        .file(mockFile)
                        .param("courseId", "1")
                        .param("title", "Test Course Content")
                        .param("description", "This is a test course content description")
                        .param("contentType", "PDF")
                        .param("isActive", "true")
                        .header("X-Test-Role", "EMPLOYEE"))
                .andExpect(status().isForbidden());
    }

    // CREATE COURSE CONTENT WITH URL TESTS
    @Test
    void createCourseContentWithUrl_ShouldReturnCreatedCourseContent_WhenValidInput() throws Exception {
        // Given
        when(courseContentService.createCourseContent(any(CourseContentUrlInDTO.class))).thenReturn(courseContentOutDTO);

        // When & Then
        mockMvc.perform(post("/api/service-api/course-content")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseContentUrlInDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Course Content Created Successfully"))
                .andExpect(jsonPath("$.data.courseContentId").value(1L))
                .andExpect(jsonPath("$.data.courseId").value(1L))
                .andExpect(jsonPath("$.data.title").value("Test Course Content"))
                .andExpect(jsonPath("$.data.description").value("This is a test course content description"))
                .andExpect(jsonPath("$.data.active").value(true));
    }

    @Test
    void createCourseContentWithUrl_ShouldReturnBadRequest_WhenInvalidInput() throws Exception {
        // Given - Invalid course content with blank title
        CourseContentUrlInDTO invalidCourseContent = CourseContentUrlInDTO.builder()
                .courseId(1L)
                .title("")
                .description("This is a test description")
                .contentType("VIDEO")
                .youtubeUrl("https://www.youtube.com/watch?v=example")
                .isActive(true)
                .build();

        // When & Then
        mockMvc.perform(post("/api/service-api/course-content")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCourseContent)))
                .andExpect(status().isBadRequest());
    }

//    @Test
//    void createCourseContentWithUrl_ShouldReturnConflict_WhenResourceAlreadyExists() throws Exception {
//        // Given
//        when(courseContentService.createCourseContent(any(CourseContentUrlInDTO.class)))
//                .thenThrow(new ResourceAlreadyExistsException("Course content already exists"));
//
//        // When & Then
//        mockMvc.perform(post("/api/service-api/course-content")
//                        .header("X-Test-Role", "ADMIN")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(courseContentUrlInDTO)))
//                .andExpect(status().isConflict());
//    }

    @Test
    void createCourseContentWithUrl_ShouldReturnBadRequest_WhenCourseNotFound() throws Exception {
        // Given
        when(courseContentService.createCourseContent(any(CourseContentUrlInDTO.class)))
                .thenThrow(new ResourceNotFoundException("Course not found"));

        // When & Then
        mockMvc.perform(post("/api/service-api/course-content")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseContentUrlInDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void createCourseContentWithUrl_ShouldReturnForbidden_WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/service-api/course-content")
                        .header("X-Test-Role", "EMPLOYEE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseContentUrlInDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    void createCourseContentWithUrl_ShouldReturnBadRequest_WhenNullYoutubeUrl() throws Exception {
        // Given - Null YouTube URL
        CourseContentUrlInDTO invalidCourseContent = CourseContentUrlInDTO.builder()
                .courseId(1L)
                .title("Test Title")
                .description("Test Description")
                .contentType("VIDEO")
                .youtubeUrl(null)
                .isActive(true)
                .build();

        // When & Then
        mockMvc.perform(post("/api/service-api/course-content")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCourseContent)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createCourseContentWithUrl_ShouldReturnBadRequest_WhenNegativeCourseId() throws Exception {
        // Given - Negative course ID
        CourseContentUrlInDTO invalidCourseContent = CourseContentUrlInDTO.builder()
                .courseId(-1L)
                .title("Test Title")
                .description("Test Description")
                .contentType("VIDEO")
                .youtubeUrl("https://www.youtube.com/watch?v=example")
                .isActive(true)
                .build();

        // When & Then
        mockMvc.perform(post("/api/service-api/course-content")
                        .header("X-Test-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCourseContent)))
                .andExpect(status().isBadRequest());
    }
}