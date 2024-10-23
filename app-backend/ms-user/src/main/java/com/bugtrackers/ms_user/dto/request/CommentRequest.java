package com.bugtrackers.ms_user.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

public record CommentRequest(
        @NotBlank Integer FK_User,
        @NotBlank String comment,
        @NotBlank Integer value,
        @NotBlank LocalDateTime createdAt
) {
}
