package com.bugtrackers.ms_user.controllers;

import com.bugtrackers.ms_user.dto.response.CreateEmployeeResponse;
import com.bugtrackers.ms_user.models.Employee;
import com.bugtrackers.ms_user.models.User;
import com.bugtrackers.ms_user.services.EmployeeService;
import com.bugtrackers.ms_user.config.GsonConfig;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployeeController.class)
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    private List<Employee> mockEmployees;
    private Gson gson;

    @BeforeEach
    void setUp() {
        gson = GsonConfig.createGsonWithLocalDateTimeAdapter();  // Usa el Gson con adaptadores para fechas

        User user1 = new User(1, "user1@example.com", "user1", "password", "token", true, true, false, LocalDate.now().atStartOfDay(), null);
        User user2 = new User(2, "user2@example.com", "user2", "password", "token", true, true, false, LocalDate.now().atStartOfDay(), null);

        mockEmployees = List.of(
                new Employee(1, "John", "Doe", LocalDate.of(1990, 1, 1), user1),
                new Employee(2, "Jane", "Smith", LocalDate.of(1985, 5, 15), user2)
        );
    }

    @Test
    void testGetAllEmployees() throws Exception {
        when(employeeService.getAllEmployees()).thenReturn(mockEmployees);

        List<CreateEmployeeResponse> expectedResponse = mockEmployees.stream()
                .map(employee -> new CreateEmployeeResponse(
                        employee.getId(),
                        employee.getFirstName(),
                        employee.getLastName(),
                        employee.getDateOfBirth(),
                        employee.getUser().getEmail(),
                        employee.getUser().getUsername()
                ))
                .toList();

        String expectedJson = gson.toJson(expectedResponse);

        mockMvc.perform(get("/employee"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }
}
