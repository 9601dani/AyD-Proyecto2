package com.bugtrackers.ms_user.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record CreateEmployeeRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank LocalDate birthOfDate,
        @NotBlank String email,
        @NotBlank String username,
        @NotBlank String password,
        @NotBlank Integer role
        ) {
}
