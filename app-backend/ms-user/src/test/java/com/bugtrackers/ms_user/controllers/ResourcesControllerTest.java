package com.bugtrackers.ms_user.controllers;

import com.bugtrackers.ms_user.config.GsonConfig;
import com.bugtrackers.ms_user.dto.request.ResourceRequest;
import com.bugtrackers.ms_user.dto.response.AttributeResponse;
import com.bugtrackers.ms_user.dto.response.ResourceResponse;
import com.bugtrackers.ms_user.models.Attribute;
import com.bugtrackers.ms_user.services.ResourceService;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ResourcesController.class)
public class ResourcesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ResourceService resourceService;

    private List<Attribute> mockAttributes;
    private Gson gson;

    @BeforeEach
    void setUp() {
        Attribute attribute1 = new Attribute(1, "Attribute1", "Description1", List.of());
        Attribute attribute2 = new Attribute(2, "Attribute2", "Description2", List.of());
        mockAttributes = List.of(attribute1, attribute2);

        gson = GsonConfig.createGsonWithAdapter();
    }

    @Test
    void testGetAttributes() throws Exception {
        when(resourceService.getAttributes()).thenReturn(mockAttributes);

        String jsonResponse = gson.toJson(mockAttributes);

        mockMvc.perform(get("/resource/attributes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(jsonResponse));
    }

    @Test
    void testCreateAttribute() throws Exception {
        Attribute newAttribute = new Attribute(3, "Attribute3", "Description3", List.of());

        when(resourceService.createAttribute(any(Attribute.class))).thenReturn(newAttribute);

        String jsonRequest = gson.toJson(newAttribute);
        String jsonResponse = gson.toJson(newAttribute);

        mockMvc.perform(post("/resource/attributes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(jsonResponse));
    }

    @Test
    void testCreateResource() throws Exception {

        ResourceRequest resourceRequest = new ResourceRequest("ResourceName", mockAttributes);
        ResourceResponse resourceResponse = new ResourceResponse(1, "ResourceName", mockAttributes.stream().map(AttributeResponse::new).toList());

        when(resourceService.createResource(any(ResourceRequest.class))).thenReturn(resourceResponse);

        String jsonRequest = gson.toJson(resourceRequest);
        String jsonResponse = gson.toJson(resourceResponse);

        mockMvc.perform(post("/resource")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(jsonResponse));
    }

    @Test
    void testGetResources() throws Exception {
        ResourceResponse resource1 = new ResourceResponse(1, "Resource1", mockAttributes.stream().map(AttributeResponse::new).toList());
        ResourceResponse resource2 = new ResourceResponse(2, "Resource2", mockAttributes.stream().map(AttributeResponse::new).toList());
        List<ResourceResponse> resources = List.of(resource1, resource2);

        when(resourceService.getResources()).thenReturn(resources);

        String jsonResponse = gson.toJson(resources);

        mockMvc.perform(get("/resource")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(jsonResponse));
    }

    @Test
    void testGetResourceById() throws Exception {
        ResourceResponse resource = new ResourceResponse(1, "Resource1", mockAttributes.stream().map(AttributeResponse::new).toList());

        when(resourceService.getResourceById(1)).thenReturn(resource);

        String jsonResponse = gson.toJson(resource);

        mockMvc.perform(get("/resource/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(jsonResponse));
    }

    @Test
    void testUpdateResource() throws Exception {
        Attribute attribute1 = new Attribute(1, "Attribute1", "Description1", List.of());
        Attribute attribute2 = new Attribute(2, "Attribute2", "Description2", List.of());
        Integer resourceId = 1;
        ResourceRequest resourceRequest = new ResourceRequest("UpdatedResource", List.of(attribute1, attribute2));

        ResourceResponse updatedResourceResponse = new ResourceResponse(resourceId, "UpdatedResource", List.of(attribute1, attribute2).stream().map(AttributeResponse::new).toList());

        Mockito.when(resourceService.updateResource(eq(resourceId), any(ResourceRequest.class)))
                .thenReturn(updatedResourceResponse);

        String requestJson = gson.toJson(resourceRequest);
        String responseJson = gson.toJson(updatedResourceResponse);

        mockMvc.perform(put("/resource/{id}", resourceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(responseJson));
    }



}
