package com.bugtrackers.ms_auth.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bugtrackers.ms_auth.dto.request.AuthRequest;
import com.bugtrackers.ms_auth.dto.response.AuthResponse;
import com.bugtrackers.ms_auth.models.User;
import com.bugtrackers.ms_auth.services.AuthService;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService _authService;
    
    
    @GetMapping("")
    public ResponseEntity<String> helloWorld() {
        return ResponseEntity.ok().body("Hello World Auth Controller!");
    }

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid AuthRequest user) {
        User user1 = this._authService.register(user);
        AuthResponse authResponse = new AuthResponse(
            user1.getId(),
            user1.getEmail(),
            user1.getUsername(),
            user1.getAuthToken(),
            user1.getIsActivated(),
            user1.getIsVerified(),
            user1.getCreatedAt()
        );

        return ResponseEntity.ok(authResponse);
    }
    
}
