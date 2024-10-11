package com.bugtrackers.ms_img.services;

import com.google.cloud.storage.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class CloudServiceTest {

   /*  @Mock
    private Storage storage;

    @Mock
    private Bucket bucket;

    @Mock
    private ResourceLoader resourceLoader;

    @Mock
    private Blob blob;

    @InjectMocks
    private CloudService cloudService;

    private final String bucketName = "test-bucket";
    private final String credentialsPath = "classpath:credentials.json";

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        Resource resource = mock(Resource.class);
        InputStream inputStream = new ByteArrayInputStream("test-credentials".getBytes());
        when(resourceLoader.getResource(credentialsPath)).thenReturn(resource);
        when(resource.getInputStream()).thenReturn(inputStream);
        when(storage.get(bucketName)).thenReturn(bucket);
    }

    @Test
    void testUploadImage() throws IOException {
        // Arrange
        MultipartFile mockFile = new MockMultipartFile(
                "image.jpg", "image.jpg", "image/jpeg", "test image".getBytes());

        String expectedObjectName = "images/" + UUID.randomUUID().toString();
        when(bucket.create(anyString(), any(byte[].class), anyString())).thenAnswer(invocation -> {
            String objectName = invocation.getArgument(0);
            assertEquals(expectedObjectName.substring(0, 7), objectName.substring(0, 7)); // Verify folder
            return mock(Blob.class); // Simulate Blob creation
        });

        // Act
        String objectName = cloudService.uploadImage(mockFile);

        // Assert
        assertEquals(expectedObjectName.substring(0, 7), objectName.substring(0, 7)); // Check folder structure
        verify(bucket, times(1)).create(anyString(), any(byte[].class), anyString());
    }

    @Test
    void testGetImage() {
        // Arrange
        String objectName = "images/test-image.jpg";
        byte[] expectedContent = "test image content".getBytes();
        when(storage.get(bucketName, objectName)).thenReturn(blob);
        when(blob.getContent()).thenReturn(expectedContent);

        // Act
        byte[] result = cloudService.getImage(objectName);

        // Assert
        assertEquals(expectedContent.length, result.length);
        verify(storage, times(1)).get(bucketName, objectName);
    }*/
}
