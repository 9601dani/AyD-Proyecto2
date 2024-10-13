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

public class CompanySettingServiceTest {

    @Mock
    private CompanySettingRepository companySettingRepository;

    @InjectMocks
    private CompanySettingService companySettingService;
    List<CompanySetting> mockCompanySettings;
    List<CompanySettingRequest> request;



    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockCompanySettings = List.of(
            new CompanySetting(1, "keyName1", "keyValue1", true, true, new ValueType(1, "value"), new SettingType(1, "value")),
            new CompanySetting( 2, "keyName2", "keyValue2", true, true, new ValueType(1, "value"), new SettingType(1, "value")),
            new CompanySetting( 3, "keyName3", "keyValue3", true, true, new ValueType(1, "value"), new SettingType(1, "value"))
        );
        request = List.of(
            new CompanySettingRequest(1, "keyName1", "keyValue1", true, "value", "value"),
            new CompanySettingRequest(2, "keyName2", "keyValue2", true, "value", "value"),
            new CompanySettingRequest(3, "keyName3", "keyValue3", true, "value", "value")
        );
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


}
