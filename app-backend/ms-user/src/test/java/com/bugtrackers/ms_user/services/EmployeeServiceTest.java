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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private AuthRestClient authClient;

    @Mock
    private UserHasRoleRepository userHasRoleRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee employee1;
    private Employee employee2;
    private CreateEmployeeRequest createEmployeeRequest;
    private User user;
    private Role role;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1);
        user.setUsername("juan_gonzalez");
        user.setEmail("juan.gonzalez@example.com");

        employee1 = new Employee();
        employee1.setId(1);
        employee1.setFirstName("Juan");
        employee1.setLastName("González");
        employee1.setDateOfBirth(LocalDate.of(1980, 1, 1));
        employee1.setUser(user);

        employee2 = new Employee();
        employee2.setId(2);
        employee2.setFirstName("Daniel");
        employee2.setLastName("Pérez");
        employee2.setDateOfBirth(LocalDate.of(1990, 2, 2));
        employee2.setUser(user);

        role = new Role();
        role.setId(1);
        role.setName("ROLE_USER");

        createEmployeeRequest = new CreateEmployeeRequest(
                "Juan",
                "González",
                LocalDate.of(1980, 1, 1),
                "juan.gonzalez@example.com",
                "juan_gonzalez",
                "password123",
                1
        );
    }

    @Test
    void testGetAllEmployees() {
        List<Employee> employees = Arrays.asList(employee1, employee2);
        when(employeeRepository.findAll()).thenReturn(employees);

        List<Employee> result = employeeService.getAllEmployees();

        assertEquals(2, result.size());
        assertEquals("Juan", result.get(0).getFirstName());
        assertEquals("Daniel", result.get(1).getFirstName());
    }

    @Test
    void testGetEmployeeById_Success() {
        when(employeeRepository.findById(1)).thenReturn(Optional.of(employee1));

        Employee result = employeeService.getEmployeeById(1);

        assertNotNull(result);
        assertEquals("Juan", result.getFirstName());
    }

    @Test
    void testGetEmployeeById_EmployeeNotFound() {
        when(employeeRepository.findById(1)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> employeeService.getEmployeeById(1));
        assertEquals("Empleado no encontrado!", exception.getMessage());
    }

    @Test
    void testCreateEmployee_Success() {
        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setUsername("juan_gonzalez");
        mockUser.setEmail("juan.gonzalez@example.com");
        mockUser.setAuthToken("token");
        mockUser.setIsActivated(true);
        mockUser.setIsVerified(true);
        mockUser.setIs2FA(false);
        mockUser.setCreatedAt(LocalDateTime.now());

        AuthResponse authResponse = new AuthResponse(mockUser);

        when(authClient.register(any(AuthRequest.class))).thenReturn(authResponse);

        when(roleRepository.findById(1)).thenReturn(Optional.of(role));

        when(employeeRepository.save(any(Employee.class))).thenReturn(employee1);

        Employee result = employeeService.createEmployee(createEmployeeRequest);

        assertNotNull(result);
        assertEquals("Juan", result.getFirstName());
        assertEquals("González", result.getLastName());
        assertEquals("juan.gonzalez@example.com", result.getUser().getEmail());
        assertEquals("juan_gonzalez", result.getUser().getUsername());

        verify(authClient, times(1)).register(any(AuthRequest.class));
        verify(userHasRoleRepository, times(1)).deleteAllById(Collections.singleton(user.getId()));
        verify(roleRepository, times(1)).findById(1);
        verify(userHasRoleRepository, times(1)).save(any(UserRole.class));
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }


    @Test
    void testCreateEmployee_RoleNotFound() {
        AuthResponse authResponse = new AuthResponse(1, "juan.gonzalez@example.com", "juan_gonzalez", "token", true, true, false, LocalDateTime.now());
        when(authClient.register(any(AuthRequest.class))).thenReturn(authResponse);
        when(roleRepository.findById(1)).thenReturn(Optional.empty());

        EmployeeNotFoundException exception = assertThrows(EmployeeNotFoundException.class, () -> employeeService.createEmployee(createEmployeeRequest));
        assertEquals("Rol no encontrado!", exception.getMessage());
        verify(authClient, times(1)).register(any(AuthRequest.class));
        verify(userHasRoleRepository, times(1)).deleteAllById(Collections.singleton(user.getId()));
        verify(userHasRoleRepository, times(0)).save(any(UserRole.class));
    }

    @Test
    void testUpdateEmployee_Success() {
        when(employeeRepository.findById(1)).thenReturn(Optional.of(employee1));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee1);

        Employee updatedEmployee = new Employee();
        updatedEmployee.setFirstName("Dulce");
        updatedEmployee.setLastName("González");
        updatedEmployee.setDateOfBirth(LocalDate.of(1980, 1, 1));

        Employee result = employeeService.updateEmployee(1, updatedEmployee);

        assertNotNull(result);
        assertEquals("Dulce", result.getFirstName());
        verify(employeeRepository, times(1)).findById(1);
        verify(employeeRepository, times(1)).save(employee1);
    }

    @Test
    void testUpdateEmployee_EmployeeNotFound() {
        when(employeeRepository.findById(1)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> employeeService.updateEmployee(1, employee1));
        assertEquals("Empleado no encontrado!", exception.getMessage());
        verify(employeeRepository, times(1)).findById(1);
        verify(employeeRepository, times(0)).save(any(Employee.class));
    }
}
