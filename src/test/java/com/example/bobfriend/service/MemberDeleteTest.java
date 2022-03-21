package com.example.bobfriend.service;

import com.example.bobfriend.model.dto.member.Delete;
import com.example.bobfriend.model.entity.*;
import com.example.bobfriend.repository.*;
import com.example.bobfriend.util.TestCommentGenerator;
import com.example.bobfriend.util.TestMemberGenerator;
import com.example.bobfriend.util.TestRecruitmentGenerator;
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
    RecruitmentMemberRepository recruitmentMemberRepository;
    @Mock
    WritingReportRepository reportRepository;
    @Mock
    MemberAgreementRepository agreementRepository;
    @InjectMocks
    MemberDeleteService memberDeleteService;

    Member testAuthor;
    Recruitment testRecruitment;

    private TestMemberGenerator testMemberGenerator = new TestMemberGenerator();
    private TestRecruitmentGenerator testRecruitmentGenerator = new TestRecruitmentGenerator();
    private TestCommentGenerator testCommentGenerator = new TestCommentGenerator();

    @BeforeEach
    void beforeEach() {
        testAuthor = testMemberGenerator.getTestAuthor();
        testRecruitmentGenerator.setAuthor(testAuthor);
        testRecruitment = testRecruitmentGenerator.getTestRecruitment();

        testCommentGenerator.setTestAuthor(testAuthor);
        testCommentGenerator.setTestRecruitment(testRecruitment);
    }


    @Test
    @DisplayName("회원을 삭제하면 회원이 작성한 게시물의 작성자 필드가 null이 된다.")
    void deleteMember() {
        Comment testComment = testCommentGenerator.getTestComment();

        setAuthentication(testAuthor);

        when(memberRepository.findMemberByEmail(any()))
                .thenReturn(Optional.ofNullable(testAuthor));
        when(writingRepository.findAllByAuthor(any()))
                .thenReturn(List.of(testRecruitment, testComment));

        memberDeleteService.delete();

        assertThat(testRecruitment.getAuthor().getEmail(), equalTo("unknown"));
        assertThat(testComment.getAuthor().getEmail(), equalTo("unknown"));

    }

    private void setAuthentication(Member member) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        member.getEmail(),
                        member.getPassword(),
                        Collections.singleton(
                                new SimpleGrantedAuthority("ROLE_USER"))
                ));
    }

}
