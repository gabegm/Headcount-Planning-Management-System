package com.gaucimaistre.headcount.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromAddress;

    public void sendPasswordResetEmail(String toEmail, String resetToken, String appBaseUrl) {
        String resetLink = appBaseUrl + "/auth/reset/" + resetToken;
        String subject = "Password Reset Request";
        String body = """
                <p>You requested a password reset for your Headcount Planning account.</p>
                <p>Click the link below to reset your password:</p>
                <p><a href="%s">%s</a></p>
                <p>If you did not request this, please ignore this email.</p>
                """.formatted(resetLink, resetLink);
        sendNotificationEmail(toEmail, subject, body);
    }

    public void sendNotificationEmail(String toEmail, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body, true);
            mailSender.send(message);
            log.debug("Email sent to {} with subject '{}'", toEmail, subject);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", toEmail, e.getMessage(), e);
            throw new IllegalStateException("Failed to send email", e);
        }
    }
}
