package com.bugtrackers.ms_user.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.bugtrackers.ms_user.dto.request.CompanySettingRequest;
import com.bugtrackers.ms_user.dto.response.CompanySettingResponse;
import com.bugtrackers.ms_user.exceptions.CompanySettingNotFoundException;
import com.bugtrackers.ms_user.models.CompanySetting;
import com.bugtrackers.ms_user.models.SettingType;
import com.bugtrackers.ms_user.models.ValueType;
import com.bugtrackers.ms_user.repositories.CompanySettingRepository;
import com.bugtrackers.ms_user.repositories.SettingTypeRepository;

public class CompanySettingServiceTest {

    @Mock
    private CompanySettingRepository companySettingRepository;

    @Mock
    private SettingTypeRepository settingTypeRepository;

    @InjectMocks
    private CompanySettingService companySettingService;
    List<CompanySetting> mockCompanySettings;
    List<CompanySettingRequest> request;



    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockCompanySettings = List.of(
            new CompanySetting(1, "keyName1", "keyValue1", "labelValue1", true, true, new ValueType(1, "value"), new SettingType(1, "value"),""),
            new CompanySetting( 2, "keyName2", "keyValue2", "labelValue2", true, true, new ValueType(1, "value"), new SettingType(1, "value"), ""),
            new CompanySetting( 3, "keyName3", "keyValue3", "labelValue3", true, true, new ValueType(1, "value"), new SettingType(1, "value"), "")
        );
        request = List.of(
            new CompanySettingRequest("keyName1", "keyValue1", "value"),
            new CompanySettingRequest("keyName2", "keyValue2", "value"),
            new CompanySettingRequest("keyName3", "keyValue3", "value")
        );
    }

    @Test
    void shouldFindByKeyName() {
        CompanySettingResponse response = new CompanySettingResponse(mockCompanySettings.get(0));
        when(this.companySettingRepository.findByKeyName("keyName1")).thenReturn(Optional.of(mockCompanySettings.get(0)));

        CompanySettingResponse result = this.companySettingService.findByKeyName("keyName1");
        assertNotNull(result);
        assertEquals(result, response);
    }

    @Test
    void shouldThrowCompanyNotFoundException() {
        when(this.companySettingRepository.findByKeyName("keyName1")).thenReturn(Optional.empty());
        Exception exception = assertThrows(CompanySettingNotFoundException.class, () -> {
            this.companySettingService.findByKeyName("keyName1");
        });

        String expectedMessage = "No se encontró la configuración.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void shouldFindBySettingName() {
        List<CompanySettingResponse> response = mockCompanySettings.stream().map(CompanySettingResponse::new).toList();
        when(this.companySettingRepository.findBySettingTypeName("value")).thenReturn(mockCompanySettings);
        when(this.companySettingRepository.save(mockCompanySettings.get(0))).thenReturn(mockCompanySettings.get(0));
        when(this.companySettingRepository.save(mockCompanySettings.get(1))).thenReturn(mockCompanySettings.get(1));
        when(this.companySettingRepository.save(mockCompanySettings.get(2))).thenReturn(mockCompanySettings.get(2));

        List<CompanySettingResponse> result = this.companySettingService.findBySettingName("value");
        assertNotNull(result);
        assertEquals(response, result);

    }

    @Test
    void shouldUpdate() {
        List<CompanySettingResponse> response = mockCompanySettings.stream().map(CompanySettingResponse::new).toList();
        when(this.companySettingRepository.findByKeyName("keyName1")).thenReturn(Optional.of(mockCompanySettings.get(0)));
        when(this.companySettingRepository.findByKeyName("keyName2")).thenReturn(Optional.of(mockCompanySettings.get(1)));
        when(this.companySettingRepository.findByKeyName("keyName3")).thenReturn(Optional.of(mockCompanySettings.get(2)));
        when(this.companySettingRepository.save(mockCompanySettings.get(0))).thenReturn(mockCompanySettings.get(0));
        when(this.companySettingRepository.save(mockCompanySettings.get(1))).thenReturn(mockCompanySettings.get(1));
        when(this.companySettingRepository.save(mockCompanySettings.get(2))).thenReturn(mockCompanySettings.get(2));

        List<CompanySettingResponse> result = this.companySettingService.update(request);
        assertNotNull(result);
        assertEquals(response, result);
    }

    @Test
    void shouldThrowException() {

        when(this.companySettingRepository.findByKeyName("keyName3")).thenReturn(Optional.empty());

        Exception exception = assertThrows(CompanySettingNotFoundException.class, () -> {
            this.companySettingService.update(request);
        });

        String expectedMessage = "No se encontró la configuración.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void shouldReturnSettingsArray() {
        List<SettingType> mocks = List.of(
            new SettingType(1, "name1"),
            new SettingType(2, "name2"),
            new SettingType(3, "name3")
        );
        List<String> response = mocks.stream().map(SettingType::getName).toList();
        when(this.settingTypeRepository.findAll()).thenReturn(mocks);

        List<String> result = this.companySettingService.findAllSettingTypes();
        assertNotNull(result);
        assertEquals(response, result);
    }


}
