package com.bugtrackers.ms_email.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.bugtrackers.ms_email.dto.EmailRequest;
import com.bugtrackers.ms_email.services.EmailService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import java.util.HashMap;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@AllArgsConstructor
@RequestMapping("/email")
public class EmailController {
    
    private final EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<HashMap<String, String>> sendEmail(@RequestBody @Valid EmailRequest emailRequest) {

        String message = this.emailService.sendEmail(emailRequest);
        HashMap<String, String> response = new HashMap<>();
        response.put("message", message);
        return ResponseEntity.ok(response);
    }
    
}
