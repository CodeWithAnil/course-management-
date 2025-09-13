package com.nt.course_service_lms.IntegrationTest;

import com.nt.course_service_lms.dto.inDTO.CourseContentUrlInDTO;
import com.nt.course_service_lms.dto.inDTO.UpdateCourseContentInDTO;
import com.nt.course_service_lms.dto.outDTO.CourseContentOutDTO;
import com.nt.course_service_lms.dto.outDTO.StandardResponseOutDTO;
import com.nt.course_service_lms.entity.Course;
import com.nt.course_service_lms.entity.CourseContent;
import com.nt.course_service_lms.exception.ErrorResponse;
import com.nt.course_service_lms.repository.CourseContentRepository;
import com.nt.course_service_lms.repository.CourseRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class CourseContentControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CourseContentRepository courseContentRepository;

    @Autowired
    private CourseRepository courseRepository;

    private static Long testCourseId;
    private static Long secondTestCourseId;
    private static Long createdContentId;
    private static Long secondContentId;
    private static Long thirdContentId;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/service-api/course-content";
    }

    private HttpHeaders createAdminHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Test-User", "test-admin");
        headers.set("X-Test-Role", "ADMIN");
        return headers;
    }

    private HttpHeaders createAdminMultipartHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("X-Test-User", "test-admin");
        headers.set("X-Test-Role", "ADMIN");
        return headers;
    }

    private HttpHeaders createEmployeeHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Test-User", "test-employee");
        headers.set("X-Test-Role", "EMPLOYEE");
        return headers;
    }

    // ==================== SETUP TEST DATA ====================

    @Test
    @Order(1)
    void setupTestData() {
        // Create test courses first
        Course testCourse1 = Course.builder()
                .title("Spring Boot Fundamentals")
                .description("Learn Spring Boot basics")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Course savedCourse1 = courseRepository.save(testCourse1);
        testCourseId = savedCourse1.getCourseId();

        Course testCourse2 = Course.builder()
                .title("Advanced Java Concepts")
                .description("Deep dive into Java")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Course savedCourse2 = courseRepository.save(testCourse2);
        secondTestCourseId = savedCourse2.getCourseId();

        // Create test course contents
        CourseContent content1 = CourseContent.builder()
                .courseId(testCourseId)
                .title("Introduction to Spring Boot")
                .description("Basic overview of Spring Boot framework and its features")
                .resourceLink("https://example.com/spring-boot-intro")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        CourseContent savedContent1 = courseContentRepository.save(content1);
        createdContentId = savedContent1.getCourseContentId();

        CourseContent content2 = CourseContent.builder()
                .courseId(testCourseId)
                .title("Spring Boot Configuration")
                .description("Learn about application properties and configuration")
                .resourceLink("https://example.com/spring-boot-config")
                .isActive(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        CourseContent savedContent2 = courseContentRepository.save(content2);
        secondContentId = savedContent2.getCourseContentId();

        CourseContent content3 = CourseContent.builder()
                .courseId(secondTestCourseId)
                .title("Java Collections Framework")
                .description("Deep dive into Java collections")
                .resourceLink("https://example.com/java-collections")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        CourseContent savedContent3 = courseContentRepository.save(content3);
        thirdContentId = savedContent3.getCourseContentId();

        System.out.println("Test Course ID: " + testCourseId);
        System.out.println("Created Content ID: " + createdContentId);
    }

    // ==================== CREATE COURSE CONTENT TESTS (JSON/URL) ====================

    @Test
    @Order(2)
    void shouldCreateCourseContentWithYouTubeUrlSuccessfully() {
        CourseContentUrlInDTO request = CourseContentUrlInDTO.builder()
                .courseId(testCourseId)
                .title("Spring Boot Testing")
                .description("Learn how to test Spring Boot applications effectively")
                .contentType("VIDEO")
                .youtubeUrl("https://www.youtube.com/watch?v=dQw4w9WgXcQ")
                .isActive(true)
                .build();

        HttpEntity<CourseContentUrlInDTO> entity = new HttpEntity<>(request, createAdminHeaders());

        ResponseEntity<StandardResponseOutDTO<CourseContentOutDTO>> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData().getTitle()).isEqualTo("Spring Boot Testing");
        assertThat(response.getBody().getData().getCourseId()).isEqualTo(testCourseId);
        assertThat(response.getBody().getData().isActive()).isTrue();
        assertThat(response.getBody().getData().getCreatedAt()).isNotNull();
    }

