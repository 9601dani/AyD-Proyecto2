package com.bugtrackers.ms_auth.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.bugtrackers.ms_auth.config.GsonConfig;
import com.bugtrackers.ms_auth.dto.request.AuthRequest;
import com.bugtrackers.ms_auth.dto.request.LoginRequest;
import com.bugtrackers.ms_auth.dto.response.AuthResponse;
import com.bugtrackers.ms_auth.models.User;
import com.bugtrackers.ms_auth.services.AuthService;
import com.google.gson.Gson;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
	MockMvc mockMvc;

	@MockBean
	private AuthService authService;

    Gson gson;

    @BeforeEach
    void setUp() {
        gson = GsonConfig.createGsonWithLocalDateTimeAdapter();
    }
	@Test
	void testHelloWorld() throws Exception {
		mockMvc.perform(get("/auth"))
		.andExpect(status().isOk())
        .andExpect(content().string("Hello World Auth Controller!"));
	}

	@Test
    void testRegister() throws Exception {
		String authRequestJson = "{\"email\":\"email@example.com\", \"username\":\"username\", \"password\":\"password\"}";

        AuthRequest authRequest = new AuthRequest("email@example.com", "username", "password");
        User mockUser = new User("email@example.com", "username", "password");
        AuthResponse authResponse = new AuthResponse(mockUser);
        when(authService.register(authRequest)).thenReturn(authResponse);

        String expectedResponseJson = gson.toJson(authResponse);


        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(authRequestJson)) 
                .andExpect(status().isOk()) 
                .andExpect(content().json(expectedResponseJson)); 
	}

    @Test
    void testLoginByUsername() throws Exception {
		String authRequestJson = "{\"usernameOrEmail\":\"username\", \"password\":\"password\"}";

        LoginRequest loginRequest = new LoginRequest("username", "password");
        User mockUser = new User("email@example.com", "username", "password");
        AuthResponse authResponse = new AuthResponse(mockUser);

        when(authService.login(loginRequest)).thenReturn(authResponse);

        String expectedResponseJson = gson.toJson(authResponse);
        
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(authRequestJson))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponseJson));
    }

    @Test
    void shouldVerifyUser() throws Exception {
        String token = "token";
        when(this.authService.verifyEmail(token)).thenReturn("Usuario verificado exitosamente!");

        this.mockMvc.perform(put("/auth/verify-email/token"))
        .andExpect(status().isOk())
        .andExpect(content().json("{\"message\": \"Usuario verificado exitosamente!\"}"));
    }

    @Test
    void shouldSendEmailVerification() throws Exception {
        when(this.authService.sendVerification("username")).thenReturn("Correo enviado exitosamente!");

        this.mockMvc.perform(put("/auth/send-email/username"))
        .andExpect(status().isOk())
        .andExpect(content().json("{\"message\": \"Correo enviado exitosamente!\"}"));
    }

    @Test
    void shouldSend2FA() throws Exception {
        when(this.authService.send2FA(1)).thenReturn("Correo enviado exitosamente!");

        this.mockMvc.perform(post("/auth/send-2FA/1"))
        .andExpect(status().isOk())
        .andExpect(content().json("{\"message\": \"Correo enviado exitosamente!\"}"));
    }

    @Test
    void shouldVerify2FA() throws Exception {
        when(this.authService.verify2FA(1, "code")).thenReturn("Usuario autenticado exitosamente!");
        this.mockMvc.perform(put("/auth/verify-2FA/1/code"))
        .andExpect(status().isOk())
        .andExpect(content().json("{\"message\": \"Usuario autenticado exitosamente!\"}"));
    }

    @Test
    void shouldSendRecoveryPassword() throws Exception {
        when(this.authService.sendRecoveryPassword("email@example.com")).thenReturn("Correo enviado exitosamente!");
        this.mockMvc.perform(post("/auth/recovery-password/email@example.com"))
        .andExpect(status().isOk())
        .andExpect(content().json("{\"message\": \"Correo enviado exitosamente!\"}"));
    }
	
    @Test
    void shouldResetPassword() throws Exception {
        when(this.authService.resetPassword("token", "newpassword")).thenReturn("Contraseña actualizada exitosamente!");
        String requestJson = "{ \"password\": \"newpassword\" }";

        this.mockMvc.perform(put("/auth/reset-password/token")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestJson))
        .andExpect(status().isOk())
        .andExpect(content().json("{\"message\":\"Contraseña actualizada exitosamente!\"}"));

    }
}
