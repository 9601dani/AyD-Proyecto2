package com.bugtrackers.ms_user.dto;

import com.bugtrackers.ms_user.dto.request.ImageUpdateRequest;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ImageUpdateRequestTest {

    @Test
    void testGetPathImg() {
        String expectedPathImg = "images/profile.png";
        ImageUpdateRequest request = new ImageUpdateRequest(expectedPathImg);

        assertEquals(expectedPathImg, request.getPathImg());
    }

    @Test
    void testConstructor() {
        String pathImg = "images/profile.png";
        ImageUpdateRequest request = new ImageUpdateRequest(pathImg);

        assertNotNull(request);
        assertEquals(pathImg, request.getPathImg());
    }
}
