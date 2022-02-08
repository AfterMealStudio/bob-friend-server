package com.example.bobfriend.service;

import com.example.bobfriend.model.entity.Member;
import com.example.bobfriend.model.entity.Writing;
import com.example.bobfriend.repository.MemberRepository;
import com.example.bobfriend.repository.RecruitmentMemberRepository;
import com.example.bobfriend.repository.WritingReportRepository;
import com.example.bobfriend.repository.WritingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberDeleteService {
    private final MemberRepository memberRepository;
    private final WritingReportRepository reportRepository;
    private final RecruitmentMemberRepository recruitmentMemberRepository;
    private final WritingRepository writingRepository;
    private final MemberService memberService;

    @Transactional
    public void delete() {
        Member currentMember = memberService.getCurrentMember();

        recruitmentMemberRepository.deleteAllByMember(currentMember);

        for (Writing writing : writingRepository.findAllByAuthor(currentMember)) {
            writing.setAuthor(null);
        }

        reportRepository.deleteAllByMember(currentMember);

        memberRepository.delete(currentMember);
    }

}
