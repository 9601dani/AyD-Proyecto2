package com.bugtrackers.ms_email.services;

import java.io.IOException;

import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.bugtrackers.ms_email.dto.EmailRequest;
import com.bugtrackers.ms_email.exceptions.EmailNotSendException;
import com.bugtrackers.ms_email.utils.MimeMessageHelperFactory;
import com.lowagie.text.DocumentException;

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

            if(emailRequest.createPDF()) {
                ByteArrayOutputStream pdfOutputStream = generatePdfFromHtml(emailRequest.content());
                helper.addAttachment("factura.pdf", new ByteArrayResource(pdfOutputStream.toByteArray()));
            }

            mailSender.send(mimeMessage);
            return "El correo se envió exitosamente!";
        } catch (Exception e) {
            System.out.println("ERROR");
            System.err.println(e);
            throw new EmailNotSendException("No se pudo enviar el correo.");
        }
    }

    public ByteArrayOutputStream generatePdfFromHtml(String htmlContent) throws DocumentException, IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(htmlContent);
        renderer.layout();

        renderer.createPDF(outputStream);
        outputStream.close();

        return outputStream;
    }
}
