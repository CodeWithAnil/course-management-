//package com.nt.course_service_lms.serviceImplTest;
//
//import com.nt.course_service_lms.exception.ResourceNotFoundException;
//import com.nt.course_service_lms.service.serviceImpl.S3FileService;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.test.util.ReflectionTestUtils;
//import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
//import software.amazon.awssdk.core.sync.RequestBody;
//import software.amazon.awssdk.services.s3.S3Client;
//import software.amazon.awssdk.services.s3.model.*;
//
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.*;
//
//class S3FileServiceTest {
//
//    @Mock
//    private S3Client s3Client;
//
//    @InjectMocks
//    private S3FileService s3FileService;
//
//    private AutoCloseable closeable;
//
//    private MockMultipartFile smallFile;
//    private MockMultipartFile largeFile;
//    private MockMultipartFile emptyFile;
//    private final String bucketName = "test-bucket";
//    private final String testFolder = "PDF";
//
//    @BeforeEach
//    void setUp() {
//        closeable = MockitoAnnotations.openMocks(this);
//
//        // Set bucket name using ReflectionTestUtils
//        ReflectionTestUtils.setField(s3FileService, "bucketName", bucketName);
//
//        // Create test files
//        smallFile = new MockMultipartFile(
//                "file",
//                "small-test.pdf",
//                "application/pdf",
//                "small file content".getBytes()
//        );
//
//        // Create a large file (> 8MB for multipart upload testing)
//        byte[] largeContent = new byte[10 * 1024 * 1024]; // 10MB
//        for (int i = 0; i < largeContent.length; i++) {
//            largeContent[i] = (byte) (i % 256);
//        }
//        largeFile = new MockMultipartFile(
//                "file",
//                "large-test.pdf",
//                "application/pdf",
//                largeContent
//        );
//
//        emptyFile = new MockMultipartFile(
//                "file",
//                "empty-test.pdf",
//                "application/pdf",
//                new byte[0]
//        );
//    }
//
//    @AfterEach
//    void tearDown() throws Exception {
//        closeable.close();
//    }
//
//    // ==================== UPLOAD FILE TESTS (SMALL FILES) ====================
//
//    @Test
//    void testUploadFile_smallFile_success() throws IOException {
//        // Arrange
//        PutObjectResponse putResponse = PutObjectResponse.builder()
//                .eTag("test-etag")
//                .build();
//        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
//                .thenReturn(putResponse);
//
//        // Act
//        String result = s3FileService.uploadFile(smallFile, testFolder);
//
//        // Assert
//        assertNotNull(result);
//        assertTrue(result.contains("small-test.pdf"));
//        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
//        verify(s3Client, never()).createMultipartUpload(any(CreateMultipartUploadRequest.class));
//    }
//
//    @Test
//    void testUploadFile_smallFile_s3Exception() throws IOException {
//        // Arrange
//        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
//                .thenThrow(S3Exception.builder().message("S3 connection failed").build());
//
//        // Act & Assert - Based on the service implementation, it should wrap in IOException
//        IOException exception = assertThrows(IOException.class,
//                () -> s3FileService.uploadFile(smallFile, testFolder));
//
//        assertTrue(exception.getMessage().contains("Failed to upload small file"));
//        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
//    }
//
//    @Test
//    void testUploadFile_emptyFile_throwsException() {
//        // Act & Assert
//        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
//                () -> s3FileService.uploadFile(emptyFile, testFolder));
//
//        assertEquals("File not found", exception.getMessage());
//        verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
//    }
//
//    // ==================== UPLOAD FILE TESTS (LARGE FILES - MULTIPART) ====================
//
//    @Test
//    void testUploadFile_largeFile_success() throws IOException {
//        // Arrange
//        CreateMultipartUploadResponse createResponse = CreateMultipartUploadResponse.builder()
//                .uploadId("test-upload-id")
//                .build();
//        when(s3Client.createMultipartUpload(any(CreateMultipartUploadRequest.class)))
//                .thenReturn(createResponse);
//
//        UploadPartResponse uploadPartResponse = UploadPartResponse.builder()
//                .eTag("part-etag-1")
//                .build();
//        when(s3Client.uploadPart(any(UploadPartRequest.class), any(RequestBody.class)))
//                .thenReturn(uploadPartResponse);
//
//        CompleteMultipartUploadResponse completeResponse = CompleteMultipartUploadResponse.builder()
//                .location("s3://test-bucket/PDF/test-file")
//                .build();
//        when(s3Client.completeMultipartUpload(any(CompleteMultipartUploadRequest.class)))
//                .thenReturn(completeResponse);
//
//        // Act
//        String result = s3FileService.uploadFile(largeFile, testFolder);
//
//        // Assert
//        assertNotNull(result);
//        assertTrue(result.contains("large-test.pdf"));
//        verify(s3Client).createMultipartUpload(any(CreateMultipartUploadRequest.class));
//        verify(s3Client, atLeastOnce()).uploadPart(any(UploadPartRequest.class), any(RequestBody.class));
//        verify(s3Client).completeMultipartUpload(any(CompleteMultipartUploadRequest.class));
//    }
//
//    @Test
//    void testUploadFile_largeFile_createMultipartFails() throws IOException {
//        // Arrange
//        when(s3Client.createMultipartUpload(any(CreateMultipartUploadRequest.class)))
//                .thenThrow(S3Exception.builder().message("Failed to create multipart upload").build());
//
//        // Act & Assert - Based on the service implementation, it should wrap in IOException
//        IOException exception = assertThrows(IOException.class,
//                () -> s3FileService.uploadFile(largeFile, testFolder));
//
//        assertTrue(exception.getMessage().contains("Failed to upload large file"));
//        verify(s3Client).createMultipartUpload(any(CreateMultipartUploadRequest.class));
//        verify(s3Client, never()).uploadPart(any(UploadPartRequest.class), any(RequestBody.class));
//    }
//
//    @Test
//    void testUploadFile_largeFile_uploadPartFails() throws IOException {
//        // Arrange
//        CreateMultipartUploadResponse createResponse = CreateMultipartUploadResponse.builder()
//                .uploadId("test-upload-id")
//                .build();
//        when(s3Client.createMultipartUpload(any(CreateMultipartUploadRequest.class)))
//                .thenReturn(createResponse);
//
//        when(s3Client.uploadPart(any(UploadPartRequest.class), any(RequestBody.class)))
//                .thenThrow(S3Exception.builder().message("Upload part failed").build());
//
//        // Act & Assert - Based on the service implementation, it should wrap in IOException
//        IOException exception = assertThrows(IOException.class,
//                () -> s3FileService.uploadFile(largeFile, testFolder));
//
//        assertTrue(exception.getMessage().contains("Failed to upload large file"));
//        verify(s3Client).createMultipartUpload(any(CreateMultipartUploadRequest.class));
//        verify(s3Client).uploadPart(any(UploadPartRequest.class), any(RequestBody.class));
//        verify(s3Client).abortMultipartUpload(any(AbortMultipartUploadRequest.class));
//    }
//
//    @Test
//    void testUploadFile_largeFile_completeMultipartFails() throws IOException {
//        // Arrange
//        CreateMultipartUploadResponse createResponse = CreateMultipartUploadResponse.builder()
//                .uploadId("test-upload-id")
//                .build();
//        when(s3Client.createMultipartUpload(any(CreateMultipartUploadRequest.class)))
//                .thenReturn(createResponse);
//
//        UploadPartResponse uploadPartResponse = UploadPartResponse.builder()
//                .eTag("part-etag-1")
//                .build();
//        when(s3Client.uploadPart(any(UploadPartRequest.class), any(RequestBody.class)))
//                .thenReturn(uploadPartResponse);
//
//        when(s3Client.completeMultipartUpload(any(CompleteMultipartUploadRequest.class)))
//                .thenThrow(S3Exception.builder().message("Complete multipart failed").build());
//
//        // Act & Assert - Based on the service implementation, it should wrap in IOException
//        IOException exception = assertThrows(IOException.class,
//                () -> s3FileService.uploadFile(largeFile, testFolder));
//
//        assertTrue(exception.getMessage().contains("Failed to upload large file"));
//        verify(s3Client).abortMultipartUpload(any(AbortMultipartUploadRequest.class));
//    }
//
//    // ==================== FILE EXISTS TESTS ====================
//
//    @Test
//    void testFileExists_fileExists_success() {
//        // Arrange
//        HeadObjectResponse headResponse = HeadObjectResponse.builder()
//                .contentLength(1024L)
//                .build();
//        when(s3Client.headObject(any(HeadObjectRequest.class))).thenReturn(headResponse);
//
//        // Act
//        boolean result = s3FileService.fileExists("test-file.pdf");
//
//        // Assert
//        assertTrue(result);
//        verify(s3Client).headObject(any(HeadObjectRequest.class));
//    }
//
//    @Test
//    void testFileExists_fileNotFound() {
//        // Arrange
//        when(s3Client.headObject(any(HeadObjectRequest.class)))
//                .thenThrow(NoSuchKeyException.builder().message("File not found").build());
//
//        // Act
//        boolean result = s3FileService.fileExists("non-existent-file.pdf");
//
//        // Assert
//        assertFalse(result);
//        verify(s3Client).headObject(any(HeadObjectRequest.class));
//    }
//
//    @Test
//    void testFileExists_s3Exception() {
//        // Arrange
//        when(s3Client.headObject(any(HeadObjectRequest.class)))
//                .thenThrow(S3Exception.builder().message("S3 error").build());
//
//        // Act
//        boolean result = s3FileService.fileExists("error-file.pdf");
//
//        // Assert
//        assertFalse(result);
//        verify(s3Client).headObject(any(HeadObjectRequest.class));
//    }
//
//    @Test
//    void testFileExists_nullFileName() {
//        // Act
//        boolean result = s3FileService.fileExists(null);
//
//        // Assert
//        assertFalse(result);
//        verify(s3Client, never()).headObject(any(HeadObjectRequest.class));
//    }
//
//    @Test
//    void testFileExists_emptyFileName() {
//        // Act
//        boolean result = s3FileService.fileExists("");
//
//        // Assert
//        assertFalse(result);
//        verify(s3Client, never()).headObject(any(HeadObjectRequest.class));
//    }
//
//    @Test
//    void testFileExists_whitespaceFileName() {
//        // Arrange
//        HeadObjectResponse headResponse = HeadObjectResponse.builder()
//                .contentLength(1024L)
//                .build();
//        when(s3Client.headObject(any(HeadObjectRequest.class))).thenReturn(headResponse);
//
//        // Act
//        boolean result = s3FileService.fileExists("  test-file.pdf  ");
//
//        // Assert
//        assertTrue(result);
//        verify(s3Client).headObject(argThat((HeadObjectRequest request) ->
//                request.key().equals("test-file.pdf")
//        ));
//    }
//
//    // ==================== DELETE FILE TESTS ====================
//
//    @Test
//    void testDeleteFile_success_fileExists() {
//        // Arrange
//        HeadObjectResponse headResponse = HeadObjectResponse.builder()
//                .contentLength(1024L)
//                .build();
//        when(s3Client.headObject(any(HeadObjectRequest.class))).thenReturn(headResponse);
//
//        // Create proper mock for DeleteObjectResponse with responseMetadata
//        S3ResponseMetadata responseMetadata = mock(S3ResponseMetadata.class);
//        when(responseMetadata.requestId()).thenReturn("test-request-id");
//
//        DeleteObjectResponse deleteResponse = mock(DeleteObjectResponse.class);
//        when(deleteResponse.responseMetadata()).thenReturn(responseMetadata);
//        when(s3Client.deleteObject(any(DeleteObjectRequest.class))).thenReturn(deleteResponse);
//
//        // Act
//        boolean result = s3FileService.deleteFile("test-file.pdf");
//
//        // Assert
//        assertTrue(result);
//        verify(s3Client).headObject(any(HeadObjectRequest.class));
//        verify(s3Client).deleteObject(any(DeleteObjectRequest.class));
//    }
//
//    @Test
//    void testDeleteFile_fileNotExists() {
//        // Arrange
//        when(s3Client.headObject(any(HeadObjectRequest.class)))
//                .thenThrow(NoSuchKeyException.builder().message("File not found").build());
//
//        // Act
//        boolean result = s3FileService.deleteFile("non-existent-file.pdf");
//
//        // Assert
//        assertFalse(result);
//        verify(s3Client).headObject(any(HeadObjectRequest.class));
//        verify(s3Client, never()).deleteObject(any(DeleteObjectRequest.class));
//    }
//
//    @Test
//    void testDeleteFile_s3DeleteException() {
//        // Arrange
//        HeadObjectResponse headResponse = HeadObjectResponse.builder()
//                .contentLength(1024L)
//                .build();
//        when(s3Client.headObject(any(HeadObjectRequest.class))).thenReturn(headResponse);
//
//        when(s3Client.deleteObject(any(DeleteObjectRequest.class)))
//                .thenThrow(S3Exception.builder()
//                        .awsErrorDetails(AwsErrorDetails.builder()
//                                .errorCode("AccessDenied")
//                                .errorMessage("Access denied")
//                                .build())
//                        .message("Access denied")
//                        .build());
//
//        // Act & Assert
//        RuntimeException exception = assertThrows(RuntimeException.class,
//                () -> s3FileService.deleteFile("test-file.pdf"));
//
//        assertTrue(exception.getMessage().contains("S3 error while deleting file"));
//        assertTrue(exception.getMessage().contains("AccessDenied"));
//        verify(s3Client).deleteObject(any(DeleteObjectRequest.class));
//    }
//
//    @Test
//    void testDeleteFile_unexpectedException() {
//        // Arrange
//        HeadObjectResponse headResponse = HeadObjectResponse.builder()
//                .contentLength(1024L)
//                .build();
//        when(s3Client.headObject(any(HeadObjectRequest.class))).thenReturn(headResponse);
//
//        when(s3Client.deleteObject(any(DeleteObjectRequest.class)))
//                .thenThrow(new RuntimeException("Unexpected error"));
//
//        // Act & Assert
//        RuntimeException exception = assertThrows(RuntimeException.class,
//                () -> s3FileService.deleteFile("test-file.pdf"));
//
//        assertTrue(exception.getMessage().contains("Unexpected error while deleting file"));
//        verify(s3Client).deleteObject(any(DeleteObjectRequest.class));
//    }
//
//    @Test
//    void testDeleteFile_nullFileName() {
//        // Act & Assert
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
//                () -> s3FileService.deleteFile(null));
//
//        assertEquals("File name cannot be null or empty", exception.getMessage());
//        verify(s3Client, never()).headObject(any(HeadObjectRequest.class));
//        verify(s3Client, never()).deleteObject(any(DeleteObjectRequest.class));
//    }
//
//    @Test
//    void testDeleteFile_emptyFileName() {
//        // Act & Assert
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
//                () -> s3FileService.deleteFile(""));
//
//        assertEquals("File name cannot be null or empty", exception.getMessage());
//        verify(s3Client, never()).headObject(any(HeadObjectRequest.class));
//        verify(s3Client, never()).deleteObject(any(DeleteObjectRequest.class));
//    }
//
//    @Test
//    void testDeleteFile_whitespaceFileName() {
//        // Arrange
//        HeadObjectResponse headResponse = HeadObjectResponse.builder()
//                .contentLength(1024L)
//                .build();
//        when(s3Client.headObject(any(HeadObjectRequest.class))).thenReturn(headResponse);
//
//        // Create proper mock for DeleteObjectResponse with responseMetadata
//        S3ResponseMetadata responseMetadata = mock(S3ResponseMetadata.class);
//        when(responseMetadata.requestId()).thenReturn("test-request-id");
//
//        DeleteObjectResponse deleteResponse = mock(DeleteObjectResponse.class);
//        when(deleteResponse.responseMetadata()).thenReturn(responseMetadata);
//        when(s3Client.deleteObject(any(DeleteObjectRequest.class))).thenReturn(deleteResponse);
//
//        // Act
//        boolean result = s3FileService.deleteFile("  test-file.pdf  ");
//
//        // Assert
//        assertTrue(result);
//        verify(s3Client).headObject(argThat((HeadObjectRequest request) ->
//                request.key().equals("test-file.pdf")
//        ));
//        verify(s3Client).deleteObject(argThat((DeleteObjectRequest request) ->
//                request.key().equals("test-file.pdf")
//        ));
//    }
//
//    // ==================== GET FILE METADATA TESTS ====================
//
//    @Test
//    void testGetFileMetadata_success() {
//        // Arrange
//        HeadObjectResponse headResponse = HeadObjectResponse.builder()
//                .contentLength(1024L)
//                .contentType("application/pdf")
//                .build();
//        when(s3Client.headObject(any(HeadObjectRequest.class))).thenReturn(headResponse);
//
//        // Act
//        HeadObjectResponse result = s3FileService.getFileMetadata("test-file.pdf");
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(1024L, result.contentLength());
//        assertEquals("application/pdf", result.contentType());
//        verify(s3Client).headObject(any(HeadObjectRequest.class));
//    }
//
//    @Test
//    void testGetFileMetadata_fileNotFound() {
//        // Arrange
//        when(s3Client.headObject(any(HeadObjectRequest.class)))
//                .thenThrow(NoSuchKeyException.builder().message("File not found").build());
//
//        // Act & Assert
//        RuntimeException exception = assertThrows(RuntimeException.class,
//                () -> s3FileService.getFileMetadata("non-existent-file.pdf"));
//
//        assertTrue(exception.getMessage().contains("File not found"));
//        verify(s3Client).headObject(any(HeadObjectRequest.class));
//    }
//
//    // ==================== LIST FILES TESTS ====================
//
//    @Test
//    void testListFiles_success() {
//        // Arrange
//        S3Object s3Object1 = S3Object.builder().key("PDF/file1.pdf").build();
//        S3Object s3Object2 = S3Object.builder().key("PDF/file2.pdf").build();
//
//        ListObjectsV2Response listResponse = ListObjectsV2Response.builder()
//                .contents(s3Object1, s3Object2)
//                .isTruncated(false)
//                .build();
//        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(listResponse);
//
//        // Act
//        List<String> result = s3FileService.listFiles("PDF/", 10);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(2, result.size());
//        assertTrue(result.contains("PDF/file1.pdf"));
//        assertTrue(result.contains("PDF/file2.pdf"));
//        verify(s3Client).listObjectsV2(any(ListObjectsV2Request.class));
//    }
//
//    @Test
//    void testListFiles_empty() {
//        // Arrange
//        ListObjectsV2Response listResponse = ListObjectsV2Response.builder()
//                .isTruncated(false)
//                .build();
//        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(listResponse);
//
//        // Act
//        List<String> result = s3FileService.listFiles("PDF/", 10);
//
//        // Assert
//        assertNotNull(result);
//        assertTrue(result.isEmpty());
//        verify(s3Client).listObjectsV2(any(ListObjectsV2Request.class));
//    }
//
//    @Test
//    void testListFiles_withPagination() {
//        // Arrange - First page
//        S3Object s3Object1 = S3Object.builder().key("PDF/file1.pdf").build();
//        ListObjectsV2Response firstResponse = ListObjectsV2Response.builder()
//                .contents(s3Object1)
//                .isTruncated(true)
//                .nextContinuationToken("token123")
//                .build();
//
//        // Second page
//        S3Object s3Object2 = S3Object.builder().key("PDF/file2.pdf").build();
//        ListObjectsV2Response secondResponse = ListObjectsV2Response.builder()
//                .contents(s3Object2)
//                .isTruncated(false)
//                .build();
//
//        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class)))
//                .thenReturn(firstResponse)
//                .thenReturn(secondResponse);
//
//        // Act
//        List<String> result = s3FileService.listFiles("PDF/", 10);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(2, result.size());
//        assertTrue(result.contains("PDF/file1.pdf"));
//        assertTrue(result.contains("PDF/file2.pdf"));
//        verify(s3Client, times(2)).listObjectsV2(any(ListObjectsV2Request.class));
//    }
//
//    // ==================== EDGE CASES AND ERROR HANDLING ====================
//
//    @Test
//    void testUploadFile_unknownSize_adaptiveStreaming() throws IOException {
//        // Arrange - Create a file with unknown size (getSize() returns -1)
//        MockMultipartFile unknownSizeFile = new MockMultipartFile(
//                "file",
//                "test.pdf",
//                "application/pdf",
//                new ByteArrayInputStream("test content".getBytes())
//        ) {
//            @Override
//            public long getSize() {
//                return -1; // Unknown size
//            }
//        };
//
//        CreateMultipartUploadResponse createResponse = CreateMultipartUploadResponse.builder()
//                .uploadId("test-upload-id")
//                .build();
//        when(s3Client.createMultipartUpload(any(CreateMultipartUploadRequest.class)))
//                .thenReturn(createResponse);
//
//        UploadPartResponse uploadPartResponse = UploadPartResponse.builder()
//                .eTag("part-etag-1")
//                .build();
//        when(s3Client.uploadPart(any(UploadPartRequest.class), any(RequestBody.class)))
//                .thenReturn(uploadPartResponse);
//
//        CompleteMultipartUploadResponse completeResponse = CompleteMultipartUploadResponse.builder()
//                .location("s3://test-bucket/PDF/test-file")
//                .build();
//        when(s3Client.completeMultipartUpload(any(CompleteMultipartUploadRequest.class)))
//                .thenReturn(completeResponse);
//
//        // Act
//        String result = s3FileService.uploadFile(unknownSizeFile, testFolder);
//
//        // Assert
//        assertNotNull(result);
//        verify(s3Client).createMultipartUpload(any(CreateMultipartUploadRequest.class));
//        verify(s3Client).completeMultipartUpload(any(CompleteMultipartUploadRequest.class));
//    }
//
//    @Test
//    void testUploadFile_zeroSize_adaptiveStreaming() throws IOException {
//        // Arrange - Create a file with zero size but actual content
//        MockMultipartFile zeroSizeFile = new MockMultipartFile(
//                "file",
//                "test.pdf",
//                "application/pdf",
//                new ByteArrayInputStream("test content".getBytes())
//        ) {
//            @Override
//            public long getSize() {
//                return 0; // Zero size reported
//            }
//
//            @Override
//            public boolean isEmpty() {
//                return false; // But not actually empty
//            }
//        };
//
//        CreateMultipartUploadResponse createResponse = CreateMultipartUploadResponse.builder()
//                .uploadId("test-upload-id")
//                .build();
//        when(s3Client.createMultipartUpload(any(CreateMultipartUploadRequest.class)))
//                .thenReturn(createResponse);
//
//        UploadPartResponse uploadPartResponse = UploadPartResponse.builder()
//                .eTag("part-etag-1")
//                .build();
//        when(s3Client.uploadPart(any(UploadPartRequest.class), any(RequestBody.class)))
//                .thenReturn(uploadPartResponse);
//
//        CompleteMultipartUploadResponse completeResponse = CompleteMultipartUploadResponse.builder()
//                .location("s3://test-bucket/PDF/test-file")
//                .build();
//        when(s3Client.completeMultipartUpload(any(CompleteMultipartUploadRequest.class)))
//                .thenReturn(completeResponse);
//
//        // Act
//        String result = s3FileService.uploadFile(zeroSizeFile, testFolder);
//
//        // Assert
//        assertNotNull(result);
//        verify(s3Client).createMultipartUpload(any(CreateMultipartUploadRequest.class));
//        verify(s3Client).completeMultipartUpload(any(CompleteMultipartUploadRequest.class));
//    }
//
//    @Test
//    void testDeleteFile_fileExistsCheckFails_withS3Exception() {
//        // Arrange
//        when(s3Client.headObject(any(HeadObjectRequest.class)))
//                .thenThrow(S3Exception.builder()
//                        .awsErrorDetails(AwsErrorDetails.builder()
//                                .errorCode("InternalError")
//                                .errorMessage("Internal server error")
//                                .build())
//                        .message("Internal error")
//                        .build());
//
//        // Act
//        boolean result = s3FileService.deleteFile("test-file.pdf");
//
//        // Assert
//        assertFalse(result); // Should return false when existence check fails
//        verify(s3Client).headObject(any(HeadObjectRequest.class));
//        verify(s3Client, never()).deleteObject(any(DeleteObjectRequest.class));
//    }
//}

