package com.example.bobfriend.service;

import com.example.bobfriend.model.Constant;
import com.example.bobfriend.model.entity.Member;
import com.example.bobfriend.model.entity.Recruitment;
import com.example.bobfriend.model.entity.Reply;
import com.example.bobfriend.model.entity.Sex;
import com.example.bobfriend.model.exception.AlreadyReportedExeption;
import com.example.bobfriend.repository.WritingReportRepository;
import com.example.bobfriend.util.TestMemberGenerator;
import com.example.bobfriend.util.TestRecruitmentGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {
    @Mock
    WritingReportRepository reportRepository;
    @InjectMocks
    ReportService reportService;

    Recruitment testRecruitment;
    Member testAuthor;
    private TestRecruitmentGenerator testRecruitmentGenerator = new TestRecruitmentGenerator();
    private TestMemberGenerator testMemberGenerator = new TestMemberGenerator();

    @BeforeEach
    void setup() {
        testAuthor = testMemberGenerator.getTestAuthor();
        testRecruitmentGenerator.setAuthor(testAuthor);
        testRecruitment = testRecruitmentGenerator.getTestRecruitment();
    }

    @Test
    @DisplayName("현재 사용자가 특정 게시물을 신고하면 해당 게시물의 report count가 1 증가한다.")
    void reportOneTime() {
        when(reportRepository.existsByMemberAndWriting(any(), any()))
                .thenReturn(false);
        reportService.reportWriting(testRecruitment.getAuthor(), testRecruitment);
        assertThat(testRecruitment.getReportCount(), equalTo(1));
    }

    @Test
    @DisplayName("한 사용자는 한 게시물에 대해 한 번만 신고할 수 있다.")
    void reportFailWithAlreadyReported() {
        when(reportRepository.existsByMemberAndWriting(any(), any()))
                .thenReturn(true);
        assertThrows(AlreadyReportedExeption.class, () -> {
            reportService.reportWriting(testRecruitment.getAuthor(), testRecruitment);
        });

    }

    @Test
    @DisplayName("일정 횟수 이상 신고당한 게시물의 작성자는 report count가 1 증가한다.")
    void reportMoreThanLimit() {
        when(reportRepository.existsByMemberAndWriting(any(), any()))
                .thenReturn(false);

        for (int i = 0; i <= Constant.REPORT_LIMIT; i++) {
            reportService.reportWriting(testRecruitment.getAuthor(), testRecruitment);
        }

        assertThat(testAuthor.getReportCount(), equalTo(1));
    }

    @Test
    @DisplayName("사용자의 report count가 일정 이상 누적되면 사용자가 일정 기간 정지된다.")
    void reportUntilSuspend() {
        when(reportRepository.existsByMemberAndWriting(any(), any()))
                .thenReturn(false);

        for (int i = 0; i <= Math.pow(Constant.REPORT_LIMIT + 1, 2); i++) {
            reportService.reportWriting(testRecruitment.getAuthor(), testRecruitment);
        }

        LocalDate now = LocalDate.now();
        assertThat(testAuthor.getAccumulatedReports(), equalTo(1));
        assertThat(testAuthor.getReportStart(),equalTo(now));
        assertThat(testAuthor.getReportEnd(), equalTo(now.plusDays(
                Constant.REPORT_SUSPENSION_PERIOD * testAuthor.getAccumulatedReports())));
        assertThat(testAuthor.isActive(), equalTo(false));

    }

}
