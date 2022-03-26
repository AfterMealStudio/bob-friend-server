package com.example.bobfriend.service;

import com.example.bobfriend.model.dto.recruitment.*;
import com.example.bobfriend.model.entity.Member;
import com.example.bobfriend.model.entity.Recruitment;
import com.example.bobfriend.model.entity.Sex;
import com.example.bobfriend.model.exception.*;
import com.example.bobfriend.repository.RecruitmentRepository;
import com.example.bobfriend.repository.WritingReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class RecruitmentService {
    private final RecruitmentRepository recruitmentRepository;
    private final ReportService reportService;
    private final WritingReportRepository reportRepository;
    private final MemberService memberService;


    @Transactional
    public DetailResponse create(Create recruitmentRequestDto) {
        Member currentMember = memberService.getCurrentMember();
        Recruitment recruitment = recruitmentRequestDto.convertToDomain();

        recruitment.setAuthor(currentMember);

        Recruitment savedRecruitment = recruitmentRepository.save(recruitment);
        return new DetailResponse(savedRecruitment);
    }


    @Transactional
    public void delete(Long recruitmentId) {
        Member currentMember = memberService.getCurrentMember();
        Recruitment recruitment = getRecruitment(recruitmentId);
        if (currentMember.equals(recruitment.getAuthor())) {

            reportRepository.deleteAllByWriting(recruitment);
            recruitmentRepository.delete(recruitment);
        } else
            throw new MemberNotAllowedException(currentMember.getEmail());
    }


    @Transactional
    public DetailResponse close(Long recruitmentId) {
        Member author = memberService.getCurrentMember();
        Recruitment recruitment = getRecruitment(recruitmentId);
        if (author.equals(recruitment.getAuthor())) {
            recruitment.close();
        } else {
            throw new MemberNotAllowedException(author.getNickname());
        }
        return new DetailResponse(recruitment);
    }


    @Transactional
    public DetailResponse joinOrUnjoin(Long recruitmentId)
            throws RecruitmentIsFullException, RecruitmentNotActiveException {
        Member currentMember = memberService.getCurrentMember();

        Recruitment recruitment = getRecruitment(recruitmentId);
        Member author = recruitment.getAuthor();

        if (author.isUnknown() || !recruitment.isActive())
            throw new RecruitmentNotActiveException(recruitmentId);

        if (currentMember.equals(author)) {
            throw new AlreadyJoined(currentMember.getNickname());
        }

        // 연령 체크
        if (!recruitment.isAgeRestrictionSatisfied(currentMember.getAge()))
            throw new RestrictionFailedException(currentMember.getEmail());

        if (!checkSexRestriction(recruitment, currentMember))
            throw new RestrictionFailedException(currentMember.getEmail());

        if (recruitment.hasMember(currentMember))
            recruitment.removeMember(currentMember);
        else
            recruitment.addMember(currentMember);

        return new DetailResponse(recruitment);
    }

    @Transactional
    public void expire() {
        List<Recruitment> allByActiveTrue = recruitmentRepository.findAllByActiveTrue();
        for (Recruitment recruitment :
                allByActiveTrue) {
            checkAppointmentTime(recruitment);
        }
    }


    private Recruitment getRecruitment(Long recruitmentId) {
        Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> {
                    throw new RecruitmentNotFoundException(recruitmentId);
                });
        if (!recruitment.isActive()) {
            throw new RecruitmentNotActiveException(recruitmentId);
        }
        return recruitment;
    }


    private boolean checkSexRestriction(Recruitment recruitment,
                                        Member member) {
        Sex restriction = recruitment.getSexRestriction();
        if (restriction.equals(Sex.NONE) ||
                restriction.equals(member.getSex())) {
            return true;
        }
        return false;
    }


    private Recruitment checkAppointmentTime(Recruitment recruitment) {
        if (LocalDateTime.now().isAfter(recruitment.getEndAt()))
            recruitment.close();
        return recruitment;
    }


}
