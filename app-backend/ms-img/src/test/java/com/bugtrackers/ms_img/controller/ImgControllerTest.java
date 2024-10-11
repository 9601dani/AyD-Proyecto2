package com.bugtrackers.ms_img.controller;

import com.bugtrackers.ms_img.services.CloudService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

@WebMvcTest(ImgController.class)
public class ImgControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CloudService cloudService;

    @Test
    public void uploadImage_shouldReturnPublicUrl() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file", "test-image.jpg", MediaType.IMAGE_JPEG_VALUE, "image data".getBytes()
        );

        Mockito.when(cloudService.uploadImage(Mockito.any())).thenReturn("test-image.jpg");
        Mockito.when(cloudService.getPublicUrl("test-image.jpg")).thenReturn("https://storage.googleapis.com/bucket/test-image.jpg");

        mockMvc.perform(multipart("/img/upload-test").file(file))
                .andExpect(status().isOk())
                .andExpect(content().string("https://storage.googleapis.com/bucket/test-image.jpg"));
    }

    @Test
    public void uploadMultipleImages_shouldReturnListOfUrls() throws Exception {
        MockMultipartFile file1 = new MockMultipartFile("files", "test1.jpg", MediaType.IMAGE_JPEG_VALUE, "image1 data".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("files", "test2.jpg", MediaType.IMAGE_JPEG_VALUE, "image2 data".getBytes());

        Mockito.when(cloudService.uploadImage(Mockito.any())).thenReturn("test1.jpg", "test2.jpg");
        Mockito.when(cloudService.getPublicUrl("test1.jpg")).thenReturn("https://storage.googleapis.com/bucket/images/test1.jpg");
        Mockito.when(cloudService.getPublicUrl("test2.jpg")).thenReturn("https://storage.googleapis.com/bucket/images/test2.jpg");

        mockMvc.perform(multipart("/img/upload").file(file1).file(file2))
                .andExpect(status().isOk())
                .andExpect(content().json("[\"https://storage.googleapis.com/bucket/images/test1.jpg\", \"https://storage.googleapis.com/bucket/images/test2.jpg\"]"));
    }
}
