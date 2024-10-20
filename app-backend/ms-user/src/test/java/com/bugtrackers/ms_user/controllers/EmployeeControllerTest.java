package com.bugtrackers.ms_user.controllers;

import com.bugtrackers.ms_user.config.GsonConfig;
import com.bugtrackers.ms_user.dto.request.CreateEmployeeRequest;
import com.bugtrackers.ms_user.dto.response.CreateEmployeeResponse;
import com.bugtrackers.ms_user.models.Employee;
import com.bugtrackers.ms_user.models.User;
import com.bugtrackers.ms_user.services.EmployeeService;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    private List<Employee> mockEmployees;
    private Gson gson;
    private CreateEmployeeRequest createEmployeeRequest;
    private Employee mockEmployee;

    @BeforeEach
    void setUp() {
        User user1 = new User(1, "email1@test.com", "user1", "password1", "authToken1", true, true, false, LocalDateTime.now());
        User user2 = new User(2, "email2@test.com", "user2", "password2", "authToken2", false, false, true, LocalDateTime.now());

        Employee employee1 = new Employee(1, "John", "Doe", LocalDate.of(1985, 5, 10), user1);
        Employee employee2 = new Employee(2, "Jane", "Doe", LocalDate.of(1990, 3, 25), user2);

        mockEmployees = List.of(employee1, employee2);
        gson = GsonConfig.createGsonWithAdapter();

        createEmployeeRequest = new CreateEmployeeRequest(
                "John",
                "Doe",
                LocalDate.of(1985, 5, 10),
                "john.doe@test.com",
                "user1",
                "password1",
                1
        );

        mockEmployee = new Employee(1, "John", "Doe", LocalDate.of(1985, 5, 10), user1);
    }

    @Test
    void testGetAllEmployees() throws Exception {
        when(employeeService.getAllEmployees()).thenReturn(mockEmployees);

        List<CreateEmployeeResponse> mockResponse = mockEmployees.stream().map(employee -> new CreateEmployeeResponse(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getDateOfBirth(),
                employee.getUser().getEmail(),
                employee.getUser().getUsername()
        )).toList();

        String jsonResponse = gson.toJson(mockResponse);

        mockMvc.perform(get("/employee"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(jsonResponse));
    }

    @Test
    void testCreateEmployee() throws Exception {
        when(employeeService.createEmployee(createEmployeeRequest)).thenReturn(mockEmployee);

        CreateEmployeeResponse expectedResponse = new CreateEmployeeResponse(
                mockEmployee.getId(),
                mockEmployee.getFirstName(),
                mockEmployee.getLastName(),
                mockEmployee.getDateOfBirth(),
                mockEmployee.getUser().getEmail(),
                mockEmployee.getUser().getUsername()
        );

        String requestJson = gson.toJson(createEmployeeRequest);
        String responseJson = gson.toJson(expectedResponse);

        mockMvc.perform(post("/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(responseJson));
    }
}
