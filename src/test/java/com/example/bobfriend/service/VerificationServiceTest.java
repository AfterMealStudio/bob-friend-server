package com.example.bobfriend.service;

import com.example.bobfriend.model.entity.Member;
import com.example.bobfriend.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VerificationServiceTest {
    @Mock
    JavaMailSender mailSender;
    @Mock
    MemberRepository memberRepository;

    @InjectMocks
    EmailVerificationService verificationService;

    @Test
    void confirmTest() {
        Member testMember = Member.builder()
                .email("test")
                .build();
        when(memberRepository.findMemberByEmail(any()))
                .thenReturn(Optional.ofNullable(testMember));
        String testVerificationCode = "1234";
        testMember.setVerificationCode(testVerificationCode);

        boolean confirm = verificationService.confirm(testMember.getEmail(),
                testVerificationCode);

        assertThat(confirm, equalTo(true));
        assertThat(testMember.isVerified(), equalTo(true));
    }

}
