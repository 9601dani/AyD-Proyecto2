package com.bugtrackers.ms_user.controllers;

import com.bugtrackers.ms_user.config.GsonConfig;
import com.bugtrackers.ms_user.dto.request.ServiceRequest;
import com.bugtrackers.ms_user.dto.response.CreateEmployeeResponse;
import com.bugtrackers.ms_user.dto.response.EmployeeResponse;
import com.bugtrackers.ms_user.dto.response.ResourceResponse;
import com.bugtrackers.ms_user.dto.response.ServiceResponse;
import com.bugtrackers.ms_user.models.Employee;
import com.bugtrackers.ms_user.models.Resource;
import com.bugtrackers.ms_user.models.Service;
import com.bugtrackers.ms_user.models.User;
import com.bugtrackers.ms_user.models.UserInformation;
import com.bugtrackers.ms_user.services.ServiceService;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ServiceController.class)
public class ServicesControllerTest {

    @MockBean
    private ServiceService serviceService;

    private Gson gson;
    private List<Service> mockServices;

    @Autowired
    private MockMvc mockMvc;

    private Service mockService;

    @BeforeEach
    void setUp() {
        gson = GsonConfig.createGsonWithAdapter();
        mockServices = List.of(
                new Service(1,"name", "description", new BigDecimal(1.0), "pageInformation", 1, true, LocalDateTime.now(), List.of(), List.of()),
                new Service(2,"name2", "description2", new BigDecimal(2.0), "pageInformation2", 2, false, LocalDateTime.now(), List.of(), List.of())
        );
        mockService = new Service(1, "name", "description", new BigDecimal(1.0), "pageInformation", 1, true, LocalDateTime.now(), List.of(), List.of());
    }

    @Test
    void testGetServices() throws Exception {
        when(serviceService.getAllServices()).thenReturn(mockServices.stream().map(ServiceResponse::new).toList());

        mockMvc.perform(get("/services"))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(mockServices.stream().map(ServiceResponse::new).toList())));
    }

    @Test
    void testGetServiceById() throws Exception {
        List<CreateEmployeeResponse> mockEmployees = List.of(
                new CreateEmployeeResponse(1, "Juan", "Perez", LocalDate.of(1990, 5, 15), "juan.perez@example.com", "juanperez"),
                new CreateEmployeeResponse(2, "Ana", "Gomez", LocalDate.of(1985, 11, 20), "ana.gomez@example.com", "anagomez")
        );

        List<ResourceResponse> mockResources = List.of(
                new ResourceResponse(1, "Recurso 1","img1", List.of()),
                new ResourceResponse(2, "Recurso 2","img1", List.of())
        );

        ServiceResponse serviceResponse = new ServiceResponse(
                1, "name", "description", new BigDecimal(1.0), "pageInformation", 1, true,
                mockEmployees, mockResources
        );

        when(serviceService.getServiceById(1)).thenReturn(serviceResponse);

        mockMvc.perform(get("/services/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(serviceResponse)));
    }

    @Test
    void testCreateService() throws Exception {
        ServiceRequest serviceRequest = new ServiceRequest(
                "name3",
                "description3",
                new BigDecimal(3.0),
                "pageInformation3",
                3,
                true,
                List.of(),
                List.of()
        );

        Service serviceCreated = new Service(
                3, "name3", "description3", new BigDecimal(3.0), "pageInformation3", 3, true, LocalDateTime.now(), List.of(), List.of());

        when(serviceService.saveService(any(ServiceRequest.class))).thenReturn(serviceCreated);

        String expectedJson = gson.toJson(serviceCreated);

        mockMvc.perform(post("/services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(serviceRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    void testUpdateService() throws Exception {
        ServiceRequest serviceRequest = new ServiceRequest(
                "Updated Service",
                "Updated description",
                new BigDecimal(2.0),
                "Updated pageInformation",
                2,
                false,
                List.of(),
                List.of()
        );

        ServiceResponse serviceResponse = new ServiceResponse(
                1,
                "Updated Service",
                "Updated description",
                new BigDecimal(2.0),
                "Updated pageInformation",
                2,
                false,
                List.of(),
                List.of()
        );

        when(serviceService.updateService(eq(mockService.getId()), any(ServiceRequest.class)))
                .thenReturn(serviceResponse);

        mockMvc.perform(put("/services/{id}", mockService.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(serviceRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(serviceResponse)));

        verify(serviceService, times(1)).updateService(eq(mockService.getId()), any(ServiceRequest.class));
    }

    @Test
    void shouldFindResourcesById() throws Exception {
        List<Integer> ids = List.of(1, 2);

        List<Resource> resources = List.of(
                new Resource(1, "name", "image", List.of(), List.of()),
                new Resource(2, "name", "image", List.of(), List.of())
        );

        when(this.serviceService.getResourcesByIds(ids)).thenReturn(resources.stream().map(ResourceResponse::new).toList());
        
        this.mockMvc.perform(get("/services/reserve/resources").param("ids", "1,2"))
        .andExpect(status().isOk())
        .andExpect(content().json(gson.toJson(resources.stream().map(ResourceResponse::new).toList())));
    }

    @Test
    void shouldFindEmployeesById() throws Exception {
        List<Integer> ids = List.of(1, 2);

        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setUsername("username");
        mockUser.setEmail("email");
        mockUser.setUserInformation(new UserInformation("nit", "profile", "description", null, "dpi", "phone"));

        List<Employee> employees = List.of(
                new Employee(1, "name", "lastname", LocalDate.now(), mockUser, List.of()),
                new Employee(2, "name", "lastname", LocalDate.now(), mockUser, List.of())
        );

        when(this.serviceService.getEmployeesByIds(ids)).thenReturn(employees.stream().map(EmployeeResponse::new).toList());
        
        this.mockMvc.perform(get("/services/reserve/employees").param("ids", "1,2"))
        .andExpect(status().isOk())
        .andExpect(content().json(gson.toJson(employees.stream().map(EmployeeResponse::new).toList())));
    }
}
