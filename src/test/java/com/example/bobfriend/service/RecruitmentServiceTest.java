package com.example.bobfriend.service;

import com.example.bobfriend.model.dto.member.Preview;
import com.example.bobfriend.model.dto.recruitment.Create;
import com.example.bobfriend.model.dto.recruitment.DetailResponse;
import com.example.bobfriend.model.entity.*;
import com.example.bobfriend.model.exception.AlreadyJoined;
import com.example.bobfriend.model.exception.MemberNotAllowedException;
import com.example.bobfriend.model.exception.RecruitmentNotFoundException;
import com.example.bobfriend.repository.RecruitmentRepository;
import com.example.bobfriend.repository.WritingReportRepository;
import com.example.bobfriend.util.TestMemberGenerator;
import com.example.bobfriend.util.TestRecruitmentGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecruitmentServiceTest {
    @Mock
    RecruitmentRepository recruitmentRepository;
    @Mock
    MemberService memberService;
    @Mock
    WritingReportRepository reportRepository;
    @InjectMocks
    RecruitmentService recruitmentService;

    Recruitment testRecruitment;
    Member testAuthor;

    private TestMemberGenerator testMemberGenerator = new TestMemberGenerator();
    private TestRecruitmentGenerator testRecruitmentGenerator = new TestRecruitmentGenerator();

    @BeforeEach
    public void setup() {
        testAuthor = testMemberGenerator.getTestAuthor();
        testRecruitmentGenerator.setAuthor(testAuthor);

        testRecruitment = testRecruitmentGenerator.getTestRecruitment();

        testRecruitment.setup();
        testAuthor.setup();
    }

    @Test
    @DisplayName("recruitment를 생성하면 작성자에 현재 사용자가 저장된다.")
    public void createTest() {
        when(memberService.getCurrentMember())
                .thenReturn(testAuthor);
        when(recruitmentRepository.save(any()))
                .thenReturn(testRecruitment);

        Create requestDto = new Create(testRecruitment);

        DetailResponse add = recruitmentService.create(requestDto);

        assertThat(add.getAuthor().getId(),
                equalTo(testRecruitment.getAuthor().getId()));
    }

    @Test
    @DisplayName("join메소드를 호출하면 현재 사용자가 해당 recruitment에 추가된다.")
    public void joinTest() throws AlreadyJoined {
        Member testMember = testMemberGenerator.getTestMember();
        testMember.setup();

        when(memberService.getCurrentMember()).thenReturn(testMember); // testMember가 참여를 요청하는 상황

        when(recruitmentRepository.findById(testRecruitment.getId()))
                .thenReturn(Optional.ofNullable(testRecruitment));

        DetailResponse recruitmentDetailResponseDto =
                recruitmentService.joinOrUnjoin(testRecruitment.getId());

        Set<Preview> members = recruitmentDetailResponseDto.getMembers();
        assertTrue(members.contains(new Preview(testMember)));
    }

    @Test
    @DisplayName("unjoin메소드를 호출하면 현재 사용자가 해당 recruitment에서 제거된다.")
    public void unJoinTest() {
        Member testMember = testMemberGenerator.getTestMember();
        testMember.setup();

        when(memberService.getCurrentMember()).thenReturn(testMember); // testMember가 참여를 요청하는 상황

        given(recruitmentRepository.findById(testRecruitment.getId()))
                .willReturn(Optional.ofNullable(testRecruitment));
        testRecruitment.getMembers().add(testMember);

        DetailResponse recruitmentDetailResponseDto =
                recruitmentService.joinOrUnjoin(testRecruitment.getId());

        Set<Preview> members = recruitmentDetailResponseDto.getMembers();
        assertFalse(members.contains(new Preview(testMember)));
    }

    @Test
    @DisplayName("delete를 호출했을 때 해당하는 recruitment가 존재하지 않으면 예외가 발생한다.")
    void deleteRecruitmentTestFailWithRecruitmentNotFound() {
        when(memberService.getCurrentMember())
                .thenReturn(testAuthor);
        when(recruitmentRepository.findById(any()))
                .thenReturn(Optional.empty());

        assertThrows(RecruitmentNotFoundException.class, () -> {
            recruitmentService.delete(testRecruitment.getId());
        });
    }

    @Test
    @DisplayName("delete를 호출했을 때 현재 사용자가 작성자가 아니면 예외가 발생한다.")
    void deleteRecruitmentTestFailWithMemberNotAllowed() {
        Member testMember = testMemberGenerator.getTestMember();

        when(memberService.getCurrentMember())
                .thenReturn(testMember);
        when(recruitmentRepository.findById(any()))
                .thenReturn(Optional.ofNullable(testRecruitment));

        assertThrows(MemberNotAllowedException.class, () -> {
            recruitmentService.delete(testRecruitment.getId());
        });
    }

    @Test
    @DisplayName("close 호출 시 해당 recruitment가 마감된다.")
    void closeRecruitmentTest() {
        when(recruitmentRepository.findById(any()))
                .thenReturn(Optional.ofNullable(testRecruitment));
        when(memberService.getCurrentMember())
                .thenReturn(testAuthor);

        recruitmentService.close(testRecruitment.getId());

        assertThat(testRecruitment.isActive(), equalTo(false));
    }

    @Test
    @DisplayName("close를 호출했을 때 현재 사용자가 작성자가 아닐 경우 예외가 발생한다.")
    void closeRecruitmentTestFail() {
        when(recruitmentRepository.findById(any()))
                .thenReturn(Optional.ofNullable(testRecruitment));
        when(memberService.getCurrentMember())
                .thenReturn(testMemberGenerator.getTestMember());

        assertThrows(MemberNotAllowedException.class, () -> {
            recruitmentService.close(testRecruitment.getId());
        });
    }

    // FIXME: 2022-03-19 join, unjoin 실패 테스트가 필요함
}