package com.bugtrackers.ms_email.config;

import java.util.Optional;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.bugtrackers.ms_email.exceptions.CompanySettingsNotFoundException;
import com.bugtrackers.ms_email.models.CompanySetting;
import com.bugtrackers.ms_email.repositories.CompanySettingRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class MailSenderConfig {
    
    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private int port;

    @Value("${spring.mail.protocol}")
    private String protocol;

    @Value("${spring.mail.properties.mail.smtp.auth}")
    private String smtpAuth;

    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private String starttlsEnable;

    @Value("${spring.mail.properties.mail.smtp.starttls.required}")
    private String starttlsRequired;

    private final CompanySettingRepository companySettingRepository;

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

        javaMailSender.setHost(host);;
        javaMailSender.setPort(port);
        javaMailSender.setProtocol(protocol);

        Optional<CompanySetting> optionalGmailAddress = this.companySettingRepository.findByKeyName("gmail_address");
        Optional<CompanySetting> optionalGmailPassword = this.companySettingRepository.findByKeyName("gmail_password");

        if(optionalGmailAddress.isEmpty() || optionalGmailPassword.isEmpty()) {
            throw new CompanySettingsNotFoundException("No se encontraron las credenciales del correo.");
        }

        CompanySetting gmailAddress = optionalGmailAddress.get();
        CompanySetting gmailPassword = optionalGmailPassword.get();

        javaMailSender.setUsername(gmailAddress.getKeyValue());
        javaMailSender.setPassword(gmailPassword.getKeyValue());

        Properties properties = javaMailSender.getJavaMailProperties();
        properties.put("mail.smtp.auth", smtpAuth);
        properties.put("mail.smtp.starttls.enable", starttlsEnable);
        properties.put("mail.smtp.starttls.required", starttlsRequired);

        return javaMailSender;
    }
}
