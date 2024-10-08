package com.bugtrackers.ms_auth.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.bugtrackers.ms_auth.dto.request.AuthRequest;
import com.bugtrackers.ms_auth.dto.response.AuthResponse;
import com.bugtrackers.ms_auth.models.User;
import com.bugtrackers.ms_auth.services.AuthService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import java.time.LocalDateTime;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
	MockMvc mockMvc;

	@MockBean
	private AuthService authService;

	@Test
	void testHelloWorld() throws Exception {
		mockMvc.perform(get("/auth"))
		.andExpect(status().isOk())
        .andExpect(content().string("Hello World Auth Controller!"));
	}

	@Test
    public void testRegister() throws Exception {
		String authRequestJson = "{\"email\":\"email@example.com\", \"username\":\"username\", \"password\":\"password\"}";

        AuthRequest authRequest = new AuthRequest("email@example.com", "username", "password");
        User mockUser = new User("email@example.com", "username", "password");
        when(authService.register(authRequest)).thenReturn(mockUser);

        String expectedResponseJson = String.format(
                "{\"id\":%d,\"email\":\"%s\",\"username\":\"%s\",\"isActivated\":%s,\"isVerified\":%s,\"createdAt\":\"%s\"}",
                mockUser.getId(),
                mockUser.getEmail(),
                mockUser.getUsername(),
                Boolean.toString(mockUser.getIsActivated()),
                Boolean.toString(mockUser.getIsVerified()),
                mockUser.getCreatedAt().toString() 
        );

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(authRequestJson)) 
                .andExpect(status().isOk()) 
                .andExpect(content().json(expectedResponseJson)); 
	}
	
}
