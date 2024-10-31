package com.bugtrackers.ms_user.controllers;

import com.bugtrackers.ms_user.config.GsonConfig;
import com.bugtrackers.ms_user.dto.request.PageRequest;
import com.bugtrackers.ms_user.dto.request.RoleRequest;
import com.bugtrackers.ms_user.dto.response.PageResponse;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

    @Test
    void shouldGetAllPages() throws Exception {
        List<PageResponse> response = List.of(
            new PageResponse(1, "name", "path", true),
            new PageResponse(2, "name", "path", true),
            new PageResponse(3, "name", "path", true),
            new PageResponse(4, "name", "path", true)
        );

        when(this.roleService.getAllPages()).thenReturn(response);

        this.mockMvc.perform(get("/role/find-pages/all"))
        .andExpect(status().isOk())
        .andExpect(content().json(gson.toJson(response)));
    }

    @Test
    void shouldGetPagesByRole() throws Exception {
        List<PageResponse> response = List.of(
            new PageResponse(1, "name", "path", true),
            new PageResponse(2, "name", "path", true),
            new PageResponse(3, "name", "path", true),
            new PageResponse(4, "name", "path", true)
        );

        when(this.roleService.getPagesByRoleId(1)).thenReturn(response);

        this.mockMvc.perform(get("/role/find-pages/{id}", 1))
        .andExpect(status().isOk())
        .andExpect(content().json(gson.toJson(response)));
    }

    @Test
    void shouldUpdatePages() throws Exception {
        List<PageRequest> request = List.of(
            new PageRequest(1, "name", "path", true),
            new PageRequest(2, "name", "path", true),
            new PageRequest(3, "name", "path", true),
            new PageRequest(4, "name", "path", true),
            new PageRequest(5, "name", "path", true)
        );

        when(this.roleService.updatePages(1, request)).thenReturn("Privilegios actualizados.");

        this.mockMvc.perform(put("/role/update/{id}", 1)
        .contentType(MediaType.APPLICATION_JSON)
        .content(gson.toJson(request)))
        .andExpect(status().isOk())
        .andExpect(content().json("{\"message\":\"Privilegios actualizados.\"}"));
    }
}
