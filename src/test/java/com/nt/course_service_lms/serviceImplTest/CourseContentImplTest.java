//package com.nt.course_service_lms.serviceImplTest;
//
//import com.nt.course_service_lms.dto.inDTO.CourseContentInDTO;
//import com.nt.course_service_lms.dto.inDTO.UpdateCourseContentInDTO;
//import com.nt.course_service_lms.dto.outDTO.CourseContentOutDTO;
//import com.nt.course_service_lms.entity.CourseContent;
//import com.nt.course_service_lms.exception.ResourceAlreadyExistsException;
//import com.nt.course_service_lms.exception.ResourceNotFoundException;
//import com.nt.course_service_lms.repository.CourseContentRepository;
//import com.nt.course_service_lms.repository.CourseRepository;
//import com.nt.course_service_lms.service.serviceImpl.CourseContentImpl;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.time.LocalDateTime;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.any;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//class CourseContentImplTest {
//
//    @Mock
//    private CourseContentRepository courseContentRepository;
//
//    @Mock
//    private CourseRepository courseRepository;
//    private AutoCloseable closeable;
//    @InjectMocks
//    private CourseContentImpl courseContentService;
//
//    private CourseContent courseContent;
//    private CourseContentInDTO courseContentInDTO;
//    private UpdateCourseContentInDTO updateCourseContentInDTO;
//
//    @BeforeEach
//    void setUp() {
//        closeable = MockitoAnnotations.openMocks(this);
//        courseContent = CourseContent.builder()
//                .courseContentId(1L)
//                .courseId(101L)
//                .title("Intro")
//                .description("Introduction")
//                .resourceLink("http://link")
//                .isActive(true)
//                .createdAt(LocalDateTime.now())
//                .updatedAt(LocalDateTime.now())
//                .build();
//
//        courseContentInDTO = new CourseContentInDTO(101L, "Intro", "Introduction", "http://link", true);
//
//        updateCourseContentInDTO = new UpdateCourseContentInDTO(101L, "Intro Updated", "Updated Description", "http://link-updated", false);
//    }
//
//    @AfterEach
//    void tearDown() throws Exception {
//        closeable.close();
//    }
//
//    @Test
//    void testCreateCourseContent_success() {
//        when(courseContentRepository.findByTitleIgnoreCaseAndCourseId("Intro", 101L)).thenReturn(Optional.empty());
//        when(courseRepository.existsById(101L)).thenReturn(true);
//        when(courseContentRepository.save(any(CourseContent.class))).thenReturn(courseContent);
//
//        CourseContentOutDTO result = courseContentService.createCourseContent(courseContentInDTO);
//
//        assertNotNull(result);
//        verify(courseContentRepository).save(any(CourseContent.class));
//    }
//
//    @Test
//    void testCreateCourseContent_alreadyExists() {
//        when(courseContentRepository.findByTitleIgnoreCaseAndCourseId("Intro", 101L)).thenReturn(Optional.of(courseContent));
//
//        assertThrows(ResourceAlreadyExistsException.class, () -> courseContentService.createCourseContent(courseContentInDTO));
//    }
//
//    @Test
//    void testCreateCourseContent_courseNotFound() {
//        when(courseContentRepository.findByTitleIgnoreCaseAndCourseId("Intro", 101L)).thenReturn(Optional.empty());
//        when(courseRepository.existsById(101L)).thenReturn(false);
//
//        assertThrows(ResourceNotFoundException.class, () -> courseContentService.createCourseContent(courseContentInDTO));
//    }
//
//    @Test
//    void testGetAllCourseContents_success() {
//        when(courseContentRepository.findAll()).thenReturn(Arrays.asList(courseContent));
//
//        List<CourseContentOutDTO> result = courseContentService.getAllCourseContents();
//
//        assertFalse(result.isEmpty());
//        assertEquals(1, result.size());
//    }
//
//    @Test
//    void testGetAllCourseContents_empty() {
//        when(courseContentRepository.findAll()).thenReturn(Collections.emptyList());
//
//        assertThrows(ResourceNotFoundException.class, () -> courseContentService.getAllCourseContents());
//    }
//
//    @Test
//    void testGetCourseContentById_success() {
//        when(courseContentRepository.findById(1L)).thenReturn(Optional.of(courseContent));
//
//        CourseContentOutDTO result = courseContentService.getCourseContentById(1L);
//
//        assertNotNull(result);
//    }
//
//    @Test
//    void testGetCourseContentById_notFound() {
//        when(courseContentRepository.findById(1L)).thenReturn(Optional.empty());
//
//        assertThrows(ResourceNotFoundException.class, () -> courseContentService.getCourseContentById(1L));
//    }
//
//    @Test
//    void testDeleteCourseContent_success() {
//        when(courseContentRepository.findById(1L)).thenReturn(Optional.of(courseContent));
//
//        String result = courseContentService.deleteCourseContent(1L);
//
//        assertEquals("Course Content Deleted Successfully", result);
//        verify(courseContentRepository).delete(courseContent);
//    }
//
//    @Test
//    void testDeleteCourseContent_notFound() {
//        when(courseContentRepository.findById(1L)).thenReturn(Optional.empty());
//
//        assertThrows(ResourceNotFoundException.class, () -> courseContentService.deleteCourseContent(1L));
//    }
//
//    @Test
//    void testUpdateCourseContent_success() {
//        when(courseContentRepository.findById(1L)).thenReturn(Optional.of(courseContent));
//        when(courseRepository.existsById(101L)).thenReturn(true);
//        when(courseContentRepository.findByTitleIgnoreCaseAndCourseId("Intro Updated", 101L)).thenReturn(Optional.empty());
//        when(courseContentRepository.save(any(CourseContent.class))).thenReturn(courseContent);
//
//        CourseContentOutDTO result = courseContentService.updateCourseContent(1L, updateCourseContentInDTO);
//
//        assertNotNull(result);
//    }
//
//    @Test
//    void testUpdateCourseContent_notFound() {
//        when(courseContentRepository.findById(1L)).thenReturn(Optional.empty());
//
//        assertThrows(ResourceNotFoundException.class, () -> courseContentService.updateCourseContent(1L, updateCourseContentInDTO));
//    }
//
//    @Test
//    void testUpdateCourseContent_duplicate() {
//        CourseContent duplicate = CourseContent.builder().courseContentId(2L).courseId(101L).title("Intro Updated").build();
//
//        when(courseContentRepository.findById(1L)).thenReturn(Optional.of(courseContent));
//        when(courseRepository.existsById(101L)).thenReturn(true);
//        when(courseContentRepository.findByTitleIgnoreCaseAndCourseId("Intro Updated", 101L)).thenReturn(Optional.of(duplicate));
//
//        assertThrows(ResourceAlreadyExistsException.class, () -> courseContentService.updateCourseContent(1L, updateCourseContentInDTO));
//    }
//
//    @Test
//    void testGetAllCourseContentByCourseId_success() {
//        when(courseRepository.existsById(101L)).thenReturn(true);
//        when(courseContentRepository.findByCourseId(101L)).thenReturn(Arrays.asList(courseContent));
//
//        List<CourseContentOutDTO> result = courseContentService.getAllCourseContentByCourseId(101L);
//
//        assertEquals(1, result.size());
//    }
//
//    @Test
//    void testGetAllCourseContentByCourseId_noContents() {
//        when(courseRepository.existsById(101L)).thenReturn(true);
//        when(courseContentRepository.findByCourseId(101L)).thenReturn(Collections.emptyList());
//
//        assertThrows(ResourceNotFoundException.class, () -> courseContentService.getAllCourseContentByCourseId(101L));
//    }
//
//    @Test
//    void testGetAllCourseContentByCourseId_courseNotFound() {
//        when(courseRepository.existsById(101L)).thenReturn(false);
//
//        assertThrows(ResourceNotFoundException.class, () -> courseContentService.getAllCourseContentByCourseId(101L));
//    }
//}
//

