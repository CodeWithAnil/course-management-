package com.nt.course_service_lms.dtoTest.inDTOTest;

import com.nt.course_service_lms.dto.inDTO.CourseContentUrlInDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CourseContentUrlInDTOTest {

    private Validator validator;

    @BeforeEach
    void initValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenValidDTO_thenNoViolations() {
        CourseContentUrlInDTO dto = new CourseContentUrlInDTO(
                1L,
                "Java Basics",
                "This is a basic Java course",
                "VIDEO",
                80.0f,
                "https://www.youtube.com/watch?v=example",
                true
        );
        Set<ConstraintViolation<CourseContentUrlInDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenCourseIdIsNegative_thenViolation() {
        CourseContentUrlInDTO dto = new CourseContentUrlInDTO();
        dto.setCourseId(-1);
        dto.setTitle("Title");
        dto.setDescription("Description");
        dto.setContentType("VIDEO");
        dto.setMinCompletionPercentage(75.0f);
        dto.setYoutubeUrl("https://www.youtube.com/watch?v=example");
        dto.setIsActive(true);

        Set<ConstraintViolation<CourseContentUrlInDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("courseId")));
    }

    @Test
    void whenTitleIsNull_thenViolation() {
        CourseContentUrlInDTO dto = new CourseContentUrlInDTO(1L, null, "Description", "VIDEO", 80.0f, "https://youtube.com/watch?v=test", true);
        Set<ConstraintViolation<CourseContentUrlInDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("title")));
    }

    @Test
    void whenTitleIsBlank_thenViolation() {
        CourseContentUrlInDTO dto = new CourseContentUrlInDTO(1L, "  ", "Description", "VIDEO", 80.0f, "https://youtube.com/watch?v=test", true);
        Set<ConstraintViolation<CourseContentUrlInDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("title")));
    }

    @Test
    void whenTitleTooLong_thenViolation() {
        String longTitle = new String(new char[101]).replace('\0', 'A');
        CourseContentUrlInDTO dto = new CourseContentUrlInDTO(1L, longTitle, "Description", "VIDEO", 80.0f, "https://youtube.com/watch?v=test", true);
        Set<ConstraintViolation<CourseContentUrlInDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("title")));
    }

    @Test
    void whenDescriptionIsNull_thenViolation() {
        CourseContentUrlInDTO dto = new CourseContentUrlInDTO(1L, "Title", null, "VIDEO", 80.0f, "https://youtube.com/watch?v=test", true);
        Set<ConstraintViolation<CourseContentUrlInDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("description")));
    }

    @Test
    void whenDescriptionIsBlank_thenViolation() {
        CourseContentUrlInDTO dto = new CourseContentUrlInDTO(1L, "Title", "   ", "VIDEO", 80.0f, "https://youtube.com/watch?v=test", true);
        Set<ConstraintViolation<CourseContentUrlInDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("description")));
    }

    @Test
    void whenDescriptionTooLong_thenViolation() {
        String longDesc = new String(new char[1001]).replace('\0', 'D');
        CourseContentUrlInDTO dto = new CourseContentUrlInDTO(1L, "Title", longDesc, "VIDEO", 80.0f, "https://youtube.com/watch?v=test", true);
        Set<ConstraintViolation<CourseContentUrlInDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("description")));
    }

    @Test
    void whenContentTypeIsNull_thenViolation() {
        CourseContentUrlInDTO dto = new CourseContentUrlInDTO(1L, "Title", "Description", null, 80.0f, "https://youtube.com/watch?v=test", true);
        Set<ConstraintViolation<CourseContentUrlInDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("contentType")));
    }

    @Test
    void whenContentTypeIsBlank_thenViolation() {
        CourseContentUrlInDTO dto = new CourseContentUrlInDTO(1L, "Title", "Description", "  ", 80.0f, "https://youtube.com/watch?v=test", true);
        Set<ConstraintViolation<CourseContentUrlInDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("contentType")));
    }

    @Test
    void whenMinCompletionPercentageIsValid_thenNoViolation() {
        CourseContentUrlInDTO dto = new CourseContentUrlInDTO(1L, "Title", "Description", "VIDEO", 75.5f, "https://youtube.com/watch?v=test", true);
        Set<ConstraintViolation<CourseContentUrlInDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenMinCompletionPercentageIsZero_thenNoViolation() {
        CourseContentUrlInDTO dto = new CourseContentUrlInDTO(1L, "Title", "Description", "VIDEO", 0.0f, "https://youtube.com/watch?v=test", true);
        Set<ConstraintViolation<CourseContentUrlInDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenMinCompletionPercentageIsHundred_thenNoViolation() {
        CourseContentUrlInDTO dto = new CourseContentUrlInDTO(1L, "Title", "Description", "VIDEO", 100.0f, "https://youtube.com/watch?v=test", true);
        Set<ConstraintViolation<CourseContentUrlInDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenYoutubeUrlIsNull_thenViolation() {
        CourseContentUrlInDTO dto = new CourseContentUrlInDTO(1L, "Title", "Description", "VIDEO", 80.0f, null, true);
        Set<ConstraintViolation<CourseContentUrlInDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("youtubeUrl")));
    }

    @Test
    void whenIsActiveIsNull_thenViolation() {
        CourseContentUrlInDTO dto = new CourseContentUrlInDTO(1L, "Title", "Description", "VIDEO", 80.0f, "https://youtube.com/watch?v=test", null);
        Set<ConstraintViolation<CourseContentUrlInDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("isActive")));
    }

    @Test
    void whenIsActiveFalse_thenStillValid() {
        CourseContentUrlInDTO dto = new CourseContentUrlInDTO(1L, "Title", "Description", "VIDEO", 80.0f, "https://youtube.com/watch?v=test", false);
        Set<ConstraintViolation<CourseContentUrlInDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testGettersAndSetters() {
        CourseContentUrlInDTO dto = new CourseContentUrlInDTO();
        dto.setCourseId(100L);
        dto.setTitle("Sample");
        dto.setDescription("Some description");
        dto.setContentType("VIDEO");
        dto.setMinCompletionPercentage(85.0f);
        dto.setYoutubeUrl("https://www.youtube.com/watch?v=sample");
        dto.setIsActive(true);

        assertEquals(100L, dto.getCourseId());
        assertEquals("Sample", dto.getTitle());
        assertEquals("Some description", dto.getDescription());
        assertEquals("VIDEO", dto.getContentType());
        assertEquals(85.0f, dto.getMinCompletionPercentage(), 0.001f);
        assertEquals("https://www.youtube.com/watch?v=sample", dto.getYoutubeUrl());
        assertTrue(dto.getIsActive());
    }

    @Test
    void testNoArgsConstructor() {
        CourseContentUrlInDTO dto = new CourseContentUrlInDTO();
        assertNotNull(dto);
    }

    @Test
    void testAllArgsConstructor() {
        CourseContentUrlInDTO dto = new CourseContentUrlInDTO(1L, "Title", "Desc", "VIDEO", 90.0f, "https://youtube.com/watch?v=test", true);
        assertEquals(1L, dto.getCourseId());
        assertEquals("Title", dto.getTitle());
        assertEquals("Desc", dto.getDescription());
        assertEquals("VIDEO", dto.getContentType());
        assertEquals(90.0f, dto.getMinCompletionPercentage(), 0.001f);
        assertEquals("https://youtube.com/watch?v=test", dto.getYoutubeUrl());
        assertTrue(dto.getIsActive());
    }

    @Test
    void testEqualsAndHashCode_sameValues() {
        CourseContentUrlInDTO dto1 = new CourseContentUrlInDTO(1L, "T", "D", "VIDEO", 80.0f, "https://youtube.com/watch?v=test", true);
        CourseContentUrlInDTO dto2 = new CourseContentUrlInDTO(1L, "T", "D", "VIDEO", 80.0f, "https://youtube.com/watch?v=test", true);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testEqualsAndHashCode_differentValues() {
        CourseContentUrlInDTO dto1 = new CourseContentUrlInDTO(1L, "T1", "D1", "VIDEO", 80.0f, "https://youtube.com/watch?v=test1", true);
        CourseContentUrlInDTO dto2 = new CourseContentUrlInDTO(2L, "T2", "D2", "AUDIO", 90.0f, "https://youtube.com/watch?v=test2", false);

        assertNotEquals(dto1, dto2);
        assertNotEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testEqualsAndHashCode_differentMinCompletionPercentage() {
        CourseContentUrlInDTO dto1 = new CourseContentUrlInDTO(1L, "T", "D", "VIDEO", 80.0f, "https://youtube.com/watch?v=test", true);
        CourseContentUrlInDTO dto2 = new CourseContentUrlInDTO(1L, "T", "D", "VIDEO", 85.0f, "https://youtube.com/watch?v=test", true);

        assertNotEquals(dto1, dto2);
        assertNotEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testEquals_differentTypeAndNull() {
        CourseContentUrlInDTO dto = new CourseContentUrlInDTO(1L, "T", "D", "VIDEO", 80.0f, "https://youtube.com/watch?v=test", true);
        assertNotEquals(dto, null);
        assertNotEquals(dto, "string");
    }

    @Test
    void testValidContentTypes() {
        String[] validContentTypes = {"VIDEO", "AUDIO", "PLAYLIST", "LIVE"};

        for (String contentType : validContentTypes) {
            CourseContentUrlInDTO dto = new CourseContentUrlInDTO(1L, "Title", "Description", contentType, 80.0f, "https://youtube.com/watch?v=test", true);
            Set<ConstraintViolation<CourseContentUrlInDTO>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty(), "Content type " + contentType + " should be valid");
        }
    }

    @Test
    void testValidYoutubeUrls() {
        String[] validUrls = {
                "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
                "https://youtube.com/watch?v=dQw4w9WgXcQ",
                "https://youtu.be/dQw4w9WgXcQ",
                "https://www.youtube.com/embed/dQw4w9WgXcQ",
                "https://www.youtube.com/playlist?list=PLExample"
        };

        for (String url : validUrls) {
            CourseContentUrlInDTO dto = new CourseContentUrlInDTO(1L, "Title", "Description", "VIDEO", 80.0f, url, true);
            Set<ConstraintViolation<CourseContentUrlInDTO>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty(), "YouTube URL " + url + " should be valid");
        }
    }

    @Test
    void testBuilder() {
        CourseContentUrlInDTO dto = CourseContentUrlInDTO.builder()
                .courseId(1L)
                .title("Test Title")
                .description("Test Description")
                .contentType("VIDEO")
                .minCompletionPercentage(75.5f)
                .youtubeUrl("https://youtube.com/watch?v=test")
                .isActive(true)
                .build();

        assertEquals(1L, dto.getCourseId());
        assertEquals("Test Title", dto.getTitle());
        assertEquals("Test Description", dto.getDescription());
        assertEquals("VIDEO", dto.getContentType());
        assertEquals(75.5f, dto.getMinCompletionPercentage(), 0.001f);
        assertEquals("https://youtube.com/watch?v=test", dto.getYoutubeUrl());
        assertTrue(dto.getIsActive());
    }

    @Test
    void testMinCompletionPercentageEdgeCases() {
        // Test with negative value (should be valid as there's no validation constraint)
        CourseContentUrlInDTO dto1 = new CourseContentUrlInDTO(1L, "Title", "Description", "VIDEO", -10.0f, "https://youtube.com/watch?v=test", true);
        Set<ConstraintViolation<CourseContentUrlInDTO>> violations1 = validator.validate(dto1);
        assertTrue(violations1.isEmpty());

        // Test with value over 100 (should be valid as there's no validation constraint)
        CourseContentUrlInDTO dto2 = new CourseContentUrlInDTO(1L, "Title", "Description", "VIDEO", 150.0f, "https://youtube.com/watch?v=test", true);
        Set<ConstraintViolation<CourseContentUrlInDTO>> violations2 = validator.validate(dto2);
        assertTrue(violations2.isEmpty());

        // Test with decimal precision
        CourseContentUrlInDTO dto3 = new CourseContentUrlInDTO(1L, "Title", "Description", "VIDEO", 85.75f, "https://youtube.com/watch?v=test", true);
        Set<ConstraintViolation<CourseContentUrlInDTO>> violations3 = validator.validate(dto3);
        assertTrue(violations3.isEmpty());
    }
}