package com.example.bobfriend.service;

import com.example.bobfriend.model.dto.member.Delete;
import com.example.bobfriend.model.entity.*;
import com.example.bobfriend.repository.*;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MemberDeleteTest {

    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    MemberRepository memberRepository;
    @Mock
    WritingRepository writingRepository;
    @Mock
    WritingReportRepository reportRepository;
    @Mock
    RecruitmentMemberRepository recruitmentMemberRepository;
    @Mock
    MemberAgreementRepository agreementRepository;

    @InjectMocks
    MemberService memberService;

    Member testAuthor;

    @BeforeEach
    void beforeEach() {
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
                .active(true)
                .build();

        testAuthor.setup();
    }


    @Test
    void deleteMember() {
        Recruitment recruitment = Recruitment.builder()
                .id(1L)
                .author(testAuthor)
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
                .author(testAuthor)
                .replies(new LinkedList<>())
                .build();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        testAuthor.getEmail(),
                        testAuthor.getPassword(),
                        Collections.singleton(
                                new SimpleGrantedAuthority("ROLE_USER"))
                ));

        when(memberRepository.findMemberWithAuthoritiesByEmail(any()))
                .thenReturn(Optional.ofNullable(testAuthor));
        when(writingRepository.findAllByAuthor(any()))
                .thenReturn(List.of(recruitment, comment));
        when(passwordEncoder.matches(any(), any()))
                .thenReturn(true);
        Delete delete = new Delete();
        delete.setPassword(testAuthor.getPassword());
        memberService.delete(delete);

        assertThat(recruitment.getAuthor().getEmail(), equalTo("unknown"));
        assertThat(comment.getAuthor().getEmail(), equalTo("unknown"));

    }

}
