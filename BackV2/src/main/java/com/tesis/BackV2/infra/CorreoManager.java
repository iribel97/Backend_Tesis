package com.tesis.BackV2.infra;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class CorreoManager {

    JavaMailSender emailSender;

    @Value("${spring.mail.username}")
    private String from;

    public CorreoManager(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void enviarCorreo( String correo, String asunto, String mensaje) {

        MimeMessage message = emailSender.createMimeMessage();

        try {
            message.setSubject(asunto);
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(correo);
            helper.setSubject(asunto);
            helper.setText(mensaje, true);

            emailSender.send(message);

        } catch (MessagingException e)  {
            throw new RuntimeException(e);
        }

    }
}
