package com.bugtrackers.ms_email.services;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.bugtrackers.ms_email.dto.EmailBody;
import com.bugtrackers.ms_email.exceptions.EmailNotSendException;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendEmail(EmailBody emailBody) {

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(emailBody.to());
            helper.setSubject(emailBody.subject());
            helper.setText(emailBody.content(), true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new EmailNotSendException("No se pudo enviar el correo.");
        }
    }
}
