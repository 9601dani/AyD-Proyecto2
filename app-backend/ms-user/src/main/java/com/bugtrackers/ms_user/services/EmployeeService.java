package com.bugtrackers.ms_user.services;

import com.bugtrackers.ms_user.clients.AuthRestClient;
import com.bugtrackers.ms_user.dto.request.AuthRequest;
import com.bugtrackers.ms_user.dto.request.CreateEmployeeRequest;
import com.bugtrackers.ms_user.dto.response.AuthResponse;
import com.bugtrackers.ms_user.exceptions.EmployeeNotFoundException;
import com.bugtrackers.ms_user.models.Employee;
import com.bugtrackers.ms_user.models.Role;
import com.bugtrackers.ms_user.models.User;
import com.bugtrackers.ms_user.models.UserRole;
import com.bugtrackers.ms_user.repositories.EmployeeRepository;
import com.bugtrackers.ms_user.repositories.RoleRepository;
import com.bugtrackers.ms_user.repositories.UserHasRoleRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final AuthRestClient authClient;
    private final UserHasRoleRepository userHasRoleRepository;
    private final RoleRepository roleRepository;

    public List<Employee> getAllEmployees() {
        return this.employeeRepository.findAll();
    }

    public Employee getEmployeeById(Integer id) {
        Optional<Employee> optional = this.employeeRepository.findById(id);

        if (optional.isEmpty()) {
            throw new RuntimeException("Empleado no encontrado!");
        }

        Employee employee = optional.get();
        return employee;
    }

    public Employee createEmployee(CreateEmployeeRequest employee) {
        AuthRequest authRequest = new AuthRequest(employee.email(), employee.username(), employee.password());
        AuthResponse authResponse = this.authClient.register(authRequest);

        User user = new User();
        user.setId(authResponse.id());
        user.setEmail(authResponse.email());
        user.setUsername(authResponse.username());

        Employee employee1 = new Employee();
        employee1.setFirstName(employee.firstName());
        employee1.setLastName(employee.lastName());
        employee1.setDateOfBirth(employee.dateOfBirth());
        employee1.setUser(user);


        this.userHasRoleRepository.deleteAllById(Collections.singleton(user.getId()));

        Optional<Role> role = this.roleRepository.findById(employee.role());

        if (role.isEmpty()) {
            throw new EmployeeNotFoundException("Rol no encontrado!");
        }

        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(role.get());

        this.userHasRoleRepository.save(userRole);
        return this.employeeRepository.save(employee1);
    }

    public Employee updateEmployee(Integer id, Employee employee) {
        Optional<Employee> optional = this.employeeRepository.findById(id);

        if (optional.isEmpty()) {
            throw new RuntimeException("Empleado no encontrado!");
        }

        Employee employeeUpdate = optional.get();
        employeeUpdate.setFirstName(employee.getFirstName());
        employeeUpdate.setLastName(employee.getLastName());
        employeeUpdate.setDateOfBirth(employee.getDateOfBirth());

        return this.employeeRepository.save(employeeUpdate);
    }
}
