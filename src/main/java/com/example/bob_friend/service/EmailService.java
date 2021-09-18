package com.example.bob_friend.service;

import com.example.bob_friend.model.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RequiredArgsConstructor
@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendMail(String to, String title, String text) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(to);
        mailMessage.setSubject(title);
        mailMessage.setText(text);
        mailSender.send(mailMessage);
    }

    public String makeMailText(Member member) {
        String baseUrl =
                ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        return baseUrl + "/api/?" + "email=" +
                member.getEmail() + "&code=" +
                member.hashCode();
    }
}
