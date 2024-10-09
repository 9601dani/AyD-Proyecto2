package com.bugtrackers.ms_auth.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.bugtrackers.ms_auth.exceptions.SettingNotFoundException;
import com.bugtrackers.ms_auth.models.CompanySetting;
import com.bugtrackers.ms_auth.repositories.CompanySettingRepository;

public class SecurityConfigTest {

    @Mock
    private CompanySettingRepository companySettingRepository;

    @InjectMocks
    private SecurityConfig securityConfig;

    CompanySetting secret;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        secret = new CompanySetting();
        secret.setKeyName("secret");
        secret.setKeyValue("secret_value");
    }

    @Test
    void shouldGetEncoder() {
        when(companySettingRepository.findByKeyName("secret")).thenReturn(Optional.of(secret));

        PasswordEncoder passwordEncoder = securityConfig.getEncoder();
        assertNotNull(passwordEncoder);
    }

    @Test
    void shouldThrowSettingNotFoundException() {
        when(companySettingRepository.findByKeyName("secret")).thenReturn(Optional.empty());

        Exception exception = assertThrows(SettingNotFoundException.class, () -> {
            securityConfig.getEncoder();
        });

        String expectedMessage = "No se encontró una configuración.";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }
}
