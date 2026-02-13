package com.servicebooking.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // ✅ Take sender email from properties
    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendEmail(String to, String subject, String content) {

        SimpleMailMessage msg = new SimpleMailMessage();

        msg.setFrom(fromEmail); // ✅ VERY IMPORTANT
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(content);

        mailSender.send(msg);
    }
}