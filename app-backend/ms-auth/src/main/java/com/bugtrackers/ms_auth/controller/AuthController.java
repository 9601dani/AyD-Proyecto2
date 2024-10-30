package com.bugtrackers.ms_auth.controller;

import java.util.HashMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bugtrackers.ms_auth.dto.request.AuthRequest;
import com.bugtrackers.ms_auth.dto.request.LoginRequest;
import com.bugtrackers.ms_auth.dto.response.AuthResponse;
import com.bugtrackers.ms_auth.exceptions.EmailVerificationExpiredException;
import com.bugtrackers.ms_auth.services.AuthService;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;
    
    
    @GetMapping("")
    public ResponseEntity<String> helloWorld() {
        return ResponseEntity.ok().body("Hello World Auth Controller!");
    }

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid AuthRequest user) {
        AuthResponse authResponse = this.authService.register(user);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/login")
    @Transactional
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        AuthResponse response = this.authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PutMapping("verify-email/{token}")
    @Transactional(dontRollbackOn = EmailVerificationExpiredException.class)
    public ResponseEntity<HashMap<String, String>> verifyEmail(@PathVariable String token) {
        String message = this.authService.verifyEmail(token);
        HashMap<String, String> response = new HashMap<>();
        response.put("message", message);
        return ResponseEntity.ok(response);
    }

    @PutMapping("send-email/{emailOrUsername}")
    @Transactional
    public ResponseEntity<HashMap<String, String>> sendEmailVerification(@PathVariable String emailOrUsername) {
        String message = this.authService.sendVerification(emailOrUsername);
        HashMap<String, String> response = new HashMap<>();
        response.put("message", message);
        return ResponseEntity.ok(response);
    }

    @PostMapping("send-2FA/{id}")
    public ResponseEntity<HashMap<String, String>> send2FA(@PathVariable Integer id) {
        String message = this.authService.send2FA(id);
        HashMap<String, String> response = new HashMap<>();
        response.put("message", message);
        return ResponseEntity.ok(response);
    }

    @PutMapping("verify-2FA/{id}/{code}")
    public ResponseEntity<HashMap<String, String>> verify2FA(@PathVariable Integer id, @PathVariable String code) {
        String message = this.authService.verify2FA(id, code);
        HashMap<String, String> response = new HashMap<>();
        response.put("message", message);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("recovery-password/{email}")
    public ResponseEntity<HashMap<String, String>> sendRecoveryPassword(@PathVariable String email) {
        String message = this.authService.sendRecoveryPassword(email);
        HashMap<String, String> response = new HashMap<>();
        response.put("message", message);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("reset-password/{token}")
    public ResponseEntity<HashMap<String, String>> resetPassword(@PathVariable String token, @RequestBody HashMap<String, String> body) {
        String password = body.get("password");
        String message = this.authService.resetPassword(token, password);
        HashMap<String, String> response = new HashMap<>();
        response.put("message", message);
        return ResponseEntity.ok(response);
    }
    
}