//    @Test
//    @Order(3)
//    void shouldCreateCourseContentWithFileUploadSuccessfully() {
//        // Create multipart request
//        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
//        parts.add("courseId", testCourseId);
//        parts.add("title", "Spring Boot File Upload Tutorial");
//        parts.add("description", "Learn how to handle file uploads in Spring Boot");
//        parts.add("contentType", "PDF");
//        parts.add("isActive", true);
//
//        // Create a mock file
//        ByteArrayResource fileResource = new ByteArrayResource("PDF content".getBytes()) {
//            @Override
//            public String getFilename() {
//                return "test-file.pdf";
//            }
//        };
//        parts.add("file", fileResource);
//
//        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(parts, createAdminMultipartHeaders());
//
//        ResponseEntity<StandardResponseOutDTO<CourseContentOutDTO>> response = restTemplate.exchange(
//                getBaseUrl(),
//                HttpMethod.POST,
//                entity,
//                new ParameterizedTypeReference<>() {
//                }
//        );
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
//        assertThat(response.getBody()).isNotNull();
//        assertThat(response.getBody().getData().getTitle()).isEqualTo("Spring Boot File Upload Tutorial");
//        assertThat(response.getBody().getData().getCourseId()).isEqualTo(testCourseId);
//        assertThat(response.getBody().getData().isActive()).isTrue();
//    }

    @Test
    @Order(4)
    void shouldRejectDuplicateCourseContentTitle() {
        CourseContentUrlInDTO request = CourseContentUrlInDTO.builder()
                .courseId(testCourseId)
                .title("Introduction to Spring Boot") // Same title as existing content
                .description("Another introduction to Spring Boot")
                .contentType("VIDEO")
                .youtubeUrl("https://www.youtube.com/watch?v=another")
                .isActive(true)
                .build();

        HttpEntity<CourseContentUrlInDTO> entity = new HttpEntity<>(request, createAdminHeaders());

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().getMessage()).contains("Course Content Already Present");
    }

    @Test
    @Order(5)
    void shouldRejectCourseContentForNonExistingCourse() {
        CourseContentUrlInDTO request = CourseContentUrlInDTO.builder()
                .courseId(999999L) // Non-existing course
                .title("Invalid Course Content")
                .description("This should fail")
                .contentType("VIDEO")
                .youtubeUrl("https://www.youtube.com/watch?v=invalid")
                .isActive(true)
                .build();

        HttpEntity<CourseContentUrlInDTO> entity = new HttpEntity<>(request, createAdminHeaders());

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getMessage()).contains("Course does not exists");
    }

    @Test
    @Order(6)
    void shouldRejectInvalidCourseContentData() {
        CourseContentUrlInDTO request = CourseContentUrlInDTO.builder()
                .courseId(testCourseId)
                .title("") // Empty title
                .description("Valid description")
                .contentType("VIDEO")
                .youtubeUrl("https://www.youtube.com/watch?v=valid")
                .isActive(true)
                .build();

        HttpEntity<CourseContentUrlInDTO> entity = new HttpEntity<>(request, createAdminHeaders());

        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<Map<String, String>>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsKey("title");
    }

