package com.bugtrackers.ms_img.controller;

import com.bugtrackers.ms_img.dto.response.ResponseString;
import com.bugtrackers.ms_img.services.CloudService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(ImgController.class)
public class ImgControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CloudService cloudService;

    @Test
    void shouldReturnHelloWorld() throws Exception {
        mockMvc.perform(get("/img"))
		.andExpect(status().isOk())
        .andExpect(content().string("Hello World Img Controller!"));
    }

    @Test
    void shouldReturnImageName() throws Exception {
        String filename = "test-name";
        MockMultipartFile multipartFile = new MockMultipartFile("file", "test-image.png", "image/png", "some-image-content".getBytes());
        when(cloudService.uploadImage(multipartFile)).thenReturn(filename);

        mockMvc.perform(multipart("/img/upload-test")
        .file(multipartFile)
        .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isOk())
        .andExpect(content().string(filename));
    }

    @Test
    void shouldReturnArrayOfImageNames() throws Exception {
        String filename = "test-name";
        MockMultipartFile multipartFile = new MockMultipartFile("files", "test-image.png", "image/png", "some-image-content".getBytes());
        when(cloudService.uploadImage(multipartFile)).thenReturn(filename);

        mockMvc.perform(multipart("/img/upload")
        .file(multipartFile)
        .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isOk())
        .andExpect(content().string("[\"" + filename + "\"]"));
    }

    @Test
    void shouldUploadProfileImage() throws Exception {
        ResponseString responseString = new ResponseString("test-name");
        String oldFilename = "old-test-name";

        MockMultipartFile multipartFile = new MockMultipartFile("file", "test-image.png", "image/png", "some-image-content".getBytes());

        when(cloudService.uploadProfileImage(multipartFile, oldFilename)).thenReturn(responseString.getMessage());

        mockMvc.perform(multipart("/img/upload/profile")
                        .file(multipartFile)
                        .param("nameOldImage", oldFilename)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"message\":\"" + responseString.getMessage() + "\"}"));
    }

    @Test
    void shouldUploadResourceImage() throws Exception {
        String objectName = "resources/test-image";
        MockMultipartFile multipartFile = new MockMultipartFile("file", "test-image.png", "image/png", "some-image-content".getBytes());

        when(cloudService.uploadResourceImage(multipartFile)).thenReturn(objectName);

        mockMvc.perform(multipart("/img/upload/resource")
                        .file(multipartFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"message\":\"" + objectName + "\"}"));
    }


    @Test
    void shouldUpdateResourceImage() throws Exception {
        String newImagePath = "resources/new-test-image";
        String oldImageName = "resources/old-test-image";
        MockMultipartFile multipartFile = new MockMultipartFile("file", "test-image.png", "image/png", "some-image-content".getBytes());

        when(cloudService.updateResourceImage(multipartFile, oldImageName)).thenReturn(newImagePath);

        mockMvc.perform(multipart("/img/upload/resource")
                        .file(multipartFile)
                        .param("nameOldImage", oldImageName)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"message\":\"" + newImagePath + "\"}"));
    }

}
