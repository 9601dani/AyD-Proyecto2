package com.bugtrackers.ms_user.controllers;

import com.bugtrackers.ms_user.dto.request.CreateEmployeeRequest;
import com.bugtrackers.ms_user.dto.response.CreateEmployeeResponse;
import com.bugtrackers.ms_user.models.Employee;
import com.bugtrackers.ms_user.models.User;
import com.bugtrackers.ms_user.services.EmployeeService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/employee")
@AllArgsConstructor

public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping("")
    public ResponseEntity<List<CreateEmployeeResponse>> getAllEmployees() {
        List<Employee> employees = this.employeeService.getAllEmployees();

        List<CreateEmployeeResponse> response = employees.stream().map(employee -> new CreateEmployeeResponse(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getDateOfBirth(),
                employee.getUser().getEmail(),
                employee.getUser().getUsername()
        )).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PostMapping("")
    @Transactional
    public ResponseEntity<CreateEmployeeResponse> createEmployee(@RequestBody CreateEmployeeRequest employeeRequest) {
      Employee employee = this.employeeService.createEmployee(employeeRequest);

      CreateEmployeeResponse response= new CreateEmployeeResponse(
        employee.getId(),
        employee.getFirstName(),
        employee.getLastName(),
        employee.getDateOfBirth(),
        employee.getUser().getEmail(),
        employee.getUser().getUsername()
      );

      return ResponseEntity.ok(response);
    }



}
