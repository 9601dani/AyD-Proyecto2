package com.bugtrackers.ms_email.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.bugtrackers.ms_email.dto.EmailRequest;
import com.bugtrackers.ms_email.exceptions.EmailNotSendException;
import com.bugtrackers.ms_email.utils.MimeMessageHelperFactory;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

public class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Mock
    private MimeMessage mimeMessage;

    @Mock
    private MimeMessageHelperFactory helperFactory;

    private EmailRequest emailRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        emailRequest = new EmailRequest("to@gmail.com", "subject", "content");
    }

    @Test
    void shouldSendEmail() throws MessagingException {
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        when(this.mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(this.helperFactory.create(mimeMessage, true, "UTF-8")).thenReturn(helper);

        this.emailService.sendEmail(emailRequest);
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void shouldThrowEmailNotSendException() throws MessagingException {

        when(this.mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(this.helperFactory.create(mimeMessage, true, "UTF-8")).thenThrow(new MessagingException());

        Exception exception = assertThrows(EmailNotSendException.class, () -> {
            this.emailService.sendEmail(emailRequest);
        });

        String expectedMessage = "No se pudo enviar el correo.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }
}
