package com.bugtrackers.ms_email.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.bugtrackers.ms_email.dto.EmailBody;
import com.bugtrackers.ms_email.services.EmailService;

import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@AllArgsConstructor
@RequestMapping("/email")
public class EmailController {
    
    private final EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestBody EmailBody emailBody) {

        this.emailService.sendEmail(emailBody);
        return ResponseEntity.ok("El correo se envió exitosamente!");
    }
    
}
