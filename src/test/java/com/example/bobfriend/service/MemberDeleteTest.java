package com.example.bobfriend.service;

import com.example.bobfriend.model.dto.member.Delete;
import com.example.bobfriend.model.entity.*;
import com.example.bobfriend.model.exception.MemberNotFoundException;
import com.example.bobfriend.repository.*;
import com.example.bobfriend.testconfig.JpaTestConfig;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static com.example.bobfriend.util.TestAuthenticationUtil.setAuthentication;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DataJpaTest
@Import({JpaTestConfig.class, BCryptPasswordEncoder.class, MemberDeleteService.class})
public class MemberDeleteTest {

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    WritingReportRepository reportRepository;
    @Autowired
    RecruitmentMemberRepository recruitmentMemberRepository;
    @Autowired
    WritingRepository writingRepository;
    @Autowired
    MemberAgreementRepository agreementRepository;

    @Autowired
    MemberDeleteService memberDeleteService;

    Member testAuthor;
    Recruitment testRecruitment;
    Comment testComment;

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

        testComment = testCommentGenerator.getTestComment();
    }


    @Test
    @DisplayName("????????? ???????????? ????????? ????????? ???????????? ????????? ????????? null??? ??????.")
    void setNullToAuthorFieldWhenMemberDeleted() {
        memberRepository.save(testAuthor);
        writingRepository.save(testRecruitment);
        writingRepository.save(testComment);

        setAuthentication(testAuthor);

        memberDeleteService.delete();

        assertThat(testRecruitment.getAuthor().getEmail(), equalTo("unknown"));
        assertThat(testComment.getAuthor().getEmail(), equalTo("unknown"));

    }

    @Test
    @DisplayName("????????? ???????????? ?????????????????? recruitment?????? ????????????.")
    void deleteRecruitmentMemberWhenMemberDeleted() {
        memberRepository.save(testAuthor);
        Member testMember = testMemberGenerator.getTestMember();
        memberRepository.save(testMember);
        testRecruitment.addMember(testMember);
        writingRepository.save(testRecruitment);

        setAuthentication(testAuthor);

        memberDeleteService.delete();

        List<RecruitmentMember> allByMember = recruitmentMemberRepository.findAllByMember(testAuthor);

        assertThat(allByMember.size(), equalTo(0));
    }

    @Test
    @DisplayName("????????? ???????????? ????????? ???????????? ????????? ????????????.")
    void deleteMemberAgreementWhenMemberDeleted() {
        memberRepository.save(testAuthor);

        agreementRepository.save(MemberAgreement.builder()
                .member(testAuthor)
                .privacyAgreement(true)
                .serviceAgreement(true)
                .build());

        setAuthentication(testAuthor);

        memberDeleteService.delete();

        Optional<MemberAgreement> byMember = agreementRepository.findByMember(testAuthor);

        assertThat(byMember, equalTo(Optional.empty()));
    }

    @Test
    @DisplayName("????????? ???????????? ????????? ?????? ????????? ????????????.")
    void deleteReportWhenMemberDeleted() {
        memberRepository.save(testAuthor);
        writingRepository.save(testRecruitment);
        writingRepository.save(testComment);

        reportRepository.save(Report.builder()
                .member(testAuthor)
                .writing(testRecruitment)
                .build());
        reportRepository.save(Report.builder()
                .member(testAuthor)
                .writing(testComment)
                .build());

        setAuthentication(testAuthor);

        memberDeleteService.delete();

        List<Report> allByMember = reportRepository.findAllByMember(testAuthor);

        assertThat(allByMember.size(), equalTo(0));
    }

    @Test
    @DisplayName("????????? ????????????.")
    void deleteMember() {
        memberRepository.save(testAuthor);

        setAuthentication(testAuthor);

        memberDeleteService.delete();

        Optional<Member> memberByEmail = memberRepository.findMemberByEmail(testAuthor.getEmail());

        assertThat(memberByEmail, equalTo(Optional.empty()));
    }

    @Test
    @DisplayName("?????? ?????? ??? ???????????? ?????? ????????? ?????? ????????? ????????????.")
    void deleteMemberFailWithNotFound() {
        setAuthentication(testAuthor);

        assertThrows(MemberNotFoundException.class, () -> {
            memberDeleteService.delete();
        });
    }

}
