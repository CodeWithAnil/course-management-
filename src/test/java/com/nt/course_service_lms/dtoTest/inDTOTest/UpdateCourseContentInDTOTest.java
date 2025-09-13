package com.nt.course_service_lms.dtoTest.inDTOTest;

import com.nt.course_service_lms.dto.inDTO.UpdateCourseContentInDTO;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

class UpdateCourseContentInDTOTest {

    private Validator validator;

    @BeforeEach
    void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenValidInput_thenNoViolations() {
        UpdateCourseContentInDTO dto = new UpdateCourseContentInDTO(
                1L,
                "Intro to Java",
                "Comprehensive overview of Java programming.",
                "https://example.com/java-course",
                85.5f,
                true
        );
        Set<ConstraintViolation<UpdateCourseContentInDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenCourseIdIsNegative_thenViolation() {
        UpdateCourseContentInDTO dto = new UpdateCourseContentInDTO(
                -1L,
                "Valid Title",
                "Valid Description",
                null,
                75.0f,
                true
        );
        Set<ConstraintViolation<UpdateCourseContentInDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("courseId")));
    }

    @Test
    void whenTitleIsBlank_thenViolation() {
        UpdateCourseContentInDTO dto = new UpdateCourseContentInDTO(
                1L,
                "   ",
                "Valid Description",
                null,
                75.0f,
                true
        );
        Set<ConstraintViolation<UpdateCourseContentInDTO>> violations = validator.validate(dto);

        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("title")));
    }

    @Test
    void whenTitleIsNull_thenViolation() {
        UpdateCourseContentInDTO dto = new UpdateCourseContentInDTO(
                1L,
                null,
                "Valid Description",
                null,
                75.0f,
                true
        );
        Set<ConstraintViolation<UpdateCourseContentInDTO>> violations = validator.validate(dto);

        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("title")));
    }

    @Test
    void whenTitleExceedsMax_thenViolation() {
        String longTitle = new String(new char[101]).replace('\0', 'T');
        UpdateCourseContentInDTO dto = new UpdateCourseContentInDTO(
                1L,
                longTitle,
                "Valid Description",
                null,
                75.0f,
                true
        );
        Set<ConstraintViolation<UpdateCourseContentInDTO>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("title")));
    }

    @Test
    void whenDescriptionIsBlank_thenViolation() {
        UpdateCourseContentInDTO dto = new UpdateCourseContentInDTO(
                1L,
                "Valid Title",
                " ",
                null,
                75.0f,
                false
        );
        Set<ConstraintViolation<UpdateCourseContentInDTO>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("description")));
    }

    @Test
    void whenDescriptionIsNull_thenViolation() {
        UpdateCourseContentInDTO dto = new UpdateCourseContentInDTO(
                1L,
                "Valid Title",
                null,
                null,
                75.0f,
                false
        );
        Set<ConstraintViolation<UpdateCourseContentInDTO>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("description")));
    }

    @Test
    void whenDescriptionTooLong_thenViolation() {
        String longDesc = new String(new char[1001]).replace('\0', 'A');
        UpdateCourseContentInDTO dto = new UpdateCourseContentInDTO(
                1L,
                "Valid Title",
                longDesc,
                null,
                75.0f,
                true
        );
        Set<ConstraintViolation<UpdateCourseContentInDTO>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("description")));
    }

    @Test
    void whenInvalidResourceLink_thenViolation() {
        UpdateCourseContentInDTO dto = new UpdateCourseContentInDTO(
                1L,
                "Valid Title",
                "Valid Description",
                "invalid-url",
                75.0f,
                true
        );
        Set<ConstraintViolation<UpdateCourseContentInDTO>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("resourceLink")));
    }

    @Test
    void whenEmptyResourceLink_thenValid() {
        UpdateCourseContentInDTO dto = new UpdateCourseContentInDTO(
                1L,
                "Valid Title",
                "Valid Description",
                "",
                75.0f,
                true
        );
        Set<ConstraintViolation<UpdateCourseContentInDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenNullResourceLink_thenValid() {
        UpdateCourseContentInDTO dto = new UpdateCourseContentInDTO(
                1L,
                "Valid Title",
                "Valid Description",
                null,
                75.0f,
                true
        );
        Set<ConstraintViolation<UpdateCourseContentInDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenValidHttpsResourceLink_thenValid() {
        UpdateCourseContentInDTO dto = new UpdateCourseContentInDTO(
                1L,
                "Valid Title",
                "Valid Description",
                "https://example.com/resource",
                75.0f,
                true
        );
        Set<ConstraintViolation<UpdateCourseContentInDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenValidFtpResourceLink_thenValid() {
        UpdateCourseContentInDTO dto = new UpdateCourseContentInDTO(
                1L,
                "Valid Title",
                "Valid Description",
                "ftp://example.com/resource",
                75.0f,
                true
        );
        Set<ConstraintViolation<UpdateCourseContentInDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }


    @Test
    void testBuilderCreatesValidObject() {
        UpdateCourseContentInDTO dto = UpdateCourseContentInDTO.builder()
                .courseId(1L)
                .title("Builder Title")
                .description("Builder Description")
                .resourceLink("https://builder.com")
                .minCompletionPercentage(80.0f)
                .isActive(true)
                .build();

        Set<ConstraintViolation<UpdateCourseContentInDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
        assertEquals("Builder Title", dto.getTitle());
        assertEquals(80.0f, dto.getMinCompletionPercentage());
    }

    @Test
    void testDefaultConstructorAndSetters() {
        UpdateCourseContentInDTO dto = new UpdateCourseContentInDTO();
        dto.setCourseId(42L);
        dto.setTitle("Course Title");
        dto.setDescription("Some description");
        dto.setResourceLink("https://valid.com");
        dto.setMinCompletionPercentage(90.5f);
        dto.setActive(true);

        assertEquals(42L, dto.getCourseId());
        assertEquals("Course Title", dto.getTitle());
        assertEquals("Some description", dto.getDescription());
        assertEquals("https://valid.com", dto.getResourceLink());
        assertEquals(90.5f, dto.getMinCompletionPercentage());
        assertTrue(dto.isActive());
    }

    @Test
    void testEqualsAndHashCode() {
        UpdateCourseContentInDTO dto1 = new UpdateCourseContentInDTO(1L, "Title", "Desc", "", 75.0f, true);
        UpdateCourseContentInDTO dto2 = new UpdateCourseContentInDTO(1L, "Title", "Desc", "", 75.0f, true);
        UpdateCourseContentInDTO dto3 = new UpdateCourseContentInDTO(2L, "Other", "Other", "", 85.0f, false);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1, dto3);
        assertNotEquals(dto1, null);
        assertNotEquals(dto1, "random string");
    }

    @Test
    void testEqualsWithDifferentMinCompletionPercentage() {
        UpdateCourseContentInDTO dto1 = new UpdateCourseContentInDTO(1L, "Title", "Desc", "", 75.0f, true);
        UpdateCourseContentInDTO dto2 = new UpdateCourseContentInDTO(1L, "Title", "Desc", "", 80.0f, true);

        assertNotEquals(dto1, dto2);
        assertNotEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testBoundaryValues() {
        // Test with 0% completion percentage
        UpdateCourseContentInDTO dto1 = new UpdateCourseContentInDTO(0L, "A", "B", "", 0.0f, false);
        Set<ConstraintViolation<UpdateCourseContentInDTO>> violations1 = validator.validate(dto1);
        assertTrue(violations1.isEmpty());

        // Test with 100% completion percentage
        UpdateCourseContentInDTO dto2 = new UpdateCourseContentInDTO(0L, "A", "B", "", 100.0f, false);
        Set<ConstraintViolation<UpdateCourseContentInDTO>> violations2 = validator.validate(dto2);
        assertTrue(violations2.isEmpty());

        // Test with maximum title length (100 characters)
        String maxTitle = new String(new char[100]).replace('\0', 'T');
        UpdateCourseContentInDTO dto3 = new UpdateCourseContentInDTO(0L, maxTitle, "Description", "", 50.0f, true);
        Set<ConstraintViolation<UpdateCourseContentInDTO>> violations3 = validator.validate(dto3);
        assertTrue(violations3.isEmpty());

        // Test with maximum description length (1000 characters)
        String maxDesc = new String(new char[1000]).replace('\0', 'D');
        UpdateCourseContentInDTO dto4 = new UpdateCourseContentInDTO(0L, "Title", maxDesc, "", 50.0f, true);
        Set<ConstraintViolation<UpdateCourseContentInDTO>> violations4 = validator.validate(dto4);
        assertTrue(violations4.isEmpty());
    }
}