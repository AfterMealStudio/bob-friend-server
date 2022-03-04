package com.example.bobfriend.service;

import com.example.bobfriend.model.dto.member.*;
import com.example.bobfriend.model.entity.Authority;
import com.example.bobfriend.model.entity.Member;
import com.example.bobfriend.model.entity.Sex;
import com.example.bobfriend.repository.MemberRepository;
import com.example.bobfriend.repository.RecruitmentMemberRepository;
import com.example.bobfriend.repository.WritingReportRepository;
import com.example.bobfriend.repository.WritingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {
    @Mock
    MemberRepository memberRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    EmailVerificationService emailService;
    @Mock
    WritingReportRepository reportRepository;
    @Mock
    RecruitmentMemberRepository recruitmentMemberRepository;
    @Mock
    WritingRepository writingRepository;

    @InjectMocks
    MemberService memberService;
    Member testAuthor;

    @BeforeEach
    void setUp() {
        testAuthor = Member.builder()
                .id(0L)
                .email("testEmail")
                .nickname("testUser")
                .password(passwordEncoder.encode("1234"))
                .birth(LocalDate.now())
                .sex(Sex.FEMALE)
                .reportCount(0)
                .accumulatedReports(0)
                .rating(0.0)
                .numberOfJoin(0)
                .authorities(Collections.singleton(Authority.ROLE_USER))
                .agree(true)
                .active(true)
                .build();

        testAuthor.setup();
    }

    @Test
    @DisplayName("get_member_by_email")
    void getMemberWithAuthoritiesTest() {
        when(memberRepository.findMemberWithAuthoritiesByEmail(any()))
                .thenReturn(Optional.ofNullable(testAuthor));

        Response getMember = memberService.getMemberWithAuthorities(testAuthor.getEmail());

        assertThat(new Response(testAuthor), equalTo(getMember));
    }


    @Test
    void getCurrentMember() {
        login();
        when(memberRepository.findMemberWithAuthoritiesByEmail(testAuthor.getEmail()))
                .thenReturn(Optional.ofNullable(testAuthor));
        Member currentMember = memberService.getCurrentMember();

        assertThat(currentMember, equalTo(testAuthor));
    }


    @Test
    void rateMemberTest() {
        when(memberRepository.findMemberByNickname(testAuthor.getNickname()))
                .thenReturn(Optional.ofNullable(testAuthor));
        Score rate = new Score();
        rate.setScore(3.0);
        Response response =
                memberService.rateMember(testAuthor.getNickname(), rate);

        assertThat(response.getRating(), equalTo(rate.getScore()));
    }


    @Test
    void updateTest() {
        login();
        when(memberRepository.findMemberWithAuthoritiesByEmail(any()))
                .thenReturn(Optional.ofNullable(testAuthor));
        Update incoming = new Update();
        incoming.setNickname("update");

        Response updatedMember = memberService.update(incoming);

        assertThat(updatedMember.getNickname(), equalTo(incoming.getNickname()));
    }


    @Test
    void resetPasswordTest() {
        when(memberRepository.findMemberWithAuthoritiesByEmail(any()))
                .thenReturn(Optional.ofNullable(testAuthor));
        when(passwordEncoder.encode(any()))
                .thenReturn("new-password");
        ResetPassword resetPasswordDto = new ResetPassword();
        resetPasswordDto.setEmail(testAuthor.getEmail());
        resetPasswordDto.setBirth(testAuthor.getBirth());

        String resetPassword = memberService.resetPassword(resetPasswordDto);

        assertThat(testAuthor.getPassword(), equalTo(
                passwordEncoder.encode(resetPassword)
        ));
    }

    @Test
    void updatePasswordTest() {
        login();
        when(memberRepository.findMemberWithAuthoritiesByEmail(any()))
                .thenReturn(Optional.ofNullable(testAuthor));
        String newPassword = "new-password";
        when(passwordEncoder.encode(any()))
                .thenReturn(newPassword);

        memberService.updatePassword(new UpdatePassword("test"));

        assertThat(testAuthor.getPassword(), equalTo(newPassword));
    }

    private void login() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        testAuthor.getEmail(),
                        testAuthor.getPassword(),
                        Collections.singleton(
                                new SimpleGrantedAuthority("ROLE_USER"))
                ));
    }
}
