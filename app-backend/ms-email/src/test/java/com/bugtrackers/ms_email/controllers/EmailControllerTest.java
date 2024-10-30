package com.bugtrackers.ms_email.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.bugtrackers.ms_email.config.GsonConfig;
import com.bugtrackers.ms_email.dto.EmailRequest;
import com.bugtrackers.ms_email.services.EmailService;
import com.google.gson.Gson;

@WebMvcTest(EmailController.class)
public class EmailControllerTest {

    @MockBean
    private EmailService emailService;

    @Autowired
    private MockMvc mockMvc;
    private Gson gson;

    @BeforeEach
    void setUp() {
        gson = GsonConfig.createGsonWithLocalDateTimeAdapter();
    }

    

    @Test
    void shouldSendEmail() throws Exception {
        
        EmailRequest emailRequest = new EmailRequest("to@test.com", "Subject", "content", false);
        String requestJson = gson.toJson(emailRequest);

        when(this.emailService.sendEmail(emailRequest)).thenReturn("El correo se envió exitosamente!");

        mockMvc.perform(post("/email/send")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestJson))
        .andExpect(status().isOk())
        .andExpect(content().json("{\"message\": \"El correo se envió exitosamente!\"}"));
    }
}