//    @Test
//    @Order(7)
//    void shouldRejectInvalidYouTubeUrl() {
//        CourseContentUrlInDTO request = CourseContentUrlInDTO.builder()
//                .courseId(testCourseId)
//                .title("Valid Title")
//                .description("Valid description")
//                .contentType("VIDEO")
//                .youtubeUrl("") // Empty YouTube URL
//                .isActive(true)
//                .build();
//
//        HttpEntity<CourseContentUrlInDTO> entity = new HttpEntity<>(request, createAdminHeaders());
//
//        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
//                getBaseUrl(),
//                HttpMethod.POST,
//                entity,
//                new ParameterizedTypeReference<Map<String, String>>() {
//                }
//        );
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
//        assertThat(response.getBody()).containsKey("youtubeUrl");
//    }

    @Test
    @Order(8)
    void shouldRejectMissingFileInMultipartRequest() {
        // Create multipart request without file
        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        parts.add("courseId", testCourseId);
        parts.add("title", "Missing File Content");
        parts.add("description", "This should fail");
        parts.add("contentType", "PDF");
        parts.add("isActive", true);
        // No file added

        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(parts, createAdminMultipartHeaders());

        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<Map<String, String>>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsKey("file");
    }

    @Test
    @Order(9)
    void shouldDenyCourseContentCreationForNonAdmin() {
        CourseContentUrlInDTO request = CourseContentUrlInDTO.builder()
                .courseId(testCourseId)
                .title("Unauthorized Content")
                .description("This should be rejected")
                .contentType("VIDEO")
                .youtubeUrl("https://www.youtube.com/watch?v=unauthorized")
                .isActive(true)
                .build();

        HttpEntity<CourseContentUrlInDTO> entity = new HttpEntity<>(request, createEmployeeHeaders());

        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    // ==================== GET COURSE CONTENT TESTS ====================

    @Test
    @Order(10)
    void shouldGetAllCourseContentsAsEmployee() {
        HttpEntity<Void> entity = new HttpEntity<>(createEmployeeHeaders());

        ResponseEntity<StandardResponseOutDTO<List<CourseContentOutDTO>>> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData()).isNotEmpty();
        assertThat(response.getBody().getData().size()).isGreaterThanOrEqualTo(3);
    }

    @Test
    @Order(11)
    void shouldGetCourseContentById() {
        HttpEntity<Void> entity = new HttpEntity<>(createEmployeeHeaders());

        ResponseEntity<StandardResponseOutDTO<CourseContentOutDTO>> response = restTemplate.exchange(
                getBaseUrl() + "/" + createdContentId,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData().getCourseContentId()).isEqualTo(createdContentId);
        assertThat(response.getBody().getData().getTitle()).isEqualTo("Introduction to Spring Boot");
        assertThat(response.getBody().getData().getCourseId()).isEqualTo(testCourseId);
    }

    @Test
    @Order(12)
    void shouldReturn404ForNonExistingCourseContent() {
        HttpEntity<Void> entity = new HttpEntity<>(createEmployeeHeaders());

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl() + "/999999",
                HttpMethod.GET,
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @Order(13)
    void shouldGetCourseContentByCourseId() {
        HttpEntity<Void> entity = new HttpEntity<>(createEmployeeHeaders());

        ResponseEntity<StandardResponseOutDTO<List<CourseContentOutDTO>>> response = restTemplate.exchange(
                getBaseUrl() + "/course/" + testCourseId,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData()).isNotEmpty();
        assertThat(response.getBody().getData().size()).isGreaterThanOrEqualTo(2);
        assertThat(response.getBody().getData()).allMatch(content -> content.getCourseId() == testCourseId);
    }

    @Test
    @Order(14)
    void shouldReturn404ForNonExistingCourseContents() {
        HttpEntity<Void> entity = new HttpEntity<>(createEmployeeHeaders());

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl() + "/course/999999",
                HttpMethod.GET,
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @Order(15)
    void shouldGetCourseContentCount() {
        HttpEntity<Void> entity = new HttpEntity<>(createEmployeeHeaders());

        ResponseEntity<StandardResponseOutDTO<Integer>> response = restTemplate.exchange(
                getBaseUrl() + "/course/" + testCourseId + "/count",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData()).isGreaterThanOrEqualTo(2);
    }

    // ==================== UPDATE COURSE CONTENT TESTS ====================

    @Test
    @Order(16)
    void shouldUpdateCourseContentSuccessfully() {
        UpdateCourseContentInDTO updateRequest = UpdateCourseContentInDTO.builder()
                .courseId(testCourseId)
                .title("Updated Spring Boot Introduction")
                .description("Updated description for Spring Boot introduction")
                .resourceLink("https://example.com/updated-intro")
                .isActive(false)
                .build();

        HttpEntity<UpdateCourseContentInDTO> entity = new HttpEntity<>(updateRequest, createAdminHeaders());

        ResponseEntity<StandardResponseOutDTO<CourseContentOutDTO>> response = restTemplate.exchange(
                getBaseUrl() + "/" + createdContentId,
                HttpMethod.PUT,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData().getTitle()).isEqualTo("Updated Spring Boot Introduction");
        assertThat(response.getBody().getData().isActive()).isFalse();
        assertThat(response.getBody().getData().getUpdatedAt()).isNotNull();
    }

    @Test
    @Order(17)
    void shouldAllowUpdateWithSameTitleForSameCourse() {
        UpdateCourseContentInDTO updateRequest = UpdateCourseContentInDTO.builder()
                .courseId(testCourseId)
                .title("Updated Spring Boot Introduction") // Same title as current
                .description("Same title is allowed for same content")
                .resourceLink("https://example.com/same-title")
                .isActive(true)
                .build();

        HttpEntity<UpdateCourseContentInDTO> entity = new HttpEntity<>(updateRequest, createAdminHeaders());

        ResponseEntity<StandardResponseOutDTO<CourseContentOutDTO>> response = restTemplate.exchange(
                getBaseUrl() + "/" + createdContentId,
                HttpMethod.PUT,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData().isActive()).isTrue();
    }

    @Test
    @Order(18)
    void shouldRejectUpdateWithDuplicateTitle() {
        UpdateCourseContentInDTO updateRequest = UpdateCourseContentInDTO.builder()
                .courseId(testCourseId)
                .title("Spring Boot Configuration") // Title exists for another content in same course
                .description("This should fail")
                .resourceLink("https://example.com/duplicate")
                .isActive(true)
                .build();

        HttpEntity<UpdateCourseContentInDTO> entity = new HttpEntity<>(updateRequest, createAdminHeaders());

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl() + "/" + createdContentId,
                HttpMethod.PUT,
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().getMessage()).containsAnyOf("duplicate", "already exists");
    }

    @Test
    @Order(19)
    void shouldReturn404WhenUpdatingNonExistingContent() {
        UpdateCourseContentInDTO updateRequest = UpdateCourseContentInDTO.builder()
                .courseId(testCourseId)
                .title("Valid Title")
                .description("Valid description")
                .resourceLink("https://example.com/valid")
                .isActive(true)
                .build();

        HttpEntity<UpdateCourseContentInDTO> entity = new HttpEntity<>(updateRequest, createAdminHeaders());

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl() + "/999999",
                HttpMethod.PUT,
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @Order(20)
    void shouldDenyUpdateForNonAdmin() {
        UpdateCourseContentInDTO updateRequest = UpdateCourseContentInDTO.builder()
                .courseId(testCourseId)
                .title("Unauthorized Update")
                .description("This should be rejected")
                .resourceLink("https://example.com/unauthorized")
                .isActive(true)
                .build();

        HttpEntity<UpdateCourseContentInDTO> entity = new HttpEntity<>(updateRequest, createEmployeeHeaders());

        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/" + createdContentId,
                HttpMethod.PUT,
                entity,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    // ==================== DELETE COURSE CONTENT TESTS ====================

    @Test
    @Order(21)
    void shouldDeleteCourseContentAsAdmin() {
        HttpEntity<Void> entity = new HttpEntity<>(createAdminHeaders());

        ResponseEntity<StandardResponseOutDTO<Void>> response = restTemplate.exchange(
                getBaseUrl() + "/" + secondContentId,
                HttpMethod.DELETE,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).contains("Course Content Deleted Successfully");
    }

    @Test
    @Order(22)
    void shouldReturn404WhenDeletingNonExistingContent() {
        HttpEntity<Void> entity = new HttpEntity<>(createAdminHeaders());

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                getBaseUrl() + "/999999",
                HttpMethod.DELETE,
                entity,
                ErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @Order(23)
    void shouldDenyDeleteForNonAdmin() {
        HttpEntity<Void> entity = new HttpEntity<>(createEmployeeHeaders());

        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/" + createdContentId,
                HttpMethod.DELETE,
                entity,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    // ==================== HEALTH CHECK TEST ====================

    @Test
    @Order(24)
    void shouldReturnHealthCheckStatus() {
        HttpEntity<Void> entity = new HttpEntity<>(createEmployeeHeaders());

        ResponseEntity<StandardResponseOutDTO<String>> response = restTemplate.exchange(
                getBaseUrl() + "/health",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData()).isEqualTo("UP");
        assertThat(response.getBody().getMessage()).contains("running");
    }

    // ==================== EDGE CASE TESTS ====================

    @Test
    @Order(25)
    void shouldRejectBlankTitle() {
        CourseContentUrlInDTO request = CourseContentUrlInDTO.builder()
                .courseId(testCourseId)
                .title("") // Blank title
                .description("Valid description")
                .contentType("VIDEO")
                .youtubeUrl("https://www.youtube.com/watch?v=valid")
                .isActive(true)
                .build();

        HttpEntity<CourseContentUrlInDTO> entity = new HttpEntity<>(request, createAdminHeaders());

        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<Map<String, String>>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsKey("title");
    }

    @Test
    @Order(26)
    void shouldRejectBlankDescription() {
        CourseContentUrlInDTO request = CourseContentUrlInDTO.builder()
                .courseId(testCourseId)
                .title("Valid Title")
                .description("") // Blank description
                .contentType("VIDEO")
                .youtubeUrl("https://www.youtube.com/watch?v=valid")
                .isActive(true)
                .build();

        HttpEntity<CourseContentUrlInDTO> entity = new HttpEntity<>(request, createAdminHeaders());

        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<Map<String, String>>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsKey("description");
    }

    @Test
    @Order(27)
    void shouldCreateCourseContentWithValidYouTubeUrls() {
        String[] validUrls = {
                "https://www.youtube.com/watch?v=test1",
                "https://youtube.com/watch?v=test2",
                "https://youtu.be/test3"
        };

        for (int i = 0; i < validUrls.length; i++) {
            CourseContentUrlInDTO request = CourseContentUrlInDTO.builder()
                    .courseId(testCourseId)
                    .title("YouTube Content " + (i + 1))
                    .description("Test YouTube content with valid URL")
                    .contentType("VIDEO")
                    .youtubeUrl(validUrls[i])
                    .isActive(true)
                    .build();

            HttpEntity<CourseContentUrlInDTO> entity = new HttpEntity<>(request, createAdminHeaders());

            ResponseEntity<StandardResponseOutDTO<CourseContentOutDTO>> response = restTemplate.exchange(
                    getBaseUrl(),
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<>() {
                    }
            );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        }
    }

    // ==================== CLEAN UP ====================

    @Test
    @Order(28)
    void cleanUpTestData() {
        HttpEntity<Void> entity = new HttpEntity<>(createAdminHeaders());

        // Get all course contents and clean up test data
        ResponseEntity<StandardResponseOutDTO<List<CourseContentOutDTO>>> allContents = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        if (allContents.getStatusCode() == HttpStatus.OK && !allContents.getBody().getData().isEmpty()) {
            allContents.getBody().getData().stream()
                    .filter(content -> content.getTitle().contains("Test") ||
                            content.getTitle().contains("Updated") ||
                            content.getTitle().contains("YouTube") ||
                            content.getTitle().contains("Spring Boot"))
                    .forEach(content -> {
                        try {
                            restTemplate.exchange(
                                    getBaseUrl() + "/" + content.getCourseContentId(),
                                    HttpMethod.DELETE,
                                    entity,
                                    new ParameterizedTypeReference<StandardResponseOutDTO<Void>>() {
                                    }
                            );
                        } catch (Exception e) {
                            // Ignore cleanup errors
                        }
                    });
        }

        // Clean up remaining test contents
        if (createdContentId != null) {
            try {
                restTemplate.exchange(
                        getBaseUrl() + "/" + createdContentId,
                        HttpMethod.DELETE,
                        entity,
                        new ParameterizedTypeReference<StandardResponseOutDTO<Void>>() {
                        }
                );
            } catch (Exception e) {
                // Ignore cleanup errors
            }
        }

        if (thirdContentId != null) {
            try {
                restTemplate.exchange(
                        getBaseUrl() + "/" + thirdContentId,
                        HttpMethod.DELETE,
                        entity,
                        new ParameterizedTypeReference<StandardResponseOutDTO<Void>>() {
                        }
                );
            } catch (Exception e) {
                // Ignore cleanup errors
            }
        }

        // Clean up test courses
        if (testCourseId != null) {
            try {
                courseRepository.deleteById(testCourseId);
            } catch (Exception e) {
                // Ignore cleanup errors
            }
        }
        if (secondTestCourseId != null) {
            try {
                courseRepository.deleteById(secondTestCourseId);
            } catch (Exception e) {
                // Ignore cleanup errors
            }
        }
    }
}