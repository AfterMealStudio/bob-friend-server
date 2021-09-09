package com.example.bob_friend.service;

import com.example.bob_friend.model.dto.MemberResponseDto;
import com.example.bob_friend.model.dto.MemberSignupDto;
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
    @InjectMocks
    MemberService memberService;
    Member testMember;

    @BeforeEach
    public void setUp() {

        testMember = Member.builder()
                .id(0L)
                .email("testEmail")
                .username("testUsername")
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
    @DisplayName("멤버 이름으로 가져오기")
    public void getMemberWithAuthoritiesTest() {
        when(memberRepository.findMemberWithAuthoritiesByUsername(any()))
                .thenReturn(Optional.ofNullable(testMember));

        MemberResponseDto getMember = memberService.getMemberWithAuthorities(testMember.getUsername());

        assertThat(new MemberResponseDto(testMember), equalTo(getMember));
    }

    @Test
    @DisplayName(value = "회원가입 성공")
    public void signup() {
        Member signupTest = Member.builder()
                .id(1L)
                .email("signupTestEmail")
                .username("signupTestUsername")
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

        MemberSignupDto memberSignupDto = MemberSignupDto.builder()
                .email("signupTestEmail")
                .username("signupTestUsername")
                .nickname("signupTestUser")
                .password("1234")
                .sex(Sex.FEMALE)
                .birth(LocalDate.of(2020, 8, 9))
                .agree(true)
                .build();

        when(memberRepository.existsMemberByUsername(any()))
                .thenReturn(false);
        when(memberRepository.save(any()))
                .thenReturn(signupTest);

        MemberResponseDto responseDto = memberService.signup(memberSignupDto);
        MemberResponseDto memberResponseDto = new MemberResponseDto(signupTest);

        assertThat(responseDto, equalTo(memberResponseDto));
    }

    @Test
    @DisplayName(value = "계정 중복으로 인한 회원가입 실패")
    public void signupFail() {
        MemberSignupDto memberSignupDto = MemberSignupDto.builder()
                .email("signupTestEmail")
                .username("signupTestUsername")
                .nickname("signupTestUser")
                .password("1234")
                .sex(Sex.FEMALE)
                .birth(LocalDate.of(2020, 8, 9))
                .agree(true)
                .build();

        when(memberRepository.existsMemberByUsername(any()))
                .thenReturn(true);

        assertThrows(MemberDuplicatedException.class
                , () -> memberService.signup(memberSignupDto));
    }

//    @Test
//    @WithUserDetails("testUsername")
//    public void getCurrentUsernameTest() {
//        String currentUsername = memberService.getCurrentUsername();
//        assertThat(currentUsername,equalTo("testUsername"));
//    }

//    @Test
//    public void getCurrentMemberTest() {
//        when(memberRepository.getMemberByUsername())
//    }

    @Test
    @DisplayName(value = "멤버를 신고하면 신고가 1 증가")
    public void reportMemberTest() {
        testMember.setReportCount(0);
        when(memberRepository.findMemberByUsername(any()))
                .thenReturn(Optional.ofNullable(testMember));
        MemberResponseDto memberResponseDto =
                memberService.reportMember(testMember.getUsername());

        assertThat(memberResponseDto.getReportCount(), equalTo(1));
    }

    @Test
    @DisplayName(value = "신고횟수가 3회를 초과하면 정지시키고 누적을 1회 증가")
    public void reportMemberMoreThanThreeTest() {
        testMember.setReportCount(0);
        when(memberRepository.findMemberByUsername(any()))
                .thenReturn(Optional.ofNullable(testMember));
        MemberResponseDto memberResponseDto = null;

        for (int i = 0; i < 4; i++) {
            memberResponseDto =
                    memberService.reportMember(testMember.getUsername());
        }

        assertThat(memberResponseDto.getReportCount(), equalTo(0));
        assertThat(memberResponseDto.getAccumulatedReports(), equalTo(1));
        assertThat(testMember.getReportStart(), equalTo(LocalDate.now()));
    }
}
