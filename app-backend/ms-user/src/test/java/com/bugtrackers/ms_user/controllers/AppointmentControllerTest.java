package com.bugtrackers.ms_user.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.bugtrackers.ms_user.dto.response.BillReportResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.bugtrackers.ms_user.config.GsonConfig;
import com.bugtrackers.ms_user.dto.request.AppointmentRequest;
import com.bugtrackers.ms_user.dto.response.AppointmentResponse;
import com.bugtrackers.ms_user.models.Appointment;
import com.bugtrackers.ms_user.models.User;
import com.bugtrackers.ms_user.models.UserInformation;
import com.bugtrackers.ms_user.services.AppointmentService;
import com.google.gson.Gson;

@WebMvcTest(AppointmentController.class)
public class AppointmentControllerTest {

    @MockBean
    private AppointmentService appointmentService;

    @Autowired
    private MockMvc mockMvc;
    Gson gson;

    @BeforeEach
    void setUp() {
        this.gson = GsonConfig.createGsonWithAdapter();
    }

    @Test
    void testFindByResourceOrEmployee() throws Exception {
        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setUsername("username");
        mockUser.setEmail("email");
        mockUser.setUserInformation(new UserInformation("nit", "profile", "description", null, "dpi", "phone"));

        Appointment appointment = new Appointment(1, mockUser, null, BigDecimal.ZERO, "PENDING", LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), null, List.of());
        AppointmentResponse response = new AppointmentResponse(appointment);

        when(this.appointmentService.findByResourceOrEmployee(1, 1)).thenReturn(List.of(response));

        this.mockMvc.perform(get("/appointment/find")
        .param("resource", "1")
        .param("employee", "1"))
        .andExpect(status().isOk())
        .andExpect(content().json(gson.toJson(List.of(response))));
    }

    @Test
    void testSaveAppointment() throws Exception {
        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setUsername("username");
        mockUser.setEmail("email");
        mockUser.setUserInformation(new UserInformation("nit", "profile", "description", null, "dpi", "phone"));

        Appointment appointment = new Appointment(1, mockUser, null, BigDecimal.ZERO, "PENDING", LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), null, List.of());
        AppointmentResponse response = new AppointmentResponse(appointment);

        AppointmentRequest request = new AppointmentRequest(1, null, "2024-02-27T18:14:01.184", "2024-02-27T19:14:01.184", null, List.of(1, 2));

        when(this.appointmentService.save(request)).thenReturn(response);

        this.mockMvc.perform(post("/appointment/save")
        .contentType(MediaType.APPLICATION_JSON)
        .content(gson.toJson(request)))
        .andExpect(status().isOk())
        .andExpect(content().json(gson.toJson(response)));
    }

    @Test
    void testFindAll() throws Exception {
        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setUsername("username");
        mockUser.setEmail("email");
        mockUser.setUserInformation(new UserInformation("nit", "profile", "description", null, "dpi", "phone"));


        Appointment appointment = new Appointment(1, mockUser, null, BigDecimal.ZERO, "PENDING", LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), null, List.of());
        AppointmentResponse response = new AppointmentResponse(appointment);

        when(this.appointmentService.findAll()).thenReturn(List.of(response));

        this.mockMvc.perform(get("/appointment"))
        .andExpect(status().isOk())
        .andExpect(content().json(gson.toJson(List.of(response))));

    }

    @Test
    void testGetBill() throws Exception {
        BillReportResponse response = new BillReportResponse("name", "nit", "address", 100.0, 50.0, LocalDateTime.now());

        when(this.appointmentService.getBill()).thenReturn(List.of(response));

        this.mockMvc.perform(get("/appointment/bill"))
        .andExpect(status().isOk())
        .andExpect(content().json(gson.toJson(List.of(response))));
    }

    @Test
    void shouldUpdateState() throws Exception {
        Integer id = 1;
        String state = "COMPLETED";
        String expectedMessage = "Cita actualizada con éxito!";
        
        when(appointmentService.updateAppointmentState(id, state)).thenReturn(expectedMessage);

        mockMvc.perform(put("/appointment/update-state/{id}", id)
                .contentType("application/json")
                .content(state))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"message\": \"Cita actualizada con éxito!\"}"));
    }

    @Test
    void shouldFindByEmployeeId() throws Exception {
        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setUsername("username");
        mockUser.setEmail("email");
        mockUser.setUserInformation(new UserInformation("nit", "profile", "description", null, "dpi", "phone"));

        Appointment appointment = new Appointment(1, mockUser, null, BigDecimal.ZERO, "PENDING", LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), null, List.of());
        AppointmentResponse response = new AppointmentResponse(appointment);

        when(this.appointmentService.findAppointmentsByEmployee(1)).thenReturn(List.of(response));

        this.mockMvc.perform(get("/appointment/employee/{id}", 1))
        .andExpect(status().isOk())
        .andExpect(content().json(gson.toJson(List.of(response))));
    }

    @Test
    void shouldFindBillByAppointmentId() throws Exception {
        BillReportResponse response = new BillReportResponse("name", "nit", "address", 100.0, 50.0, LocalDateTime.now());

        when(this.appointmentService.findBillByAppointmentId(1)).thenReturn(response);

        this.mockMvc.perform(get("/appointment/bill/{id}", 1))
        .andExpect(status().isOk())
        .andExpect(content().json(gson.toJson(response)));
    }
}
