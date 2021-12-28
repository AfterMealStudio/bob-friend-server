package com.example.bobfriend.service;

import com.example.bobfriend.model.dto.member.Response;
import com.example.bobfriend.model.dto.member.Score;
import com.example.bobfriend.model.entity.*;
import com.example.bobfriend.repository.*;
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
import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {
    @Mock
    RecruitmentRepository recruitmentRepository;
    @Mock
    CommentRepository commentRepository;
    @Mock
    ReplyRepository replyRepository;
    @Mock
    MemberRepository memberRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    EmailService emailService;
    @Mock
    WritingReportRepository reportRepository;
    @InjectMocks
    MemberService memberService;
    Member testMember;

    @BeforeEach
    void setUp() {
        testMember = Member.builder()
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


    }

    @Test
    @DisplayName("get_member_by_email")
    void getMemberWithAuthoritiesTest() {
        when(memberRepository.findMemberWithAuthoritiesByEmail(any()))
                .thenReturn(Optional.ofNullable(testMember));

        Response getMember = memberService.getMemberWithAuthorities(testMember.getEmail());

        assertThat(new Response(testMember), equalTo(getMember));
    }


    @Test
    @DisplayName(value = "check_member_with_code")
    void checkMemberWithCodeTest() {
        when(memberRepository.findMemberByEmail(any())).thenReturn(Optional.ofNullable(testMember));
        memberService.checkMemberWithCode(testMember.getEmail(), String.valueOf(testMember.hashCode()));
        assertTrue(testMember.isEmailVerified());
    }


    @Test
    void deleteMember() {
        login();

        Recruitment recruitment = Recruitment.builder()
                .id(1L)
                .author(testMember)
                .active(true)
                .appointmentTime(LocalDateTime.now())
                .totalNumberOfPeople(4)
                .members(new HashSet<>())
                .sexRestriction(null)
                .latitude(0.0)
                .longitude(0.0)
                .content("test content1")
                .title("test title1")
                .restaurantAddress("test address")
                .restaurantName("test name")
                .createdAt(LocalDateTime.now())
                .endAt(LocalDateTime.now().plusDays(1))
                .build();
        Comment comment = Comment.builder()
                .id(1L)
                .content("test content")
                .recruitment(recruitment)
                .createdAt(LocalDateTime.now())
                .author(testMember)
                .replies(new LinkedList<>())
                .build();

        when(memberRepository.findMemberWithAuthoritiesByEmail(any()))
                .thenReturn(Optional.ofNullable(testMember));
        when(recruitmentRepository.findAllByAuthor(any()))
                .thenReturn(Arrays.asList(recruitment));
        when(commentRepository.findAllByAuthor(any()))
                .thenReturn(Arrays.asList(comment));

        memberService.deleteById(testMember.getId());


        assertThat(recruitment.getAuthor().getEmail(), equalTo("unknown"));
        assertThat(comment.getAuthor().getEmail(), equalTo("unknown"));

    }

    @Test
    void getCurrentMember() {
        login();
        when(memberRepository.findMemberWithAuthoritiesByEmail(testMember.getEmail()))
                .thenReturn(Optional.ofNullable(testMember));
        Member currentMember = memberService.getCurrentMember();

        assertThat(currentMember, equalTo(testMember));
    }


    @Test
    void rateMemberTest() {
        when(memberRepository.findMemberByNickname(testMember.getNickname()))
                .thenReturn(Optional.ofNullable(testMember));
        Score rate = new Score();
        rate.setScore(3.0);
        Response response =
                memberService.rateMember(testMember.getNickname(), rate);

        assertThat(response.getRating(), equalTo(rate.getScore()));
    }


    private void login() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        testMember.getEmail(),
                        testMember.getPassword(),
                        Collections.singleton(
                                new SimpleGrantedAuthority("ROLE_USER"))
                ));
    }
}