package com.nt.course_service_lms.serviceImplTest;

import com.nt.course_service_lms.converters.CourseContentConverters;
import com.nt.course_service_lms.dto.inDTO.CourseContentInDTO;
import com.nt.course_service_lms.dto.inDTO.CourseContentUrlInDTO;
import com.nt.course_service_lms.dto.inDTO.UpdateCourseContentInDTO;
import com.nt.course_service_lms.dto.outDTO.CourseContentOutDTO;
import com.nt.course_service_lms.entity.CourseContent;
import com.nt.course_service_lms.exception.ResourceAlreadyExistsException;
import com.nt.course_service_lms.exception.ResourceNotFoundException;
import com.nt.course_service_lms.repository.CourseContentRepository;
import com.nt.course_service_lms.repository.CourseRepository;
import com.nt.course_service_lms.repository.UserProgressRepository;
import com.nt.course_service_lms.service.serviceImpl.CourseContentImpl;
import com.nt.course_service_lms.service.serviceImpl.S3FileService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CourseContentImplTest {

    @Mock
    private CourseContentRepository courseContentRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private S3FileService s3FileService;

    @Mock
    private UserProgressRepository userProgressRepository;

    @InjectMocks
    private CourseContentImpl courseContentService;

    private AutoCloseable closeable;
    private MockedStatic<CourseContentConverters> convertersMock;

    // Test data
    private CourseContentInDTO courseContentInDTO;
    private CourseContentUrlInDTO courseContentUrlInDTO;
    private UpdateCourseContentInDTO updateCourseContentInDTO;
    private CourseContent courseContent;
    private CourseContent savedCourseContent;
    private CourseContentOutDTO courseContentOutDTO;
    private MockMultipartFile testFile;

    private static final Long COURSE_ID = 1L;
    private static final Long CONTENT_ID = 100L;
    private static final String TITLE = "Test Course Content";
    private static final String DESCRIPTION = "Test Description";
    private static final String CONTENT_TYPE = "VIDEO";
    private static final String S3_FILE_NAME = "test-file.mp4";
    private static final String RESOURCE_LINK = "https://example.com/resource";

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        convertersMock = mockStatic(CourseContentConverters.class);
        setupTestData();
    }

    @AfterEach
    void tearDown() throws Exception {
        convertersMock.close();
        closeable.close();
    }

    private void setupTestData() {
        // Create test file
        testFile = new MockMultipartFile(
                "file",
                "test-video.mp4",
                "video/mp4",
                "test content".getBytes()
        );

        // Create DTOs
        courseContentInDTO = CourseContentInDTO.builder()
                .courseId(COURSE_ID)
                .title(TITLE)
                .description(DESCRIPTION)
                .contentType(CONTENT_TYPE)
                .file(testFile)
                .isActive(true)
                .build();

        courseContentUrlInDTO = CourseContentUrlInDTO.builder()
                .courseId(COURSE_ID)
                .title(TITLE)
                .description(DESCRIPTION)
                .youtubeUrl(RESOURCE_LINK)
                .isActive(true)
                .build();

        updateCourseContentInDTO = UpdateCourseContentInDTO.builder()
                .courseId(COURSE_ID)
                .title("Updated Title")
                .description("Updated Description")
                .resourceLink("https://updated.com/resource")
                .isActive(true)
                .build();

        // Create entities
        courseContent = new CourseContent();
        courseContent.setCourseId(COURSE_ID);
        courseContent.setTitle(TITLE);
        courseContent.setDescription(DESCRIPTION);
        courseContent.setContentType(CONTENT_TYPE);
        courseContent.setActive(true);

        savedCourseContent = new CourseContent();
        savedCourseContent.setCourseContentId(CONTENT_ID);
        savedCourseContent.setCourseId(COURSE_ID);
        savedCourseContent.setTitle(TITLE);
        savedCourseContent.setDescription(DESCRIPTION);
        savedCourseContent.setContentType(CONTENT_TYPE);
        savedCourseContent.setResourceLink(S3_FILE_NAME);
        savedCourseContent.setActive(true);
        savedCourseContent.setCreatedAt(LocalDateTime.now());
        savedCourseContent.setUpdatedAt(LocalDateTime.now());

        // Create output DTO
        courseContentOutDTO = CourseContentOutDTO.builder()
                .courseContentId(CONTENT_ID)
                .courseId(COURSE_ID)
                .title(TITLE)
                .description(DESCRIPTION)
                .contentType(CONTENT_TYPE)
                .resourceLink(S3_FILE_NAME)
                .isActive(true)
                .build();
    }

    // ==================== CREATE COURSE CONTENT WITH FILE TESTS ====================

    @Test
    void testCreateCourseContent_withFile_success() throws IOException {
        // Arrange
        when(courseContentRepository.findByTitleIgnoreCaseAndCourseId(TITLE, COURSE_ID))
                .thenReturn(Optional.empty());
        when(courseRepository.existsById(COURSE_ID)).thenReturn(true);
        when(s3FileService.uploadFile(testFile, CONTENT_TYPE)).thenReturn(S3_FILE_NAME);
        when(CourseContentConverters.courseContentInDtoToEntity(courseContentInDTO))
                .thenReturn(courseContent);
        when(courseContentRepository.save(any(CourseContent.class))).thenReturn(savedCourseContent);
        when(CourseContentConverters.entityToOutDto(savedCourseContent)).thenReturn(courseContentOutDTO);

        // Act
        CourseContentOutDTO result = courseContentService.createCourseContent(courseContentInDTO);

        // Assert
        assertNotNull(result);
        assertEquals(CONTENT_ID, result.getCourseContentId());
        assertEquals(S3_FILE_NAME, result.getResourceLink());

        verify(courseContentRepository).findByTitleIgnoreCaseAndCourseId(TITLE, COURSE_ID);
        verify(courseRepository).existsById(COURSE_ID);
        verify(s3FileService).uploadFile(testFile, CONTENT_TYPE);
        verify(courseContentRepository).save(any(CourseContent.class));
    }

    @Test
    void testCreateCourseContent_withFile_duplicateTitle() throws IOException {
        // Arrange
        when(courseContentRepository.findByTitleIgnoreCaseAndCourseId(TITLE, COURSE_ID))
                .thenReturn(Optional.of(courseContent));

        // Act & Assert
        ResourceAlreadyExistsException exception = assertThrows(
                ResourceAlreadyExistsException.class,
                () -> courseContentService.createCourseContent(courseContentInDTO)
        );

        verify(courseContentRepository).findByTitleIgnoreCaseAndCourseId(TITLE, COURSE_ID);
        verify(courseRepository, never()).existsById(anyLong());
        verify(s3FileService, never()).uploadFile(any(), any());
    }

    @Test
    void testCreateCourseContent_withFile_courseNotFound() throws IOException {
        // Arrange
        when(courseContentRepository.findByTitleIgnoreCaseAndCourseId(TITLE, COURSE_ID))
                .thenReturn(Optional.empty());
        when(courseRepository.existsById(COURSE_ID)).thenReturn(false);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> courseContentService.createCourseContent(courseContentInDTO)
        );

        verify(courseContentRepository).findByTitleIgnoreCaseAndCourseId(TITLE, COURSE_ID);
        verify(courseRepository).existsById(COURSE_ID);
        verify(s3FileService, never()).uploadFile(any(), any());
    }

    @Test
    void testCreateCourseContent_withFile_s3UploadFails() throws IOException {
        // Arrange
        when(courseContentRepository.findByTitleIgnoreCaseAndCourseId(TITLE, COURSE_ID))
                .thenReturn(Optional.empty());
        when(courseRepository.existsById(COURSE_ID)).thenReturn(true);
        when(s3FileService.uploadFile(testFile, CONTENT_TYPE))
                .thenThrow(new IOException("S3 upload failed"));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> courseContentService.createCourseContent(courseContentInDTO)
        );

        verify(s3FileService).uploadFile(testFile, CONTENT_TYPE);
        verify(courseContentRepository, never()).save(any());
    }

    // ==================== CREATE COURSE CONTENT WITH URL TESTS ====================

    @Test
    void testCreateCourseContent_withUrl_success() throws IOException {
        // Arrange
        when(courseContentRepository.findByTitleIgnoreCaseAndCourseId(TITLE, COURSE_ID))
                .thenReturn(Optional.empty());
        when(courseRepository.existsById(COURSE_ID)).thenReturn(true);
        when(CourseContentConverters.courseContentInDtoToEntity(courseContentUrlInDTO))
                .thenReturn(courseContent);
        when(courseContentRepository.save(any(CourseContent.class))).thenReturn(savedCourseContent);
        when(CourseContentConverters.entityToOutDto(savedCourseContent)).thenReturn(courseContentOutDTO);

        // Act
        CourseContentOutDTO result = courseContentService.createCourseContent(courseContentUrlInDTO);

        // Assert
        assertNotNull(result);
        assertEquals(CONTENT_ID, result.getCourseContentId());

        verify(courseContentRepository).findByTitleIgnoreCaseAndCourseId(TITLE, COURSE_ID);
        verify(courseRepository).existsById(COURSE_ID);
        verify(courseContentRepository).save(any(CourseContent.class));
        verify(s3FileService, never()).uploadFile(any(), any()); // No S3 call for URL-based content
    }

    @Test
    void testCreateCourseContent_withUrl_duplicateTitle() throws IOException {
        // Arrange
        when(courseContentRepository.findByTitleIgnoreCaseAndCourseId(TITLE, COURSE_ID))
                .thenReturn(Optional.of(courseContent));

        // Act & Assert
        ResourceAlreadyExistsException exception = assertThrows(
                ResourceAlreadyExistsException.class,
                () -> courseContentService.createCourseContent(courseContentUrlInDTO)
        );

        verify(courseContentRepository).findByTitleIgnoreCaseAndCourseId(TITLE, COURSE_ID);
        verify(s3FileService, never()).uploadFile(any(), any());
    }

    // ==================== GET ALL COURSE CONTENTS TESTS ====================

    @Test
    void testGetAllCourseContents_success() {
        // Arrange
        List<CourseContent> courseContents = Arrays.asList(courseContent, savedCourseContent);
        List<CourseContentOutDTO> expectedDTOs = Arrays.asList(courseContentOutDTO);

        when(courseContentRepository.findAll()).thenReturn(courseContents);
        when(CourseContentConverters.entityListToOutDtoList(courseContents)).thenReturn(expectedDTOs);

        // Act
        List<CourseContentOutDTO> result = courseContentService.getAllCourseContents();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(courseContentRepository).findAll();
    }

    @Test
    void testGetAllCourseContents_noneFound() {
        // Arrange
        when(courseContentRepository.findAll()).thenReturn(Collections.emptyList());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> courseContentService.getAllCourseContents()
        );

        verify(courseContentRepository).findAll();
    }

    // ==================== GET COURSE CONTENT BY ID TESTS ====================

    @Test
    void testGetCourseContentById_success() {
        // Arrange
        when(courseContentRepository.findById(CONTENT_ID)).thenReturn(Optional.of(savedCourseContent));
        when(CourseContentConverters.entityToOutDto(savedCourseContent)).thenReturn(courseContentOutDTO);

        // Act
        CourseContentOutDTO result = courseContentService.getCourseContentById(CONTENT_ID);

        // Assert
        assertNotNull(result);
        assertEquals(CONTENT_ID, result.getCourseContentId());
        verify(courseContentRepository).findById(CONTENT_ID);
    }

    @Test
    void testGetCourseContentById_notFound() {
        // Arrange
        when(courseContentRepository.findById(CONTENT_ID)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> courseContentService.getCourseContentById(CONTENT_ID)
        );

        verify(courseContentRepository).findById(CONTENT_ID);
    }

    // ==================== DELETE COURSE CONTENT TESTS ====================

    @Test
    void testDeleteCourseContent_success_fileDeletedFromS3() {
        // Arrange
        savedCourseContent.setResourceLink("test-file.mp4");
        when(courseContentRepository.findById(CONTENT_ID)).thenReturn(Optional.of(savedCourseContent));
        when(s3FileService.deleteFile(CONTENT_TYPE + "/" + savedCourseContent.getResourceLink()))
                .thenReturn(true);

        // Act
        String result = courseContentService.deleteCourseContent(CONTENT_ID);

        // Assert
        assertNotNull(result);
        verify(courseContentRepository).findById(CONTENT_ID);
        verify(s3FileService).deleteFile(CONTENT_TYPE + "/" + savedCourseContent.getResourceLink());
        verify(userProgressRepository).deleteByContentId(CONTENT_ID);
        verify(courseContentRepository).delete(savedCourseContent);
    }

    @Test
    void testDeleteCourseContent_success_fileNotInS3() {
        // Arrange
        savedCourseContent.setResourceLink("test-file.mp4");
        when(courseContentRepository.findById(CONTENT_ID)).thenReturn(Optional.of(savedCourseContent));
        when(s3FileService.deleteFile(CONTENT_TYPE + "/" + savedCourseContent.getResourceLink()))
                .thenReturn(false);

        // Act
        String result = courseContentService.deleteCourseContent(CONTENT_ID);

        // Assert
        assertNotNull(result);
        verify(courseContentRepository).findById(CONTENT_ID);
        verify(s3FileService).deleteFile(CONTENT_TYPE + "/" + savedCourseContent.getResourceLink());
        verify(userProgressRepository).deleteByContentId(CONTENT_ID);
        verify(courseContentRepository).delete(savedCourseContent);
    }

    @Test
    void testDeleteCourseContent_contentNotFound() {
        // Arrange
        when(courseContentRepository.findById(CONTENT_ID)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> courseContentService.deleteCourseContent(CONTENT_ID)
        );

        verify(courseContentRepository).findById(CONTENT_ID);
        verify(s3FileService, never()).deleteFile(any());
        verify(courseContentRepository, never()).delete(any());
    }

    @Test
    void testDeleteCourseContent_s3DeleteThrowsException() {
        // Arrange
        savedCourseContent.setResourceLink("test-file.mp4");
        when(courseContentRepository.findById(CONTENT_ID)).thenReturn(Optional.of(savedCourseContent));
        when(s3FileService.deleteFile(anyString()))
                .thenThrow(new RuntimeException("S3 delete failed"));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> courseContentService.deleteCourseContent(CONTENT_ID)
        );

        verify(s3FileService).deleteFile(CONTENT_TYPE + "/" + savedCourseContent.getResourceLink());
        verify(courseContentRepository, never()).delete(any());
    }

    // ==================== UPDATE COURSE CONTENT TESTS ====================

    @Test
    void testUpdateCourseContent_success() {
        // Arrange
        CourseContent existingContent = new CourseContent();
        existingContent.setCourseContentId(CONTENT_ID);
        existingContent.setCourseId(COURSE_ID);
        existingContent.setTitle(TITLE);
        existingContent.setDescription(DESCRIPTION);

        when(courseContentRepository.findById(CONTENT_ID)).thenReturn(Optional.of(existingContent));
        when(courseRepository.existsById(updateCourseContentInDTO.getCourseId())).thenReturn(true);
        when(courseContentRepository.findByTitleIgnoreCaseAndCourseId(
                updateCourseContentInDTO.getTitle(), updateCourseContentInDTO.getCourseId()))
                .thenReturn(Optional.empty());
        when(courseContentRepository.save(any(CourseContent.class))).thenReturn(savedCourseContent);
        when(CourseContentConverters.entityToOutDto(savedCourseContent)).thenReturn(courseContentOutDTO);

        // Act
        CourseContentOutDTO result = courseContentService.updateCourseContent(CONTENT_ID, updateCourseContentInDTO);

        // Assert
        assertNotNull(result);
        verify(courseContentRepository).findById(CONTENT_ID);
        verify(courseRepository).existsById(updateCourseContentInDTO.getCourseId());
        verify(courseContentRepository).save(any(CourseContent.class));
    }

    @Test
    void testUpdateCourseContent_contentNotFound() {
        // Arrange
        when(courseContentRepository.findById(CONTENT_ID)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> courseContentService.updateCourseContent(CONTENT_ID, updateCourseContentInDTO)
        );

        verify(courseContentRepository).findById(CONTENT_ID);
        verify(courseContentRepository, never()).save(any());
    }

    @Test
    void testUpdateCourseContent_courseNotFound() {
        // Arrange
        when(courseContentRepository.findById(CONTENT_ID)).thenReturn(Optional.of(savedCourseContent));
        when(courseRepository.existsById(updateCourseContentInDTO.getCourseId())).thenReturn(false);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> courseContentService.updateCourseContent(CONTENT_ID, updateCourseContentInDTO)
        );

        verify(courseRepository).existsById(updateCourseContentInDTO.getCourseId());
        verify(courseContentRepository, never()).save(any());
    }

    @Test
    void testUpdateCourseContent_duplicateTitle() {
        // Arrange
        CourseContent existingContent = new CourseContent();
        existingContent.setCourseContentId(CONTENT_ID);
        existingContent.setCourseId(2L); // Different course ID
        existingContent.setTitle("Different Title");

        CourseContent duplicateContent = new CourseContent();
        duplicateContent.setCourseContentId(999L); // Different content ID

        when(courseContentRepository.findById(CONTENT_ID)).thenReturn(Optional.of(existingContent));
        when(courseRepository.existsById(updateCourseContentInDTO.getCourseId())).thenReturn(true);
        when(courseContentRepository.findByTitleIgnoreCaseAndCourseId(
                updateCourseContentInDTO.getTitle(), updateCourseContentInDTO.getCourseId()))
                .thenReturn(Optional.of(duplicateContent));

        // Act & Assert
        ResourceAlreadyExistsException exception = assertThrows(
                ResourceAlreadyExistsException.class,
                () -> courseContentService.updateCourseContent(CONTENT_ID, updateCourseContentInDTO)
        );

        verify(courseContentRepository, never()).save(any());
    }

    // ==================== GET ALL COURSE CONTENT BY COURSE ID TESTS ====================

    @Test
    void testGetAllCourseContentByCourseId_success() {
        // Arrange
        List<CourseContent> courseContents = Arrays.asList(courseContent, savedCourseContent);
        List<CourseContentOutDTO> expectedDTOs = Arrays.asList(courseContentOutDTO);

        when(courseRepository.existsById(COURSE_ID)).thenReturn(true);
        when(courseContentRepository.findByCourseId(COURSE_ID)).thenReturn(courseContents);
        when(CourseContentConverters.entityListToOutDtoList(courseContents)).thenReturn(expectedDTOs);

        // Act
        List<CourseContentOutDTO> result = courseContentService.getAllCourseContentByCourseId(COURSE_ID);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(courseRepository).existsById(COURSE_ID);
        verify(courseContentRepository).findByCourseId(COURSE_ID);
    }

    @Test
    void testGetAllCourseContentByCourseId_courseNotFound() {
        // Arrange
        when(courseRepository.existsById(COURSE_ID)).thenReturn(false);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> courseContentService.getAllCourseContentByCourseId(COURSE_ID)
        );

        verify(courseRepository).existsById(COURSE_ID);
        verify(courseContentRepository, never()).findByCourseId(anyLong());
    }

    @Test
    void testGetAllCourseContentByCourseId_noContentFound() {
        // Arrange
        when(courseRepository.existsById(COURSE_ID)).thenReturn(true);
        when(courseContentRepository.findByCourseId(COURSE_ID)).thenReturn(Collections.emptyList());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> courseContentService.getAllCourseContentByCourseId(COURSE_ID)
        );

        verify(courseRepository).existsById(COURSE_ID);
        verify(courseContentRepository).findByCourseId(COURSE_ID);
    }

    // ==================== EDGE CASES AND ERROR HANDLING TESTS ====================

    @Test
    void testCreateCourseContent_unexpectedException() throws IOException {
        // Arrange
        when(courseContentRepository.findByTitleIgnoreCaseAndCourseId(TITLE, COURSE_ID))
                .thenReturn(Optional.empty());
        when(courseRepository.existsById(COURSE_ID)).thenReturn(true);
        when(s3FileService.uploadFile(testFile, CONTENT_TYPE)).thenReturn(S3_FILE_NAME);
        when(CourseContentConverters.courseContentInDtoToEntity(courseContentInDTO))
                .thenReturn(courseContent);
        when(courseContentRepository.save(any(CourseContent.class)))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> courseContentService.createCourseContent(courseContentInDTO)
        );

        verify(s3FileService).uploadFile(testFile, CONTENT_TYPE);
        verify(courseContentRepository).save(any(CourseContent.class));
    }

    @Test
    void testGetCourseContentById_unexpectedException() {
        // Arrange
        when(courseContentRepository.findById(CONTENT_ID))
                .thenThrow(new RuntimeException("Database connection failed"));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> courseContentService.getCourseContentById(CONTENT_ID)
        );

        verify(courseContentRepository).findById(CONTENT_ID);
    }

    @Test
    void testUpdateCourseContent_sameTitle_sameCourse_shouldNotCheckDuplicate() {
        // Arrange - Update with same title and course (no actual change)
        CourseContent existingContent = new CourseContent();
        existingContent.setCourseContentId(CONTENT_ID);
        existingContent.setCourseId(COURSE_ID);
        existingContent.setTitle("Same Title");

        UpdateCourseContentInDTO sameDataDTO = UpdateCourseContentInDTO.builder()
                .courseId(COURSE_ID)
                .title("Same Title") // Same title
                .description("Updated Description")
                .resourceLink("https://updated.com/resource")
                .isActive(true)
                .build();

        when(courseContentRepository.findById(CONTENT_ID)).thenReturn(Optional.of(existingContent));
        when(courseRepository.existsById(COURSE_ID)).thenReturn(true);
        when(courseContentRepository.save(any(CourseContent.class))).thenReturn(savedCourseContent);
        when(CourseContentConverters.entityToOutDto(savedCourseContent)).thenReturn(courseContentOutDTO);

        // Act
        CourseContentOutDTO result = courseContentService.updateCourseContent(CONTENT_ID, sameDataDTO);

        // Assert
        assertNotNull(result);
        verify(courseContentRepository).findById(CONTENT_ID);
        verify(courseRepository).existsById(COURSE_ID);
        // Should not check for duplicate since title and course didn't change
        verify(courseContentRepository, never()).findByTitleIgnoreCaseAndCourseId(anyString(), anyLong());
        verify(courseContentRepository).save(any(CourseContent.class));
    }

    @Test
    void testDeleteCourseContent_withNullResourceLink() {
        // Arrange
        CourseContent contentWithNullLink = new CourseContent();
        contentWithNullLink.setCourseContentId(CONTENT_ID);
        contentWithNullLink.setContentType(CONTENT_TYPE);
        contentWithNullLink.setResourceLink(null); // Null resource link

        when(courseContentRepository.findById(CONTENT_ID)).thenReturn(Optional.of(contentWithNullLink));
        when(s3FileService.deleteFile(CONTENT_TYPE + "/" + null)).thenReturn(false);

        // Act
        String result = courseContentService.deleteCourseContent(CONTENT_ID);

        // Assert
        assertNotNull(result);
        verify(s3FileService).deleteFile(CONTENT_TYPE + "/" + null);
        verify(userProgressRepository).deleteByContentId(CONTENT_ID);
        verify(courseContentRepository).delete(contentWithNullLink);
    }
}
