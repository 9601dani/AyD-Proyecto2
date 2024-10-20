package com.bugtrackers.ms_user.dto.response;

import com.bugtrackers.ms_user.models.User;

import java.time.LocalDateTime;

public record AuthResponse(Integer id, String email, String username, String token, Boolean isActivated, Boolean isVerified, Boolean is2FA, LocalDateTime createdAt) {
    public AuthResponse (User user){
        this(user.getId(), user.getEmail(), user.getUsername(), user.getAuthToken(), user.getIsActivated(), user.getIsVerified(), user.getIs2FA(), user.getCreatedAt());
    }
}
