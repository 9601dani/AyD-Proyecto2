package com.bugtrackers.ms_user.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.bugtrackers.ms_user.config.GsonConfig;
import com.bugtrackers.ms_user.dto.response.ModuleResponse;
import com.bugtrackers.ms_user.models.Module;
import com.bugtrackers.ms_user.models.Page;
import com.bugtrackers.ms_user.services.UserService;
import com.google.gson.Gson;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;
    private List<Module> mockModules;
    Gson gson;

    @BeforeEach
    void setUp() {
        mockModules = List.of(
            new Module(1, "module", "/direction", true, LocalDateTime.now(), List.of(
                new Page(1, "page", "/path", null, true, LocalDateTime.now())
            ))
        );

        gson = GsonConfig.createGsonWithLocalDateTimeAdapter();
    }

    @Test
    void testGetPages() throws Exception {
        List<ModuleResponse> moduleResponses = mockModules.stream().map(ModuleResponse::new).toList();
        when(userService.getPages(1)).thenReturn(moduleResponses);

        String expectedJson = gson.toJson(moduleResponses);

        mockMvc.perform(get("/user/pages/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }
}
