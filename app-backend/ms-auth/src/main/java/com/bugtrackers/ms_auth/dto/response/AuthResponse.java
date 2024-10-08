package com.bugtrackers.ms_auth.dto.response;

import java.time.LocalDateTime;
import com.bugtrackers.ms_auth.models.User;

public record AuthResponse(Integer id, String email, String username, String authToken, Boolean isActivated, Boolean isVerified, LocalDateTime createdAt) {
    public AuthResponse (User user){
        this(user.getId(), user.getEmail(), user.getUsername(), user.getAuthToken(), user.getIsActivated(), user.getIsVerified(), user.getCreatedAt());
    }
}