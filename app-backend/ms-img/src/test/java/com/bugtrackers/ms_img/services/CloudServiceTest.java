package com.bugtrackers.ms_img.services;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.UUID;

import com.google.cloud.storage.Blob;
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

    @Mock
    private Blob blob;

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

    @Test
    void shouldUploadNewProfile() throws IOException {
        String nameOldImage = "images/oldImage.jpg";
        String objectName = "images/" + UUID.randomUUID().toString();
        byte[] fileBytes = "file-content".getBytes();
        String contentType = "image/jpeg";

        when(file.getBytes()).thenReturn(fileBytes);
        when(file.getContentType()).thenReturn(contentType);

        when(storage.get(bucketName)).thenReturn(bucket);
        Blob blob = mock(Blob.class);
        when(bucket.get(nameOldImage)).thenReturn(blob);
        when(blob.exists()).thenReturn(true);

        when(bucket.create(anyString(), eq(fileBytes), eq(contentType))).thenReturn(null);

        String result = cloudService.uploadProfileImage(file, nameOldImage);

        verify(bucket, times(1)).get(nameOldImage);
        verify(blob, times(1)).delete();
        verify(bucket, times(1)).create(anyString(), eq(fileBytes), eq(contentType));

        assertTrue(result.startsWith("images/"));
    }

    @Test
    void shouldUploadNewProfileNull() throws IOException {
        String objectName = "images/" + UUID.randomUUID().toString();
        byte[] fileBytes = "file-content".getBytes();
        String contentType = "image/jpeg";

        when(file.getBytes()).thenReturn(fileBytes);
        when(file.getContentType()).thenReturn(contentType);

        when(storage.get(bucketName)).thenReturn(bucket);

        when(bucket.create(anyString(), eq(fileBytes), eq(contentType))).thenReturn(null);

        String result = cloudService.uploadProfileImage(file, "");

        verify(bucket, never()).get(anyString());
        verify(bucket, times(1)).create(anyString(), eq(fileBytes), eq(contentType));

        assertTrue(result.startsWith("images/"));
    }

    @Test
    void shouldThrowFileNotCreated() throws IOException {
        String objectName = "images/" + UUID.randomUUID().toString();
        byte[] fileBytes = "file-content".getBytes();
        String contentType = "image/jpeg";

        when(file.getBytes()).thenReturn(fileBytes);
        when(file.getContentType()).thenReturn(contentType);

        when(storage.get(bucketName)).thenReturn(bucket);

        when(bucket.create(anyString(), eq(fileBytes), eq(contentType))).thenThrow(new RuntimeException("Error al subir"));

        assertThrows(FileNotCreatedException.class, () -> {
            cloudService.uploadProfileImage(file, "");
        });

        verify(bucket, times(1)).create(anyString(), eq(fileBytes), eq(contentType));
    }

    @Test
    void shouldDeleteImageIfExists() {
        String imagePath = "images/oldImage.jpg";

        when(bucket.get(imagePath)).thenReturn(blob);
        when(blob.exists()).thenReturn(true);

        cloudService.deleteImage(imagePath, bucket);

        verify(blob, times(1)).delete();
    }


    @Test
    void shouldThrowFileNotCreatedExceptionIfDeleteFails() {
        String imagePath = "images/oldImage.jpg";

        when(bucket.get(imagePath)).thenReturn(blob);
        when(blob.exists()).thenReturn(true);

        doThrow(new RuntimeException("Error al eliminar")).when(blob).delete();

        assertThrows(FileNotCreatedException.class, () -> cloudService.deleteImage(imagePath, bucket));
    }


    @Test
    void shouldThrowFileNotCreatedExceptionWhenBlobCannotBeObtained() {
        String imagePath = "images/oldImage.jpg";

        when(bucket.get(imagePath)).thenThrow(new RuntimeException("Error al obtener el blob"));

        assertThrows(FileNotCreatedException.class, () -> cloudService.deleteImage(imagePath, bucket));

        verify(bucket, times(1)).get(imagePath);
        verify(blob, never()).delete();
    }

    @Test
    void shouldDeleteOldImageIfExistsInUploadProfileImage() throws IOException {
        String nameOldImage = "images/oldImage.jpg";
        String objectName = "images/" + UUID.randomUUID().toString();
        byte[] fileBytes = "file-content".getBytes();
        String contentType = "image/jpeg";

        when(file.getBytes()).thenReturn(fileBytes);
        when(file.getContentType()).thenReturn(contentType);

        when(storage.get(bucketName)).thenReturn(bucket);
        when(bucket.get(nameOldImage)).thenReturn(blob);
        when(blob.exists()).thenReturn(true);

        when(bucket.create(anyString(), eq(fileBytes), eq(contentType))).thenReturn(null);

        String result = cloudService.uploadProfileImage(file, nameOldImage);

        verify(bucket, times(1)).get(nameOldImage);
        verify(blob, times(1)).delete();

        verify(bucket, times(1)).create(anyString(), eq(fileBytes), eq(contentType));
        assertTrue(result.startsWith("images/"));
    }

    @Test
    void shouldDeleteImageIfBlobExists() {
        String imagePath = "images/oldImage.jpg";

        when(bucket.get(imagePath)).thenReturn(blob);
        when(blob.exists()).thenReturn(true);

        cloudService.deleteImage(imagePath, bucket);

        verify(blob, times(1)).delete();
    }

    @Test
    void shouldNotDeleteImageIfBlobDoesNotExist() {
        String imagePath = "images/oldImage.jpg";

        when(bucket.get(imagePath)).thenReturn(blob);
        when(blob.exists()).thenReturn(false);

        cloudService.deleteImage(imagePath, bucket);

        verify(blob, never()).delete();
    }

    @Test
    void shouldCreateNewProfileImage() throws IOException {
        String objectName = "images/" + UUID.randomUUID().toString();
        byte[] fileBytes = "file-content".getBytes();
        String contentType = "image/jpeg";

        when(file.getBytes()).thenReturn(fileBytes);
        when(file.getContentType()).thenReturn(contentType);

        when(storage.get(bucketName)).thenReturn(bucket);
        when(bucket.create(anyString(), eq(fileBytes), eq(contentType))).thenReturn(null);

        String result = cloudService.uploadProfileImage(file, null);

        verify(bucket, times(1)).create(anyString(), eq(fileBytes), eq(contentType));
        assertTrue(result.startsWith("images/"));
    }
    @Test
    void shouldThrowFileNotCreatedExceptionWhenDeleteImageFails() {
        String imagePath = "images/oldImage.jpg";

        when(bucket.get(imagePath)).thenReturn(blob);
        when(blob.exists()).thenReturn(true);

        doThrow(new RuntimeException("Error al eliminar")).when(blob).delete();

        assertThrows(FileNotCreatedException.class, () -> {
            cloudService.deleteImage(imagePath, bucket);
        });

        verify(blob, times(1)).delete();
    }


    @Test
    void shouldNotDeleteBlobIfBlobExistsReturnsFalse() {
        String imagePath = "images/oldImage.jpg";

        when(bucket.get(imagePath)).thenReturn(blob);
        when(blob.exists()).thenReturn(false);

        cloudService.deleteImage(imagePath, bucket);

        verify(blob, never()).delete();
    }

    @Test
    void shouldThrowFileNotCreatedExceptionWhenBlobIsNull() {
        String imagePath = "images/oldImage.jpg";

        when(bucket.get(imagePath)).thenReturn(null);

        assertThrows(FileNotCreatedException.class, () -> {
            cloudService.deleteImage(imagePath, bucket);
        });

        verify(blob, never()).delete();
    }





}
