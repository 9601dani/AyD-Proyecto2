package com.bugtrackers.ms_email.services;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.bugtrackers.ms_email.dto.EmailRequest;
import com.bugtrackers.ms_email.exceptions.EmailNotSendException;
import com.bugtrackers.ms_email.utils.MimeMessageHelperFactory;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final MimeMessageHelperFactory helperFactory;

    public String sendEmail(EmailRequest emailRequest) {

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();

            MimeMessageHelper helper = this.helperFactory.create(mimeMessage, true, "UTF-8");
            helper.setTo(emailRequest.to());
            helper.setSubject(emailRequest.subject());
            helper.setText(emailRequest.content(), true);

            mailSender.send(mimeMessage);
            return "El correo se envió exitosamente!";
        } catch (MessagingException e) {
            throw new EmailNotSendException("No se pudo enviar el correo.");
        }
    }
}
