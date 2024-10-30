package com.bugtrackers.ms_user.dto.response;


import java.time.LocalDate;

import com.bugtrackers.ms_user.models.Employee;

public record CreateEmployeeResponse(
        Integer id,
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        String email,
        String username
) {

        public CreateEmployeeResponse(Employee employee) {
                this(employee.getId(), employee.getFirstName(), employee.getLastName(), employee.getDateOfBirth()
                , employee.getUser().getEmail(), employee.getUser().getUsername());
        }
}
