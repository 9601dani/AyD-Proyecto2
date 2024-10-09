package com.bugtrackers.ms_auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(@NotBlank String usernameOrEmail, @NotBlank String password) {
    
}
