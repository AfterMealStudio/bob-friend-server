package com.example.bob_friend.service;

import com.example.bob_friend.model.dto.MemberDto;
import com.example.bob_friend.model.entity.*;
import com.example.bob_friend.model.exception.MemberDuplicatedException;
import com.example.bob_friend.repository.*;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

        MemberDto.Response getMember = memberService.getMemberWithAuthorities(testMember.getEmail());

        assertThat(new MemberDto.Response(testMember), equalTo(getMember));
    }

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
                .authorities(Collections.singleton(Authority.ROLE_USER))
                .agree(true)
                .active(true)
                .build();

        MemberDto.Signup memberSignupDto = MemberDto.Signup.builder()
                .email("signupTestEmail")
                .nickname("signupTestUser")
                .password("1234")
                .sex(Sex.FEMALE)
                .birth(LocalDate.of(2020, 8, 9))
                .agree(true)
                .build();

        when(memberRepository.existsMemberByEmail(any()))
                .thenReturn(false);
        when(memberRepository.save(any()))
                .thenReturn(signupTest);
        when(emailService.makeMailText(any()))
                .thenReturn("http://localhost:8080/api/?email=qww1552@naver.com&code=-150140394");
        MemberDto.Response responseDto = memberService.signup(memberSignupDto);
        MemberDto.Response memberResponseDto = new MemberDto.Response(signupTest);

        assertThat(responseDto, equalTo(memberResponseDto));
    }

    @Test
    @DisplayName(value = "signup_fail_MemberDuplicated")
    void signupFail() {
        MemberDto.Signup memberSignupDto = MemberDto.Signup.builder()
                .email("signupTestEmail")
                .nickname("signupTestUser")
                .password("1234")
                .sex(Sex.FEMALE)
                .birth(LocalDate.of(2020, 8, 9))
                .agree(true)
                .build();

        when(memberRepository.existsMemberByEmail(any()))
                .thenReturn(true);

        assertThrows(MemberDuplicatedException.class
                , () -> memberService.signup(memberSignupDto));
    }

    @Test
    @DisplayName(value = "check_member_with_code")
    void checkMemberWithCodeTest() {
        when(memberRepository.getMemberByEmail(any())).thenReturn(testMember);
        memberService.checkMemberWithCode(testMember.getEmail(), String.valueOf(testMember.hashCode()));
        assertTrue(testMember.isVerified());
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
                .replies(new HashSet<>())
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
