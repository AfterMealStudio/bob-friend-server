package com.example.bobfriend.service;

import com.example.bobfriend.model.entity.Member;
import com.example.bobfriend.model.entity.Report;
import com.example.bobfriend.model.entity.Writing;
import com.example.bobfriend.model.exception.AlreadyReportedExeption;
import com.example.bobfriend.repository.WritingReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ReportService {
    private final WritingReportRepository reportRepository;

    @Transactional
    public void reportWriting(Member member, Writing writing) {
        if (reportRepository.existsByMemberAndWriting(member , writing))
            throw new AlreadyReportedExeption(writing);
        else {
            writing.report();
            Report report = Report.builder()
                    .member(member)
                    .writing(writing)
                    .build();
            reportRepository.save(report);
        }
    }
}
