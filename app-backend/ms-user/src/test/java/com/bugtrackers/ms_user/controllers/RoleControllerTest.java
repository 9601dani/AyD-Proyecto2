package com.bugtrackers.ms_user.controllers;

import com.bugtrackers.ms_user.config.GsonConfig;
import com.bugtrackers.ms_user.dto.request.RoleRequest;
import com.bugtrackers.ms_user.dto.response.RoleResponse;
import com.bugtrackers.ms_user.models.Role;
import com.bugtrackers.ms_user.services.RoleService;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoleController.class)
public class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoleService roleService;

    private List<Role> mockRoles;
    private Gson gson;

    @BeforeEach
    void setUp() {
        Role role1 = new Role(1, "Admin", "Administrator role", LocalDateTime.now(), List.of(), List.of());
        Role role2 = new Role(2, "User", "Standard user role", LocalDateTime.now(), List.of(), List.of());
        mockRoles = List.of(role1, role2);

        gson = GsonConfig.createGsonWithAdapter();
    }

    @Test
    void testGetRoles() throws Exception {
        when(roleService.getRoles()).thenReturn(mockRoles.stream().map(RoleResponse::new).toList());

        String jsonResponse = gson.toJson(mockRoles.stream().map(RoleResponse::new).toList());

        mockMvc.perform(get("/role"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(jsonResponse));
    }

    @Test
    void testSaveRole() throws Exception {
        Role role = new Role(1, "Admin", "Administrator role", LocalDateTime.now(), List.of(), List.of());
        when(roleService.saveRole(any(RoleRequest.class))).thenReturn(new RoleResponse(role));
        String roleJson = gson.toJson(new RoleResponse(role));

        mockMvc.perform(post("/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(roleJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(roleJson));
    }
}
