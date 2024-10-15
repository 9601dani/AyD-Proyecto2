package com.bugtrackers.ms_user.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.bugtrackers.ms_user.config.GsonConfig;
import com.bugtrackers.ms_user.dto.request.CompanySettingRequest;
import com.bugtrackers.ms_user.dto.response.CompanySettingResponse;
import com.bugtrackers.ms_user.models.CompanySetting;
import com.bugtrackers.ms_user.models.SettingType;
import com.bugtrackers.ms_user.models.ValueType;
import com.bugtrackers.ms_user.services.CompanySettingService;
import com.google.gson.Gson;

@WebMvcTest(CompanySettingController.class)
public class CompanySettingControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    CompanySettingService companySettingService;
    List<CompanySetting> mockCompanySettings;
    Gson gson;

    @BeforeEach
    void setUp() {
        mockCompanySettings = List.of(
            new CompanySetting(1, "keyName1", "keyValue1", "labelValue1", true, true, new ValueType(1, "value"), new SettingType(1, "value"), ""),
            new CompanySetting( 2, "keyName2", "keyValue2","labelValue2", true, true, new ValueType(1, "value"), new SettingType(1, "value"), ""),
            new CompanySetting( 3, "keyName3", "keyValue3","labelValue3", true, true, new ValueType(1, "value"), new SettingType(1, "value"), "")
        );
        gson = GsonConfig.createGsonWithLocalDateTimeAdapter();

    }

    @Test
    void shouldFindByKeyName() throws Exception {
        CompanySettingResponse response = new CompanySettingResponse(this.mockCompanySettings.get(0));
        when(this.companySettingService.findByKeyName("keyName1")).thenReturn(response);

        String expectedJson = gson.toJson(response);

        this.mockMvc.perform(get("/company-settings/keyName1"))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
    }


    @Test
    void shouldFindBySettingName() throws Exception {
        List<CompanySettingResponse> response = mockCompanySettings.stream().map(CompanySettingResponse::new).toList();
        when(this.companySettingService.findBySettingName("value")).thenReturn(response);

        String expectedJson = gson.toJson(response);
    
        this.mockMvc.perform(get("/company-settings/setting-type/value"))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
    }

    @Test
    void shouldUpdate() throws Exception {
        List<CompanySettingRequest> request = List.of(
            new CompanySettingRequest("keyName1", "keyValue1", "value"),
            new CompanySettingRequest("keyName2", "keyValue2", "value"),
            new CompanySettingRequest("keyName3", "keyValue3", "value")
        );
        List<CompanySettingResponse> response = mockCompanySettings.stream().map(CompanySettingResponse::new).toList();
        when(this.companySettingService.update(request)).thenReturn(response);

        String requestJson = gson.toJson(request);
        String responseJson = gson.toJson(response);

        this.mockMvc.perform(put("/company-settings/update")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestJson))
        .andExpect(status().isOk())
        .andExpect(content().json(responseJson));
    }

    @Test
    void shouldFindAllSettingTypes() throws Exception {
        List<String> settingTypes = List.of("name1", "name2", "name3");

        when(this.companySettingService.findAllSettingTypes()).thenReturn(settingTypes);

        String expectedJson = gson.toJson(settingTypes);

        this.mockMvc.perform(get("/company-settings/setting-types"))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
    }
}
