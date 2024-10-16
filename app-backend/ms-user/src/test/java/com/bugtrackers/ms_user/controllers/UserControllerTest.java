package com.bugtrackers.ms_user.controllers;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import com.bugtrackers.ms_user.dto.request.RequestString;
import com.bugtrackers.ms_user.dto.response.ResponseString;
import com.bugtrackers.ms_user.models.User;
import com.bugtrackers.ms_user.models.UserInformation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.bugtrackers.ms_user.config.GsonConfig;
import com.bugtrackers.ms_user.dto.request.UserAllRequest;
import com.bugtrackers.ms_user.dto.response.ModuleResponse;
import com.bugtrackers.ms_user.dto.response.UserAllResponse;
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


    private UserAllRequest userAllRequest;

    @BeforeEach
    void setUp() {
        mockModules = List.of(
            new Module(1, "module", "/direction", true, LocalDateTime.now(), List.of(
                new Page(1, "page", "/path", null, true, LocalDateTime.now())
            ))
        );

        gson = GsonConfig.createGsonWithLocalDateTimeAdapter();

        userAllRequest = new UserAllRequest( "nitUpdate", "imageProfileUpdate", "descriptionUpdate");
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

    @Test
    void testGetProfile() throws Exception{

        UserAllResponse userAllResponse = new UserAllResponse("email", "username", "nit", "imageProfile", "description");

        when(userService.getById(1)).thenReturn(userAllResponse);

        String expectedJson = gson.toJson(userAllResponse);

        mockMvc.perform(get("/user/profile/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    void testPutProfile() throws Exception{
        
        UserAllResponse userAllResponse = new UserAllResponse("emailUpdate", "usernameUpdate", "nitUpdate", "imageProfileUpdate", "descriptionUpdate");
        
        when(userService.updateProfile(1, userAllRequest)).thenReturn(userAllResponse);

        String expectedJson = gson.toJson(userAllResponse);

        mockMvc.perform(put("/user/profile/1")
            .contentType(MediaType.APPLICATION_JSON) 
            .content(gson.toJson(userAllRequest)))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedJson)); 
    }

    @Test
    void testGetInfo() throws Exception {
        User user = new User("email", "username", "password");
        UserInformation userInformation = new UserInformation("nit", "imageProfile", "description", user);

        when(userService.getInfo(1)).thenReturn(userInformation.getImageProfile());

        String expectedResponse = "{\"message\":\"imageProfile\"}";

        mockMvc.perform(get("/user/info/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));  // Verificamos el JSON de respuesta
    }

    @Test
    void shouldUpdateImageProfile() throws Exception {
        int id = 1;
        String pathImg = "pathImg";
        RequestString requestString = new RequestString(pathImg);

        Gson gson = new Gson();
        String requestJson = gson.toJson(requestString);

        when(userService.updateImageProfile(id, pathImg)).thenReturn(1);

        mockMvc.perform(put("/user/profile/img/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"message\":\"" + pathImg + "\"}"));
    }



}
