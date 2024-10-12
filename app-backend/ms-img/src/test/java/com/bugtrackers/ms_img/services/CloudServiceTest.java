package com.bugtrackers.ms_img.services;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import com.bugtrackers.ms_img.exceptions.FileNotCreatedException;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;

public class CloudServiceTest {

    @InjectMocks
    private CloudService cloudService;

    @Mock
    private Storage storage;

    private final String bucketName = "test-bucket";

    @Mock
    private Bucket bucket;

    @Mock
    private MultipartFile file;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cloudService = new CloudService(storage, bucketName);
    }
    
    @Test
    void shouldSaveFile() throws IOException {
        String objectName = "images/" + UUID.randomUUID().toString();
        byte[] fileBytes = "file-content".getBytes();
        String contentType = "image/jpeg";
        
        
        when(storage.get(bucketName)).thenReturn(bucket);
        when(bucket.create(objectName, fileBytes, contentType)).thenReturn(null);
        when(file.getBytes()).thenReturn(fileBytes);
        when(file.getContentType()).thenReturn(contentType);

        String result = cloudService.uploadImage(file);

        verify(bucket, times(1)).create(anyString(), any(byte[].class), anyString());
        assertTrue(result.startsWith("images/"));
    }

    @Test
    void shouldThrowFileNotCreatedException() throws IOException {
        when(file.getBytes()).thenThrow(new IOException());

        assertThrows(FileNotCreatedException.class, () -> cloudService.uploadImage(file));
    }

}
