package com.nt.course_service_lms.dtoTest.inDTOTest;

import com.nt.course_service_lms.dto.inDTO.QuizUpdateInDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuizUpdateInDTOTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenAllFieldsValid_thenNoViolations() {
        QuizUpdateInDTO dto = new QuizUpdateInDTO(
                "Sample Title",
                "This is a sample quiz description.",
                60,
                3,
                new BigDecimal("75.50"),
                true,
                false,
                true,
                15  // questionsToShow
        );

        Set<ConstraintViolation<QuizUpdateInDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenOptionalFieldsAreNull_thenNoViolations() {
        QuizUpdateInDTO dto = new QuizUpdateInDTO();
        Set<ConstraintViolation<QuizUpdateInDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenTitleTooLong_thenViolation() {
        QuizUpdateInDTO dto = new QuizUpdateInDTO();
        String longTitle = new String(new char[256]).replace('\0', 'A');
        dto.setTitle(longTitle);
        Set<ConstraintViolation<QuizUpdateInDTO>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("title")));
    }

    @Test
    void whenDescriptionTooLong_thenViolation() {
        QuizUpdateInDTO dto = new QuizUpdateInDTO();
        dto.setDescription(new String(new char[1001]).replace('\0', 'D'));
        Set<ConstraintViolation<QuizUpdateInDTO>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("description")));
    }

    @Test
    void whenTimeLimitOutOfRange_thenViolation() {
        QuizUpdateInDTO dto = new QuizUpdateInDTO();
        dto.setTimeLimit(0); // less than minimum
        Set<ConstraintViolation<QuizUpdateInDTO>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("timeLimit")));

        dto.setTimeLimit(601); // more than maximum
        violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("timeLimit")));
    }

    @Test
    void whenAttemptsAllowedOutOfRange_thenViolation() {
        QuizUpdateInDTO dto = new QuizUpdateInDTO();
        dto.setAttemptsAllowed(0); // less than min
        Set<ConstraintViolation<QuizUpdateInDTO>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("attemptsAllowed")));

        dto.setAttemptsAllowed(11); // more than max
        violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("attemptsAllowed")));
    }

    @Test
    void whenPassingScoreInvalid_thenViolation() {
        QuizUpdateInDTO dto = new QuizUpdateInDTO();
        dto.setPassingScore(new BigDecimal("-1.00")); // less than 0
        Set<ConstraintViolation<QuizUpdateInDTO>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("passingScore")));

        dto.setPassingScore(new BigDecimal("12345.123")); // too many digits
        violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("passingScore")));
    }

    @Test
    void whenQuestionsToShowInvalid_thenViolation() {
        QuizUpdateInDTO dto = new QuizUpdateInDTO();
        dto.setQuestionsToShow(0); // less than minimum
        Set<ConstraintViolation<QuizUpdateInDTO>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("questionsToShow")));

        dto.setQuestionsToShow(101); // more than maximum
        violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("questionsToShow")));
    }

    @Test
    void testBuilderCreatesCorrectObject() {
        QuizUpdateInDTO dto = QuizUpdateInDTO.builder()
                .title("Builder Title")
                .description("Builder Desc")
                .timeLimit(120)
                .attemptsAllowed(3)
                .passingScore(new BigDecimal("60.00"))
                .randomizeQuestions(true)
                .showResults(false)
                .isActive(true)
                .questionsToShow(25)
                .build();

        assertEquals("Builder Title", dto.getTitle());
        assertEquals("Builder Desc", dto.getDescription());
        assertEquals(120, dto.getTimeLimit());
        assertEquals(3, dto.getAttemptsAllowed());
        assertEquals(new BigDecimal("60.00"), dto.getPassingScore());
        assertTrue(dto.getRandomizeQuestions());
        assertFalse(dto.getShowResults());
        assertTrue(dto.getIsActive());
        assertEquals(25, dto.getQuestionsToShow());
    }

    @Test
    void testEqualsAndHashCode() {
        QuizUpdateInDTO dto1 = new QuizUpdateInDTO(
                "Title", "Desc", 30, 2, new BigDecimal("50.00"), true, false, true, 10
        );
        QuizUpdateInDTO dto2 = new QuizUpdateInDTO(
                "Title", "Desc", 30, 2, new BigDecimal("50.00"), true, false, true, 10
        );

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testNotEquals() {
        QuizUpdateInDTO dto1 = new QuizUpdateInDTO(
                "T1", "D1", 10, 1, BigDecimal.ZERO, true, true, false, 5
        );
        QuizUpdateInDTO dto2 = new QuizUpdateInDTO(
                "T2", "D2", 20, 2, BigDecimal.TEN, false, false, true, 15
        );

        assertNotEquals(dto1, dto2);
        assertNotEquals(dto1, null);
        assertNotEquals(dto1, "some string");
    }

    @Test
    void testNotEqualsWithDifferentQuestionsToShow() {
        QuizUpdateInDTO dto1 = new QuizUpdateInDTO(
                "Title", "Desc", 30, 2, new BigDecimal("50.00"), true, false, true, 10
        );
        QuizUpdateInDTO dto2 = new QuizUpdateInDTO(
                "Title", "Desc", 30, 2, new BigDecimal("50.00"), true, false, true, 20
        );

        assertNotEquals(dto1, dto2);
        assertNotEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testDefaultConstructorAndSetters() {
        QuizUpdateInDTO dto = new QuizUpdateInDTO();
        dto.setTitle("New Title");
        dto.setDescription("New Desc");
        dto.setTimeLimit(45);
        dto.setAttemptsAllowed(5);
        dto.setPassingScore(new BigDecimal("85.00"));
        dto.setRandomizeQuestions(false);
        dto.setShowResults(true);
        dto.setIsActive(true);
        dto.setQuestionsToShow(30);

        assertEquals("New Title", dto.getTitle());
        assertEquals("New Desc", dto.getDescription());
        assertEquals(45, dto.getTimeLimit());
        assertEquals(5, dto.getAttemptsAllowed());
        assertEquals(new BigDecimal("85.00"), dto.getPassingScore());
        assertFalse(dto.getRandomizeQuestions());
        assertTrue(dto.getShowResults());
        assertTrue(dto.getIsActive());
        assertEquals(30, dto.getQuestionsToShow());
    }

    @Test
    void testQuestionsToShowValidValues() {
        QuizUpdateInDTO dto = new QuizUpdateInDTO();

        // Test valid minimum value
        dto.setQuestionsToShow(1);
        Set<ConstraintViolation<QuizUpdateInDTO>> violations = validator.validate(dto);
        assertFalse(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("questionsToShow")));

        // Test valid maximum value
        dto.setQuestionsToShow(100);
        violations = validator.validate(dto);
        assertFalse(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("questionsToShow")));

        // Test null value (should be valid as it's optional)
        dto.setQuestionsToShow(null);
        violations = validator.validate(dto);
        assertFalse(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("questionsToShow")));
    }
}