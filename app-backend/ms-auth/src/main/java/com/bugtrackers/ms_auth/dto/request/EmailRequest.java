package com.bugtrackers.ms_auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record EmailRequest(
    @NotBlank String to,
    @NotBlank String subject,
    @NotBlank String content
) {
    
}
