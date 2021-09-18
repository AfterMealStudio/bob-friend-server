package com.example.bob_friend.service;

import com.example.bob_friend.model.dto.MemberDto;
import com.example.bob_friend.model.entity.Authority;
import com.example.bob_friend.model.entity.Member;
import com.example.bob_friend.model.entity.Sex;
import com.example.bob_friend.model.exception.MemberDuplicatedException;
import com.example.bob_friend.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.annotation.SecurityTestExecutionListeners;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SecurityTestExecutionListeners
public class MemberServiceTest {
    @Mock
    MemberRepository memberRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    EmailService emailService;
    @InjectMocks
    MemberService memberService;
    Member testMember;

    @BeforeEach
    public void setUp() {
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
    @DisplayName("get_member_by_name")
    public void getMemberWithAuthoritiesTest() {
        when(memberRepository.findMemberWithAuthoritiesByEmail(any()))
                .thenReturn(Optional.ofNullable(testMember));

        MemberDto.Response getMember = memberService.getMemberWithAuthorities(testMember.getEmail());

        assertThat(new MemberDto.Response(testMember), equalTo(getMember));
    }

    @Test
    @DisplayName(value = "signup_success")
    public void signup() {
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
    public void signupFail() {
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
    @DisplayName(value = "reportMember")
    public void reportMemberTest() {
        testMember.setReportCount(0);
        when(memberRepository.findMemberByEmail(any()))
                .thenReturn(Optional.ofNullable(testMember));
        MemberDto.Response memberResponseDto =
                memberService.reportMember(testMember.getEmail());

        assertThat(memberResponseDto.getReportCount(), equalTo(1));
    }

    @Test
    @DisplayName(value = "accumulate_report")
    public void reportMemberMoreThanThreeTest() {
        testMember.setReportCount(0);
        when(memberRepository.findMemberByEmail(any()))
                .thenReturn(Optional.ofNullable(testMember));
        MemberDto.Response memberResponseDto = null;

        for (int i = 0; i < 4; i++) {
            memberResponseDto =
                    memberService.reportMember(testMember.getEmail());
        }

        assertThat(memberResponseDto.getReportCount(), equalTo(0));
        assertThat(memberResponseDto.getAccumulatedReports(), equalTo(1));
        assertThat(testMember.getReportStart(), equalTo(LocalDate.now()));
    }
}
