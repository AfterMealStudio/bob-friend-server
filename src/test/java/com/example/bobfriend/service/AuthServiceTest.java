package com.example.bobfriend.service;

import com.example.bobfriend.model.dto.member.Response;
import com.example.bobfriend.model.dto.member.Signup;
import com.example.bobfriend.model.entity.Authority;
import com.example.bobfriend.model.entity.Member;
import com.example.bobfriend.model.entity.Sex;
import com.example.bobfriend.model.exception.MemberDuplicatedException;
import com.example.bobfriend.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    MemberRepository memberRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    MemberService memberService;

    @InjectMocks
    AuthService authService;
    Member testMember;


    @Test
    @DisplayName(value = "signup_success")
    void signup() {
        Member signupTest = Member.builder()
                .id(1L)
                .email("signupTestEmail")
                .nickname("signupTestUser")
                .password(passwordEncoder.encode("1234"))
                .birth(LocalDate.of(2020, 8, 9))
                .sex(Sex.FEMALE)
                .reportCount(0)
                .accumulatedReports(0)
                .rating(0.0)
                .numberOfJoin(0)
                .authorities(Collections.singleton(Authority.ROLE_USER))
                .active(true)
                .build();

        Signup memberSignupDto = Signup.builder()
                .email("signupTestEmail")
                .nickname("signupTestUser")
                .password("1234")
                .sex(Sex.FEMALE)
                .birth(LocalDate.of(2020, 8, 9))
                .build();

        when(memberRepository.existsMemberByEmail(any()))
                .thenReturn(false);
        when(memberRepository.save(any()))
                .thenReturn(signupTest);
//        when(emailService.makeMailText(any()))
//                .thenReturn("http://localhost:8080/api/?email=qww1552@naver.com&code=-150140394");
        when(memberService.convertToEntity(any()))
                .thenReturn(signupTest);

        Response responseDto = authService.signup(memberSignupDto);
        Response memberResponseDto = new Response(signupTest);

        assertThat(responseDto, equalTo(memberResponseDto));
    }

    @Test
    @DisplayName(value = "signup_fail_MemberDuplicated")
    void signupFail() {
        Signup memberSignupDto = Signup.builder()
                .email("signupTestEmail")
                .nickname("signupTestUser")
                .password("1234")
                .sex(Sex.FEMALE)
                .birth(LocalDate.of(2020, 8, 9))
                .build();

        when(memberRepository.existsMemberByEmail(any()))
                .thenReturn(true);

        assertThrows(MemberDuplicatedException.class
                , () -> authService.signup(memberSignupDto));
    }


}
