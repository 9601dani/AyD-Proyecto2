package com.bugtrackers.ms_user.dto.response;

import java.time.LocalDateTime;

public record CommentResponse(
        Integer id,
        String username,
        String comment,
        Integer value,
        LocalDateTime createdAt
) {
}
