package com.bugtrackers.gateway.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import com.bugtrackers.gateway.exceptions.NotAllowedException;
import com.bugtrackers.gateway.models.CompanySetting;
import com.bugtrackers.gateway.models.User;
import com.bugtrackers.gateway.repositories.CompanySettingRepository;

public class TokenServiceTest {

    @Mock
    private CompanySettingRepository companySettingRepository;

    private TokenService tokenService;

    private User mockUser;
    
    CompanySetting jwtSecret;
    CompanySetting jwtTime;
    CompanySetting companyName;
    CompanySetting zone;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        jwtTime = new CompanySetting();
        jwtTime.setKeyName("jwt_time");
        jwtTime.setKeyValue("60");

        companyName = new CompanySetting();
        companyName.setKeyName("company_name");
        companyName.setKeyValue("my company");

        zone = new CompanySetting();
        zone.setKeyName("zone");
        zone.setKeyValue("America/Guatemala");

        jwtSecret = new CompanySetting();
        jwtSecret.setKeyName("jwt_secret");
        jwtSecret.setKeyValue("secret");

        when(companySettingRepository.findByKeyName("company_name")).thenReturn(Optional.of(companyName));
        when(companySettingRepository.findByKeyName("jwt_secret")).thenReturn(Optional.of(jwtSecret));
        when(companySettingRepository.findByKeyName("jwt_time")).thenReturn(Optional.of(jwtTime));
        when(companySettingRepository.findByKeyName("zone")).thenReturn(Optional.of(zone));

        tokenService = new TokenService(companySettingRepository);

        mockUser = new User();
        mockUser.setEmail("email@example.com");
        mockUser.setUsername("username");
        mockUser.setPassword("password");
    }

    @Test
    void testGetToken() {

        String token = tokenService.getToken(mockUser);

        assertNotNull(token);
    }

    @Test
    void shouldThrowNotAllowedExceptionFindByKeyNameJwtSecret() {

        when(companySettingRepository.findByKeyName("jwt_secret")).thenReturn(Optional.empty());
        when(companySettingRepository.findByKeyName("jwt_time")).thenReturn(Optional.of(jwtTime));
        when(companySettingRepository.findByKeyName("company_name")).thenReturn(Optional.of(companyName));
        when(companySettingRepository.findByKeyName("zone")).thenReturn(Optional.of(zone));

        Exception exception = assertThrows(NotAllowedException.class, () -> {
            tokenService = new TokenService(companySettingRepository);
        });

        String expectedMessage = "No se encontró una configuración.";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);

    }

    @Test
    void shouldThrowNotAllowedExceptionFindByKeyNameCompanyName() {

        when(companySettingRepository.findByKeyName("company_name")).thenReturn(Optional.empty());
        when(companySettingRepository.findByKeyName("jwt_secret")).thenReturn(Optional.of(jwtSecret));
        when(companySettingRepository.findByKeyName("jwt_time")).thenReturn(Optional.of(jwtTime));
        when(companySettingRepository.findByKeyName("zone")).thenReturn(Optional.of(zone));

        Exception exception = assertThrows(NotAllowedException.class, () -> {
            tokenService = new TokenService(companySettingRepository);
        });

        String expectedMessage = "No se encontró una configuración.";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);

    }

    @Test
    void shouldThrowNotAllowedExceptionFindByKeyNameJwtTime() {

        when(companySettingRepository.findByKeyName("company_name")).thenReturn(Optional.of(companyName));
        when(companySettingRepository.findByKeyName("jwt_secret")).thenReturn(Optional.of(jwtSecret));
        when(companySettingRepository.findByKeyName("jwt_time")).thenReturn(Optional.empty());
        when(companySettingRepository.findByKeyName("zone")).thenReturn(Optional.of(zone));

        Exception exception = assertThrows(NotAllowedException.class, () -> {
            tokenService = new TokenService(companySettingRepository);
        });

        String expectedMessage = "No se encontró una configuración.";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);

    }

    @Test
    void shouldThrowNotAllowedFindByKeyNameZone() {

        when(companySettingRepository.findByKeyName("company_name")).thenReturn(Optional.of(companyName));
        when(companySettingRepository.findByKeyName("jwt_secret")).thenReturn(Optional.of(jwtSecret));
        when(companySettingRepository.findByKeyName("jwt_time")).thenReturn(Optional.of(jwtTime));
        when(companySettingRepository.findByKeyName("zone")).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotAllowedException.class, () -> {
            tokenService = new TokenService(companySettingRepository);
        });

        String expectedMessage = "No se encontró una configuración.";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);

    }

    @Test
    void shouldThrowNotAllowedExceptionFindByKeyNameAllEmpty() {

        when(companySettingRepository.findByKeyName("company_name")).thenReturn(Optional.empty());
        when(companySettingRepository.findByKeyName("jwt_secret")).thenReturn(Optional.empty());
        when(companySettingRepository.findByKeyName("jwt_time")).thenReturn(Optional.empty());
        when(companySettingRepository.findByKeyName("zone")).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotAllowedException.class, () -> {
            tokenService = new TokenService(companySettingRepository);
        });

        String expectedMessage = "No se encontró una configuración.";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);

    }

    @Test
    void shouldGetClaim() {
        String token = tokenService.getToken(mockUser);
        String expectedClaim = "username";
        String actualClaim = tokenService.getClaim(token, "username");
        assertEquals(expectedClaim, actualClaim);
    }

    @Test
    void shouldReturnExpiredFalse() {
        String token = tokenService.getToken(mockUser);
        boolean actualValue = tokenService.isTokenExpired(token);
        assertFalse(actualValue);
    }
}
