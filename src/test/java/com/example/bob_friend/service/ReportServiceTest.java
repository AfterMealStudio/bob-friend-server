package com.example.bob_friend.service;

import com.example.bob_friend.model.Constant;
import com.example.bob_friend.model.entity.Member;
import com.example.bob_friend.model.entity.Reply;
import com.example.bob_friend.model.entity.Sex;
import com.example.bob_friend.repository.WritingReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {
    @Mock
    WritingReportRepository reportRepository;
    @InjectMocks
    ReportService reportService;

    Reply testReply;
    Member testAuthor;

    @BeforeEach
    void setup() {
        testAuthor = Member.builder()
                .id(1)
                .email("testAuthor@test.com")
                .nickname("testAuthor")
                .password("testPassword")
                .sex(Sex.FEMALE)
                .birth(LocalDate.now())
                .reportCount(0)
                .accumulatedReports(0)
                .active(true)
                .build();

        testReply = Reply.builder()
                .id(1L)
                .content("test content")
                .author(testAuthor)
                .reportCount(0)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void reportOneTime() {
        when(reportRepository.existsByMemberAndWriting(any(), any()))
                .thenReturn(false);
        reportService.reportWriting(testReply.getAuthor(), testReply);
        assertThat(testReply.getReportCount(), equalTo(1));
    }

    @Test
    void reportMoreThanLimit() {
        when(reportRepository.existsByMemberAndWriting(any(), any()))
                .thenReturn(false);

        for (int i = 0; i <= Constant.REPORT_LIMIT; i++) {
            reportService.reportWriting(testReply.getAuthor(), testReply);
        }

        assertThat(testAuthor.getReportCount(), equalTo(1));
    }

    @Test
    void reportUntilSuspend() {
        when(reportRepository.existsByMemberAndWriting(any(), any()))
                .thenReturn(false);

        for (int i = 0; i <= Math.pow(Constant.REPORT_LIMIT + 1, 2); i++) {
            reportService.reportWriting(testReply.getAuthor(), testReply);
        }

        LocalDate now = LocalDate.now();
        assertThat(testAuthor.getAccumulatedReports(), equalTo(1));
        assertThat(testAuthor.getReportStart(),equalTo(now));
        assertThat(testAuthor.getReportEnd(), equalTo(now.plusDays(
                Constant.REPORT_SUSPENSION_PERIOD * testAuthor.getAccumulatedReports())));
        assertThat(testAuthor.isActive(), equalTo(false));

    }

}
