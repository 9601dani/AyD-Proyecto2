package com.bugtrackers.ms_user.dto.response;

public record UserAllResponse(
    String email,
    String username,
    String nit,
    String imageProfile,
    String description,
    String dpi,
    String phoneNumber
) {
}
