package com.bugtrackers.ms_auth.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import com.bugtrackers.ms_auth.exceptions.SettingNotFoundException;
import com.bugtrackers.ms_auth.models.CompanySetting;
import com.bugtrackers.ms_auth.models.User;
import com.bugtrackers.ms_auth.repositories.CompanySettingRepository;

public class TokenServiceTest {

    @Mock
    private CompanySettingRepository companySettingRepository;

    @InjectMocks
    private TokenService tokenService;

    private User mockUser;
    CompanySetting jwtSecret;
    CompanySetting jwtTime;
    CompanySetting companyName;
    CompanySetting zone;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUser = new User("email@example.com", "username", "password");
        jwtSecret = new CompanySetting();
        jwtSecret.setKeyName("jwt_secret");
        jwtSecret.setKeyValue("secret");

        jwtTime = new CompanySetting();
        jwtTime.setKeyName("jwt_time");
        jwtTime.setKeyValue("60");

        companyName = new CompanySetting();
        companyName.setKeyName("company_name");
        companyName.setKeyValue("my company");

        zone = new CompanySetting();
        zone.setKeyName("zone");
        zone.setKeyValue("Europe/Berlin");
    }

    @Test
    void testGetToken() {

        when(companySettingRepository.findByKeyName("jwt_secret")).thenReturn(Optional.of(jwtSecret));
        when(companySettingRepository.findByKeyName("jwt_time")).thenReturn(Optional.of(jwtTime));
        when(companySettingRepository.findByKeyName("company_name")).thenReturn(Optional.of(companyName));
        when(companySettingRepository.findByKeyName("zone")).thenReturn(Optional.of(zone));

        String token = tokenService.getToken(mockUser);

        assertNotNull(token);
    }

    @Test
    void shouldThrowSettingNotFoundExceptionFindByKeyNameJwtSecret() {

        when(companySettingRepository.findByKeyName("jwt_secret")).thenReturn(Optional.empty());
        when(companySettingRepository.findByKeyName("jwt_time")).thenReturn(Optional.of(jwtTime));
        when(companySettingRepository.findByKeyName("company_name")).thenReturn(Optional.of(companyName));
        when(companySettingRepository.findByKeyName("zone")).thenReturn(Optional.of(zone));

        Exception exception = assertThrows(SettingNotFoundException.class, () -> {
            tokenService.getToken(mockUser);
        });

        String expectedMessage = "No se encontró una configuración.";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);

    }

    @Test
    void shouldThrowSettingNotFoundExceptionFindByKeyNameCompanyName() {

        when(companySettingRepository.findByKeyName("company_name")).thenReturn(Optional.empty());
        when(companySettingRepository.findByKeyName("jwt_secret")).thenReturn(Optional.of(jwtSecret));
        when(companySettingRepository.findByKeyName("jwt_time")).thenReturn(Optional.of(jwtTime));
        when(companySettingRepository.findByKeyName("zone")).thenReturn(Optional.of(zone));

        Exception exception = assertThrows(SettingNotFoundException.class, () -> {
            tokenService.getToken(mockUser);
        });

        String expectedMessage = "No se encontró una configuración.";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);

    }

    @Test
    void shouldThrowSettingNotFoundExceptionFindByKeyNameJwtTime() {

        when(companySettingRepository.findByKeyName("company_name")).thenReturn(Optional.of(companyName));
        when(companySettingRepository.findByKeyName("jwt_secret")).thenReturn(Optional.of(jwtSecret));
        when(companySettingRepository.findByKeyName("jwt_time")).thenReturn(Optional.empty());
        when(companySettingRepository.findByKeyName("zone")).thenReturn(Optional.of(zone));

        Exception exception = assertThrows(SettingNotFoundException.class, () -> {
            tokenService.getToken(mockUser);
        });

        String expectedMessage = "No se encontró una configuración.";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);

    }

    @Test
    void shouldThrowSettingNotFoundExceptionFindByKeyNameZone() {

        when(companySettingRepository.findByKeyName("company_name")).thenReturn(Optional.of(companyName));
        when(companySettingRepository.findByKeyName("jwt_secret")).thenReturn(Optional.of(jwtSecret));
        when(companySettingRepository.findByKeyName("jwt_time")).thenReturn(Optional.of(jwtTime));
        when(companySettingRepository.findByKeyName("zone")).thenReturn(Optional.empty());

        Exception exception = assertThrows(SettingNotFoundException.class, () -> {
            tokenService.getToken(mockUser);
        });

        String expectedMessage = "No se encontró una configuración.";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);

    }

    @Test
    void shouldThrowSettingNotFoundExceptionFindByKeyNameAllEmpty() {

        when(companySettingRepository.findByKeyName("company_name")).thenReturn(Optional.empty());
        when(companySettingRepository.findByKeyName("jwt_secret")).thenReturn(Optional.empty());
        when(companySettingRepository.findByKeyName("jwt_time")).thenReturn(Optional.empty());
        when(companySettingRepository.findByKeyName("zone")).thenReturn(Optional.empty());

        Exception exception = assertThrows(SettingNotFoundException.class, () -> {
            tokenService.getToken(mockUser);
        });

        String expectedMessage = "No se encontró una configuración.";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);

    }
}
