package com.bugtrackers.ms_auth.dto.request;
import jakarta.validation.constraints.NotBlank;

public record AuthRequest(@NotBlank String email, @NotBlank String username, @NotBlank String password) {
}