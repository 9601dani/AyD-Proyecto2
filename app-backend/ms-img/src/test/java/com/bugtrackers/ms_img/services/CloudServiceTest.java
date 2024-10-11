package com.bugtrackers.ms_img.services;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class CloudServiceTest {

    /*@Mock
    private Storage mockStorage;

    @Mock
    private Bucket mockBucket;

    @Mock
    private Blob mockBlob;

    @Mock
    private ResourceLoader mockResourceLoader;

    @Mock
    private Resource mockResource;

    @Mock
    private MultipartFile mockFile;

    @Mock
    private GoogleCredentials mockGoogleCredentials;

    private CloudService cloudService;

    private final String bucketName = "bucket_ayd1";

    @BeforeEach
    public void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        when(mockGoogleCredentials.createScoped(anyList())).thenReturn(mockGoogleCredentials);
        when(mockResourceLoader.getResource(anyString())).thenReturn(mockResource);
        cloudService = new CloudService("bucket_ayd1", "bucket_ayd1", mockResourceLoader);
        when(mockStorage.get(bucketName)).thenReturn(mockBucket);
    }

    @Test
    public void testUploadImage() throws IOException {
        byte[] fileContent = "test file content".getBytes();
        when(mockFile.getBytes()).thenReturn(fileContent);
        when(mockFile.getContentType()).thenReturn("image/jpeg");
        when(mockBucket.create(anyString(), any(byte[].class), anyString())).thenReturn(mockBlob);
        String objectName = cloudService.uploadImage(mockFile);
        assertNotNull(objectName);
        assertTrue(objectName.startsWith("images/"));
        verify(mockBucket, times(1)).create(anyString(), eq(fileContent), eq("image/jpeg"));
    }

    @Test
    public void testGetImage() {
        byte[] blobContent = "test image content".getBytes();
        when(mockBlob.getContent()).thenReturn(blobContent);
        when(mockStorage.get(bucketName, "test-image.jpg")).thenReturn(mockBlob);
        byte[] result = cloudService.getImage("test-image.jpg");
        assertNotNull(result);
        assertArrayEquals(blobContent, result);
    }

    @Test
    public void testGetPublicUrl() {
        String publicUrl = cloudService.getPublicUrl("test-image.jpg");
        String expectedUrl = String.format("https://storage.googleapis.com/%s/%s", bucketName, "test-image.jpg");
        assertEquals(expectedUrl, publicUrl);
    }*/
}