package com.nt.course_service_lms.serviceImplTest;

import com.nt.course_service_lms.exception.ResourceNotFoundException;
import com.nt.course_service_lms.service.serviceImpl.S3FileService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.AbortMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.model.S3ResponseMetadata;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;
import software.amazon.awssdk.services.s3.model.UploadPartResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class S3FileServiceTest {

    @Mock
    private S3Client s3Client;

    @InjectMocks
    private S3FileService s3FileService;

    private AutoCloseable closeable;

    private MockMultipartFile smallFile;
    private MockMultipartFile largeFile;
    private MockMultipartFile emptyFile;
    private final String bucketName = "test-bucket";
    private final String testFolder = "PDF";

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);

        // Set bucket name using ReflectionTestUtils
        ReflectionTestUtils.setField(s3FileService, "bucketName", bucketName);

        // Create test files
        smallFile = new MockMultipartFile(
                "file",
                "small-test.pdf",
                "application/pdf",
                "small file content".getBytes()
        );

        // Create a large file (> 8MB for multipart upload testing)
        byte[] largeContent = new byte[10 * 1024 * 1024]; // 10MB
        for (int i = 0; i < largeContent.length; i++) {
            largeContent[i] = (byte) (i % 256);
        }
        largeFile = new MockMultipartFile(
                "file",
                "large-test.pdf",
                "application/pdf",
                largeContent
        );

        emptyFile = new MockMultipartFile(
                "file",
                "empty-test.pdf",
                "application/pdf",
                new byte[0]
        );
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    // ==================== UPLOAD FILE TESTS (SMALL FILES) ====================

    @Test
    void testUploadFile_smallFile_success() throws IOException {
        // Arrange
        PutObjectResponse putResponse = PutObjectResponse.builder()
                .eTag("test-etag")
                .build();
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(putResponse);

        // Act
        String result = s3FileService.uploadFile(smallFile, testFolder);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("small-test.pdf"));
        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        verify(s3Client, never()).createMultipartUpload(any(CreateMultipartUploadRequest.class));
    }

    @Test
    void testUploadFile_smallFile_s3Exception() throws IOException {
        // Arrange
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenThrow(S3Exception.builder().message("S3 connection failed").build());

        // Act & Assert
        IOException exception = assertThrows(IOException.class,
                () -> s3FileService.uploadFile(smallFile, testFolder));

        assertTrue(exception.getMessage().contains("Failed to upload small file"));
        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void testUploadFile_emptyFile_throwsException() {
        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> s3FileService.uploadFile(emptyFile, testFolder));

        assertEquals("File not found", exception.getMessage());
        verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    // ==================== UPLOAD FILE TESTS (LARGE FILES - MULTIPART) ====================

    @Test
    void testUploadFile_largeFile_success() throws IOException {
        // Arrange
        CreateMultipartUploadResponse createResponse = CreateMultipartUploadResponse.builder()
                .uploadId("test-upload-id")
                .build();
        when(s3Client.createMultipartUpload(any(CreateMultipartUploadRequest.class)))
                .thenReturn(createResponse);

        UploadPartResponse uploadPartResponse = UploadPartResponse.builder()
                .eTag("part-etag-1")
                .build();
        when(s3Client.uploadPart(any(UploadPartRequest.class), any(RequestBody.class)))
                .thenReturn(uploadPartResponse);

        CompleteMultipartUploadResponse completeResponse = CompleteMultipartUploadResponse.builder()
                .location("s3://test-bucket/PDF/test-file")
                .build();
        when(s3Client.completeMultipartUpload(any(CompleteMultipartUploadRequest.class)))
                .thenReturn(completeResponse);

        // Act
        String result = s3FileService.uploadFile(largeFile, testFolder);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("large-test.pdf"));
        verify(s3Client).createMultipartUpload(any(CreateMultipartUploadRequest.class));
        verify(s3Client, atLeastOnce()).uploadPart(any(UploadPartRequest.class), any(RequestBody.class));
        verify(s3Client).completeMultipartUpload(any(CompleteMultipartUploadRequest.class));
    }

    @Test
    void testUploadFile_largeFile_createMultipartFails() throws IOException {
        // Arrange
        when(s3Client.createMultipartUpload(any(CreateMultipartUploadRequest.class)))
                .thenThrow(S3Exception.builder().message("Failed to create multipart upload").build());

        // Act & Assert
        IOException exception = assertThrows(IOException.class,
                () -> s3FileService.uploadFile(largeFile, testFolder));

        assertTrue(exception.getMessage().contains("Failed to upload large file"));
        verify(s3Client).createMultipartUpload(any(CreateMultipartUploadRequest.class));
        verify(s3Client, never()).uploadPart(any(UploadPartRequest.class), any(RequestBody.class));
    }

    @Test
    void testUploadFile_largeFile_uploadPartFails() throws IOException {
        // Arrange
        CreateMultipartUploadResponse createResponse = CreateMultipartUploadResponse.builder()
                .uploadId("test-upload-id")
                .build();
        when(s3Client.createMultipartUpload(any(CreateMultipartUploadRequest.class)))
                .thenReturn(createResponse);

        when(s3Client.uploadPart(any(UploadPartRequest.class), any(RequestBody.class)))
                .thenThrow(S3Exception.builder().message("Upload part failed").build());

        // Act & Assert
        IOException exception = assertThrows(IOException.class,
                () -> s3FileService.uploadFile(largeFile, testFolder));

        assertTrue(exception.getMessage().contains("Failed to upload large file"));
        verify(s3Client).createMultipartUpload(any(CreateMultipartUploadRequest.class));
        verify(s3Client).uploadPart(any(UploadPartRequest.class), any(RequestBody.class));
        verify(s3Client).abortMultipartUpload(any(AbortMultipartUploadRequest.class));
    }

    @Test
    void testUploadFile_largeFile_completeMultipartFails() throws IOException {
        // Arrange
        CreateMultipartUploadResponse createResponse = CreateMultipartUploadResponse.builder()
                .uploadId("test-upload-id")
                .build();
        when(s3Client.createMultipartUpload(any(CreateMultipartUploadRequest.class)))
                .thenReturn(createResponse);

        UploadPartResponse uploadPartResponse = UploadPartResponse.builder()
                .eTag("part-etag-1")
                .build();
        when(s3Client.uploadPart(any(UploadPartRequest.class), any(RequestBody.class)))
                .thenReturn(uploadPartResponse);

        when(s3Client.completeMultipartUpload(any(CompleteMultipartUploadRequest.class)))
                .thenThrow(S3Exception.builder().message("Complete multipart failed").build());

        // Act & Assert
        IOException exception = assertThrows(IOException.class,
                () -> s3FileService.uploadFile(largeFile, testFolder));

        assertTrue(exception.getMessage().contains("Failed to upload large file"));
        verify(s3Client).abortMultipartUpload(any(AbortMultipartUploadRequest.class));
    }

    // ==================== FILE EXISTS TESTS ====================

    @Test
    void testFileExists_fileExists_success() {
        // Arrange
        HeadObjectResponse headResponse = HeadObjectResponse.builder()
                .contentLength(1024L)
                .build();
        when(s3Client.headObject(any(HeadObjectRequest.class))).thenReturn(headResponse);

        // Act
        boolean result = s3FileService.fileExists("test-file.pdf");

        // Assert
        assertTrue(result);
        verify(s3Client).headObject(any(HeadObjectRequest.class));
    }

    @Test
    void testFileExists_fileNotFound() {
        // Arrange
        when(s3Client.headObject(any(HeadObjectRequest.class)))
                .thenThrow(NoSuchKeyException.builder().message("File not found").build());

        // Act
        boolean result = s3FileService.fileExists("non-existent-file.pdf");

        // Assert
        assertFalse(result);
        verify(s3Client).headObject(any(HeadObjectRequest.class));
    }

    @Test
    void testFileExists_s3Exception() {
        // Arrange
        when(s3Client.headObject(any(HeadObjectRequest.class)))
                .thenThrow(S3Exception.builder().message("S3 error").build());

        // Act
        boolean result = s3FileService.fileExists("error-file.pdf");

        // Assert
        assertFalse(result);
        verify(s3Client).headObject(any(HeadObjectRequest.class));
    }

    @Test
    void testFileExists_nullFileName() {
        // Act
        boolean result = s3FileService.fileExists(null);

        // Assert
        assertFalse(result);
        verify(s3Client, never()).headObject(any(HeadObjectRequest.class));
    }

    @Test
    void testFileExists_emptyFileName() {
        // Act
        boolean result = s3FileService.fileExists("");

        // Assert
        assertFalse(result);
        verify(s3Client, never()).headObject(any(HeadObjectRequest.class));
    }

    @Test
    void testFileExists_whitespaceFileName() {
        // Arrange
        HeadObjectResponse headResponse = HeadObjectResponse.builder()
                .contentLength(1024L)
                .build();
        when(s3Client.headObject(any(HeadObjectRequest.class))).thenReturn(headResponse);

        // Act
        boolean result = s3FileService.fileExists("  test-file.pdf  ");

        // Assert
        assertTrue(result);
        verify(s3Client).headObject(argThat((HeadObjectRequest request) ->
                request.key().equals("test-file.pdf")
        ));
    }

    // ==================== DELETE FILE TESTS ====================

    @Test
    void testDeleteFile_success_fileExists() {
        // Arrange
        HeadObjectResponse headResponse = HeadObjectResponse.builder()
                .contentLength(1024L)
                .build();
        when(s3Client.headObject(any(HeadObjectRequest.class))).thenReturn(headResponse);

        // Create proper mock for DeleteObjectResponse with responseMetadata
        S3ResponseMetadata responseMetadata = mock(S3ResponseMetadata.class);
        when(responseMetadata.requestId()).thenReturn("test-request-id");

        DeleteObjectResponse deleteResponse = mock(DeleteObjectResponse.class);
        when(deleteResponse.responseMetadata()).thenReturn(responseMetadata);
        when(s3Client.deleteObject(any(DeleteObjectRequest.class))).thenReturn(deleteResponse);

        // Act
        boolean result = s3FileService.deleteFile("test-file.pdf");

        // Assert
        assertTrue(result);
        verify(s3Client).headObject(any(HeadObjectRequest.class));
        verify(s3Client).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    void testDeleteFile_fileNotExists() {
        // Arrange
        when(s3Client.headObject(any(HeadObjectRequest.class)))
                .thenThrow(NoSuchKeyException.builder().message("File not found").build());

        // Act
        boolean result = s3FileService.deleteFile("non-existent-file.pdf");

        // Assert
        assertFalse(result);
        verify(s3Client).headObject(any(HeadObjectRequest.class));
        verify(s3Client, never()).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    void testDeleteFile_s3DeleteException() {
        // Arrange
        HeadObjectResponse headResponse = HeadObjectResponse.builder()
                .contentLength(1024L)
                .build();
        when(s3Client.headObject(any(HeadObjectRequest.class))).thenReturn(headResponse);

        when(s3Client.deleteObject(any(DeleteObjectRequest.class)))
                .thenThrow(S3Exception.builder()
                        .awsErrorDetails(AwsErrorDetails.builder()
                                .errorCode("AccessDenied")
                                .errorMessage("Access denied")
                                .build())
                        .message("Access denied")
                        .build());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> s3FileService.deleteFile("test-file.pdf"));

        assertTrue(exception.getMessage().contains("S3 error while deleting file"));
        assertTrue(exception.getMessage().contains("AccessDenied"));
        verify(s3Client).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    void testDeleteFile_unexpectedException() {
        // Arrange
        HeadObjectResponse headResponse = HeadObjectResponse.builder()
                .contentLength(1024L)
                .build();
        when(s3Client.headObject(any(HeadObjectRequest.class))).thenReturn(headResponse);

        when(s3Client.deleteObject(any(DeleteObjectRequest.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> s3FileService.deleteFile("test-file.pdf"));

        assertTrue(exception.getMessage().contains("Unexpected error while deleting file"));
        verify(s3Client).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    void testDeleteFile_nullFileName() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> s3FileService.deleteFile(null));

        assertEquals("File name cannot be null or empty", exception.getMessage());
        verify(s3Client, never()).headObject(any(HeadObjectRequest.class));
        verify(s3Client, never()).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    void testDeleteFile_emptyFileName() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> s3FileService.deleteFile(""));

        assertEquals("File name cannot be null or empty", exception.getMessage());
        verify(s3Client, never()).headObject(any(HeadObjectRequest.class));
        verify(s3Client, never()).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    void testDeleteFile_whitespaceFileName() {
        // Arrange
        HeadObjectResponse headResponse = HeadObjectResponse.builder()
                .contentLength(1024L)
                .build();
        when(s3Client.headObject(any(HeadObjectRequest.class))).thenReturn(headResponse);

        // Create proper mock for DeleteObjectResponse with responseMetadata
        S3ResponseMetadata responseMetadata = mock(S3ResponseMetadata.class);
        when(responseMetadata.requestId()).thenReturn("test-request-id");

        DeleteObjectResponse deleteResponse = mock(DeleteObjectResponse.class);
        when(deleteResponse.responseMetadata()).thenReturn(responseMetadata);
        when(s3Client.deleteObject(any(DeleteObjectRequest.class))).thenReturn(deleteResponse);

        // Act
        boolean result = s3FileService.deleteFile("  test-file.pdf  ");

        // Assert
        assertTrue(result);
        verify(s3Client).headObject(argThat((HeadObjectRequest request) ->
                request.key().equals("test-file.pdf")
        ));
        verify(s3Client).deleteObject(argThat((DeleteObjectRequest request) ->
                request.key().equals("test-file.pdf")
        ));
    }

    // ==================== GET FILE METADATA TESTS ====================

    @Test
    void testGetFileMetadata_success() {
        // Arrange
        HeadObjectResponse headResponse = HeadObjectResponse.builder()
                .contentLength(1024L)
                .contentType("application/pdf")
                .build();
        when(s3Client.headObject(any(HeadObjectRequest.class))).thenReturn(headResponse);

        // Act
        HeadObjectResponse result = s3FileService.getFileMetadata("test-file.pdf");

        // Assert
        assertNotNull(result);
        assertEquals(1024L, result.contentLength());
        assertEquals("application/pdf", result.contentType());
        verify(s3Client).headObject(any(HeadObjectRequest.class));
    }

    @Test
    void testGetFileMetadata_fileNotFound() {
        // Arrange
        when(s3Client.headObject(any(HeadObjectRequest.class)))
                .thenThrow(NoSuchKeyException.builder().message("File not found").build());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> s3FileService.getFileMetadata("non-existent-file.pdf"));

        assertTrue(exception.getMessage().contains("File not found"));
        verify(s3Client).headObject(any(HeadObjectRequest.class));
    }

    // ==================== LIST FILES TESTS ====================

    @Test
    void testListFiles_success() {
        // Arrange
        S3Object s3Object1 = S3Object.builder().key("PDF/file1.pdf").build();
        S3Object s3Object2 = S3Object.builder().key("PDF/file2.pdf").build();

        ListObjectsV2Response listResponse = ListObjectsV2Response.builder()
                .contents(s3Object1, s3Object2)
                .isTruncated(false)
                .build();
        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(listResponse);

        // Act
        List<String> result = s3FileService.listFiles("PDF/", 10);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains("PDF/file1.pdf"));
        assertTrue(result.contains("PDF/file2.pdf"));
        verify(s3Client).listObjectsV2(any(ListObjectsV2Request.class));
    }

    @Test
    void testListFiles_empty() {
        // Arrange
        ListObjectsV2Response listResponse = ListObjectsV2Response.builder()
                .isTruncated(false)
                .build();
        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(listResponse);

        // Act
        List<String> result = s3FileService.listFiles("PDF/", 10);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(s3Client).listObjectsV2(any(ListObjectsV2Request.class));
    }

    @Test
    void testListFiles_withPagination() {
        // Arrange - First page
        S3Object s3Object1 = S3Object.builder().key("PDF/file1.pdf").build();
        ListObjectsV2Response firstResponse = ListObjectsV2Response.builder()
                .contents(s3Object1)
                .isTruncated(true)
                .nextContinuationToken("token123")
                .build();

        // Second page
        S3Object s3Object2 = S3Object.builder().key("PDF/file2.pdf").build();
        ListObjectsV2Response secondResponse = ListObjectsV2Response.builder()
                .contents(s3Object2)
                .isTruncated(false)
                .build();

        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class)))
                .thenReturn(firstResponse)
                .thenReturn(secondResponse);

        // Act
        List<String> result = s3FileService.listFiles("PDF/", 10);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains("PDF/file1.pdf"));
        assertTrue(result.contains("PDF/file2.pdf"));
        verify(s3Client, times(2)).listObjectsV2(any(ListObjectsV2Request.class));
    }

    // ==================== EDGE CASES AND ERROR HANDLING ====================

    @Test
    void testUploadFile_unknownSize_adaptiveStreaming() throws IOException {
        // Arrange - Create a file with unknown size (getSize() returns -1)
        MockMultipartFile unknownSizeFile = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                new ByteArrayInputStream("test content".getBytes())
        ) {
            @Override
            public long getSize() {
                return -1; // Unknown size
            }
        };

        CreateMultipartUploadResponse createResponse = CreateMultipartUploadResponse.builder()
                .uploadId("test-upload-id")
                .build();
        when(s3Client.createMultipartUpload(any(CreateMultipartUploadRequest.class)))
                .thenReturn(createResponse);

        UploadPartResponse uploadPartResponse = UploadPartResponse.builder()
                .eTag("part-etag-1")
                .build();
        when(s3Client.uploadPart(any(UploadPartRequest.class), any(RequestBody.class)))
                .thenReturn(uploadPartResponse);

        CompleteMultipartUploadResponse completeResponse = CompleteMultipartUploadResponse.builder()
                .location("s3://test-bucket/PDF/test-file")
                .build();
        when(s3Client.completeMultipartUpload(any(CompleteMultipartUploadRequest.class)))
                .thenReturn(completeResponse);

        // Act
        String result = s3FileService.uploadFile(unknownSizeFile, testFolder);

        // Assert
        assertNotNull(result);
        verify(s3Client).createMultipartUpload(any(CreateMultipartUploadRequest.class));
        verify(s3Client).completeMultipartUpload(any(CompleteMultipartUploadRequest.class));
    }

    @Test
    void testUploadFile_zeroSize_adaptiveStreaming() throws IOException {
        // Arrange - Create a file with zero size but actual content
        MockMultipartFile zeroSizeFile = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                new ByteArrayInputStream("test content".getBytes())
        ) {
            @Override
            public long getSize() {
                return 0; // Zero size reported
            }

            @Override
            public boolean isEmpty() {
                return false; // But not actually empty
            }
        };

        CreateMultipartUploadResponse createResponse = CreateMultipartUploadResponse.builder()
                .uploadId("test-upload-id")
                .build();
        when(s3Client.createMultipartUpload(any(CreateMultipartUploadRequest.class)))
                .thenReturn(createResponse);

        UploadPartResponse uploadPartResponse = UploadPartResponse.builder()
                .eTag("part-etag-1")
                .build();
        when(s3Client.uploadPart(any(UploadPartRequest.class), any(RequestBody.class)))
                .thenReturn(uploadPartResponse);

        CompleteMultipartUploadResponse completeResponse = CompleteMultipartUploadResponse.builder()
                .location("s3://test-bucket/PDF/test-file")
                .build();
        when(s3Client.completeMultipartUpload(any(CompleteMultipartUploadRequest.class)))
                .thenReturn(completeResponse);

        // Act
        String result = s3FileService.uploadFile(zeroSizeFile, testFolder);

        // Assert
        assertNotNull(result);
        verify(s3Client).createMultipartUpload(any(CreateMultipartUploadRequest.class));
        verify(s3Client).completeMultipartUpload(any(CompleteMultipartUploadRequest.class));
    }

    @Test
    void testDeleteFile_fileExistsCheckFails_withS3Exception() {
        // Arrange
        when(s3Client.headObject(any(HeadObjectRequest.class)))
                .thenThrow(S3Exception.builder()
                        .awsErrorDetails(AwsErrorDetails.builder()
                                .errorCode("InternalError")
                                .errorMessage("Internal server error")
                                .build())
                        .message("Internal error")
                        .build());

        // Act
        boolean result = s3FileService.deleteFile("test-file.pdf");

        // Assert
        assertFalse(result); // Should return false when existence check fails
        verify(s3Client).headObject(any(HeadObjectRequest.class));
        verify(s3Client, never()).deleteObject(any(DeleteObjectRequest.class));
    }
}
