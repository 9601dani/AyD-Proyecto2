package com.bugtrackers.ms_user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RoleRequest(
    @NotBlank String name,
    @NotBlank String description
) {
    
}
