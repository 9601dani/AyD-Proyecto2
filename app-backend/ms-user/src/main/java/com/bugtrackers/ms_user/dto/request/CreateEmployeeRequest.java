package com.bugtrackers.ms_user.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;


public record CreateEmployeeRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank LocalDate dateOfBirth,
        @NotBlank String email,
        @NotBlank String username,
        @NotBlank String password,
        @NotBlank Integer role
        ) {

        @Override
        public String toString() {
                return String.format("CreateEmployeeRequest[firstName='%s', lastName='%s', dateOfBirth='%s', email='%s', username='%s', password='%s', role='%s']", firstName, lastName, dateOfBirth, email, username, password, role);
        }


}
