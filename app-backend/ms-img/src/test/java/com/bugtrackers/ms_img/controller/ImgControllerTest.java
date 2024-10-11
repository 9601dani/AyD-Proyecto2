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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.UUID;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@WebMvcTest(ImgController.class)
public class ImgControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CloudService cloudService;

    @Test
    public void helloWorld_shouldReturnHelloWorld() throws Exception {
        mockMvc.perform(get("/img"))
		.andExpect(status().isOk())
        .andExpect(content().string("Hello World Img Controller!"));
    }

    @Test
    public void uploadImage_shouldReturnObjectName() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file", "test-image.jpg", MediaType.IMAGE_JPEG_VALUE, "image data".getBytes()
        );

        String mockObjectName = "images/" + UUID.randomUUID();
        Mockito.when(cloudService.uploadImage(Mockito.any())).thenReturn(mockObjectName);

        mockMvc.perform(multipart("/img/upload-test").file(file))
                .andExpect(status().isOk())
                .andExpect(content().string(mockObjectName));
    }

    @Test
    public void uploadMultipleImages_shouldReturnListOfObjectNames() throws Exception {
        MockMultipartFile file1 = new MockMultipartFile("files","test1.jpg", MediaType.IMAGE_JPEG_VALUE, "image1 data".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("files", "test2.jpg", MediaType.IMAGE_JPEG_VALUE, "image2 data".getBytes());

        String objectName1 = "images/" + UUID.randomUUID();
        String objectName2 = "images/" + UUID.randomUUID();

        List<String> objectNames = Arrays.asList(objectName1, objectName2);

        Mockito.when(cloudService.uploadImage(Mockito.any())).thenReturn(objectName1, objectName2);

        mockMvc.perform(multipart("/img/upload").file(file1).file(file2))
                .andExpect(status().isOk())
                .andExpect(content().json("[\"" + objectName1 + "\", \"" + objectName2 + "\"]"));
    }

    @Test
    public void uploadImage_shouldReturnErrorMessageOnException() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test-image.jpg", MediaType.IMAGE_JPEG_VALUE, "image data".getBytes()
        );
        Mockito.when(cloudService.uploadImage(Mockito.any())).thenThrow(new IOException("Simulated IO Exception"));

        mockMvc.perform(multipart("/img/upload-test").file(file))
                .andExpect(status().isOk())
                .andExpect(content().string("Error al subir la imagen: Simulated IO Exception"));
    }

    @Test
public void uploadMultipleImages_shouldReturnErrorMessagesOnException() throws Exception {
    MockMultipartFile file1 = new MockMultipartFile("files", "test1.jpg", MediaType.IMAGE_JPEG_VALUE, "image1 data".getBytes());
    MockMultipartFile file2 = new MockMultipartFile("files", "test2.jpg", MediaType.IMAGE_JPEG_VALUE, "image2 data".getBytes());

    String objectName1 = "images/" + UUID.randomUUID();
    
    Mockito.when(cloudService.uploadImage(file1)).thenReturn(objectName1);

    Mockito.when(cloudService.uploadImage(file2)).thenThrow(new IOException("Simulated IO Exception"));

    mockMvc.perform(multipart("/img/upload").file(file1).file(file2))
            .andExpect(status().isOk())
            .andExpect(content().json("[\"" + objectName1 + "\", \"Error al subir: test2.jpg\"]"));
}

}
