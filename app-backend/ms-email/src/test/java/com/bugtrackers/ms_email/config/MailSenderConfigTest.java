package com.bugtrackers.ms_email.config;

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
import org.springframework.mail.javamail.JavaMailSender;

import com.bugtrackers.ms_email.exceptions.CompanySettingsNotFoundException;
import com.bugtrackers.ms_email.models.CompanySetting;
import com.bugtrackers.ms_email.repositories.CompanySettingRepository;

public class MailSenderConfigTest {

    @Mock
    private CompanySettingRepository companySettingRepository;

    @InjectMocks
    private MailSenderConfig mailSenderConfig;
    CompanySetting gmailAddress;
    CompanySetting gmailPassword;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        gmailAddress = new CompanySetting();
        gmailAddress.setKeyName("gmail_address");
        gmailAddress.setKeyValue("address@test.com");
        gmailPassword = new CompanySetting();
        gmailPassword.setKeyName("gmail_password");
        gmailPassword.setKeyValue("password");
    }

    @Test
    void shouldGetMailSender() {
        when(this.companySettingRepository.findByKeyName("gmail_address")).thenReturn(Optional.of(gmailAddress));
        when(this.companySettingRepository.findByKeyName("gmail_password")).thenReturn(Optional.of(gmailPassword));

        JavaMailSender javaMailSender = this.mailSenderConfig.getJavaMailSender();

        assertNotNull(javaMailSender);
    }

    @Test
    void shouldThrowCompanySettingNotFoundByEmail() {
        when(this.companySettingRepository.findByKeyName("gmail_address")).thenReturn(Optional.empty());
        when(this.companySettingRepository.findByKeyName("gmail_password")).thenReturn(Optional.of(gmailPassword));

        Exception exception = assertThrows(CompanySettingsNotFoundException.class, () -> {
            mailSenderConfig.getJavaMailSender();
        });

        String expectedMessage = "No se encontraron las credenciales del correo.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void shouldThrowCompanySettingNotFoundByPassword() {
        when(this.companySettingRepository.findByKeyName("gmail_address")).thenReturn(Optional.of(gmailAddress));
        when(this.companySettingRepository.findByKeyName("gmail_password")).thenReturn(Optional.empty());

        Exception exception = assertThrows(CompanySettingsNotFoundException.class, () -> {
            mailSenderConfig.getJavaMailSender();
        });

        String expectedMessage = "No se encontraron las credenciales del correo.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void shouldThrowCompanySettingNotFoundByEmailPassword() {
        when(this.companySettingRepository.findByKeyName("gmail_address")).thenReturn(Optional.empty());
        when(this.companySettingRepository.findByKeyName("gmail_password")).thenReturn(Optional.empty());

        Exception exception = assertThrows(CompanySettingsNotFoundException.class, () -> {
            mailSenderConfig.getJavaMailSender();
        });

        String expectedMessage = "No se encontraron las credenciales del correo.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }
}
