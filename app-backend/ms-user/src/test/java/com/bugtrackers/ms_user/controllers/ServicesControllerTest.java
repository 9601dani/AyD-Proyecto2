package com.bugtrackers.ms_user.controllers;

import com.bugtrackers.ms_user.config.GsonConfig;
import com.bugtrackers.ms_user.models.Service;
import com.bugtrackers.ms_user.services.ServiceService;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@WebMvcTest(ServicesController.class)
public class ServicesControllerTest {

    @MockBean
    private ServiceService serviceService;

    Gson gson;
    private List<Service> mockServices;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        gson = GsonConfig.createGsonWithAdapter();
        mockServices = List.of(
            new Service(1,"name", "description", new BigDecimal(1.0), "pageInformation", 1, true, LocalDateTime.now()),
            new Service(2,"name2", "description2", new BigDecimal(2.0), "pageInformation2", 2, false,LocalDateTime.now())
        );
    }

   @Test
    void testGetServices() throws Exception {
        when(serviceService.getAllServices()).thenReturn(mockServices);

        mockMvc.perform(get("/services"))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(mockServices)));
    }


    @Test
    void testGetServiceById() throws Exception {
        Service service = mockServices.get(0);
        when(serviceService.getServiceById(1)).thenReturn(service);

        mockMvc.perform(get("/services/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(service)));
    }

    @Test
    void testCreateService() throws Exception {
        Service serviceCreated = new Service(
                3,"name3", "description3", new BigDecimal(3.0), "pageInformation3", 3, true, LocalDateTime.now());
        when(serviceService.saveService(serviceCreated)).thenReturn(serviceCreated);

        String expectedJson = gson.toJson(serviceCreated);

        System.out.println("Expected: " + expectedJson);
        System.out.println("Actual: " + gson.toJson(serviceCreated));
        mockMvc.perform(post("/services")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(serviceCreated)))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateService() throws Exception {
        Service serviceToUpdate = new Service(
                1, "nameUpdate", "descriptionUpdate", new BigDecimal(1.0), "pageInformationUpdate", 1, true, LocalDateTime.now());

        when(serviceService.updateService(eq(1), any(Service.class))).thenReturn(serviceToUpdate);

        String serviceJson = gson.toJson(serviceToUpdate);

        mockMvc.perform(put("/services/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serviceJson))
                .andExpect(status().isOk())
                .andExpect(content().json(serviceJson));
    }







}
