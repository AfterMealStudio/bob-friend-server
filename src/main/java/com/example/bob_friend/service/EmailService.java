package com.example.bob_friend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendMail(String to, String title, String text) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(to);
        mailMessage.setSubject(title);
        mailMessage.setFrom("noreply@bobfriendtest.com");
        mailMessage.setText(text);
        mailSender.send(mailMessage);
    }
}
