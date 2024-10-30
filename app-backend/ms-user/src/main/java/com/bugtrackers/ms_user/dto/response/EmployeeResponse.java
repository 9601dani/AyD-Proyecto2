package com.bugtrackers.ms_user.dto.response;

import java.time.LocalDate;

import java.util.List;

import com.bugtrackers.ms_user.models.Employee;
import com.bugtrackers.ms_user.models.Role;


public record EmployeeResponse(
    Integer id,
    String firstName,
    String lastName,
    LocalDate dateOfBirth,
    String email,
    String username,
    String imageProfile,
    List<String> roles) {

        public EmployeeResponse(Employee employee) {
            this(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getDateOfBirth(),
                employee.getUser().getEmail(),
                employee.getUser().getUsername(),
                employee.getUser().getUserInformation().getImageProfile(),
                employee.getUser().getRoles().stream().map(Role::getName).toList()
            );
        }
}
