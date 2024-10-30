package com.bugtrackers.ms_user.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record CommentRequest(
        @NotBlank Integer FK_User,
        @NotBlank String comment,
        @NotBlank Integer value,
        @NotBlank LocalDateTime createdAt
) {
}
