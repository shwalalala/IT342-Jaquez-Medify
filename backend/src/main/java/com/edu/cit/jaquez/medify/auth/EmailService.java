package com.edu.cit.jaquez.medify.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.edu.cit.jaquez.medify.user.User;

@Service
public class EmailService {
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Value("${spring.mail.host:}")
    private String smtpHost;

    @Value("${app.mail.from:no-reply@medify.local}")
    private String from;

    public EmailService(ObjectProvider<JavaMailSender> mailSenderProvider) {
        this.mailSenderProvider = mailSenderProvider;
    }

    public String sendVerificationEmail(User user) {
        String link = frontendUrl + "/verify-email?token=" + user.getVerificationToken();

        if (smtpHost == null || smtpHost.isBlank()) {
            log.info("SMTP not configured. Dev verification link for {}: {}", user.getEmail(), link);
            return link;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(user.getEmail());
            message.setSubject("Verify your Medify account");
            message.setText("Hi " + user.getFirstName() + ",\n\n" +
                    "Please verify your Medify account by opening this link:\n" +
                    link + "\n\nThis link expires in 24 hours.\n\nMedify");
            mailSenderProvider.getObject().send(message);
            return null;
        } catch (Exception ex) {
            log.warn("Failed to send verification email. Dev link: {}", link, ex);
            return link;
        }
    }
}
