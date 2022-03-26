package com.example.bobfriend.service;

import com.example.bobfriend.model.entity.Member;
import com.example.bobfriend.model.entity.Report;
import com.example.bobfriend.model.entity.Writing;
import com.example.bobfriend.model.exception.AlreadyReportedExeption;
import com.example.bobfriend.model.exception.MemberNotFoundException;
import com.example.bobfriend.model.exception.WritingNotFoundException;
import com.example.bobfriend.repository.MemberRepository;
import com.example.bobfriend.repository.WritingReportRepository;
import com.example.bobfriend.repository.WritingRepository;
import com.example.bobfriend.util.AuthenticationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ReportService {

    private final WritingReportRepository reportRepository;
    private final MemberRepository memberRepository;
    private final WritingRepository writingRepository;

    @Transactional
    public void reportWriting(Long id) {
        Member member = getCurrentMember();
        Writing writing = getWriting(id);

        if (reportRepository.existsByMemberAndWriting(member, writing))
            throw new AlreadyReportedExeption(writing);

        writing.report();
        Report report = Report.builder()
                .member(member)
                .writing(writing)
                .build();

        reportRepository.save(report);
    }

    private Writing getWriting(Long id) {
        return writingRepository.findById(id).orElseThrow(WritingNotFoundException::new);
    }

    private Member getCurrentMember() {
        String currentUsername = AuthenticationUtil.getCurrentUsername();
        return memberRepository.findMemberByEmail(currentUsername).orElseThrow(MemberNotFoundException::new);
    }
}
