package com.bugtrackers.ms_user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EmailRequest(
    @NotBlank String to,
    @NotBlank String subject,
    @NotBlank String content,
    @NotNull Boolean createPDF
) {
    
}
