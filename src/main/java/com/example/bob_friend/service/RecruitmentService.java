package com.example.bob_friend.service;

import com.example.bob_friend.model.dto.Condition;
import com.example.bob_friend.model.dto.RecruitmentDto;
import com.example.bob_friend.model.entity.Member;
import com.example.bob_friend.model.entity.Recruitment;
import com.example.bob_friend.model.entity.Sex;
import com.example.bob_friend.model.exception.*;
import com.example.bob_friend.repository.RecruitmentMemberRepository;
import com.example.bob_friend.repository.RecruitmentRepository;
import com.example.bob_friend.repository.WritingReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RequiredArgsConstructor
@Service
public class RecruitmentService {
    private final RecruitmentRepository recruitmentRepository;
    private final ReportService reportService;
    private final WritingReportRepository reportRepository;
    private final RecruitmentMemberRepository recruitmentMemberRepository;
    private final MemberService memberService;

    @Transactional
    public RecruitmentDto.Response findById(Long recruitmentId) {
        Recruitment recruitment = getRecruitment(recruitmentId);
        return new RecruitmentDto.Response(recruitment);
    }

    @Transactional
    public Page<RecruitmentDto.ResponseList> findAll(Pageable pageable) {
        return recruitmentRepository.findAll(pageable)
                .map(RecruitmentDto.ResponseList::new);
    }


    @Transactional
    public RecruitmentDto.Response createRecruitment(RecruitmentDto.Request recruitmentRequestDto) {
        Member currentMember = memberService.getCurrentMember();
        Recruitment recruitment = recruitmentRequestDto.convertToDomain();
        recruitment.setAuthor(currentMember);
        Recruitment savedRecruitment = recruitmentRepository.save(recruitment);
        return new RecruitmentDto.Response(savedRecruitment);
    }


    @Transactional
    public void deleteRecruitment(Long recruitmentId) {
        Member currentMember = memberService.getCurrentMember();
        Recruitment recruitment = getRecruitment(recruitmentId);
        if (currentMember.equals(recruitment.getAuthor())) {
//            recruitmentMemberRepository.deleteAllByRecruitment(recruitment);
            reportRepository.deleteAllByWriting(recruitment);
            recruitmentRepository.delete(recruitment);
        } else
            throw new MemberNotAllowedException(currentMember.getEmail());
    }


    @Transactional
    public Page<RecruitmentDto.ResponseList> findAllByRestaurant(
            Condition.Search searchCondition,
            Pageable pageable) {
        return recruitmentRepository
                .findAllByRestaurant(searchCondition, pageable)
                .map(RecruitmentDto.ResponseList::new);
    }


    @Transactional
    public Page<RecruitmentDto.ResponseList> findAllAvailableRecruitments(Pageable pageable) {
        Member currentMember = memberService.getCurrentMember();
        return recruitmentRepository
                .findAllAvailable(currentMember, pageable)
                .map(RecruitmentDto.ResponseList::new);
    }

    @Transactional
    public Set<RecruitmentDto.Address> findAllLocations() {
        Map<RecruitmentDto.Address, Integer> addressMap = new HashMap();

        Iterator<Recruitment> iterator =
                recruitmentRepository.findAllByActiveTrue()
                        .iterator();
        while (iterator.hasNext()) {
            Recruitment recruitment = iterator.next();
            RecruitmentDto.Address address =
                    new RecruitmentDto.Address(recruitment);
            addressMap.put(address, addressMap.getOrDefault(address, 0) + 1);
        }

        for (Map.Entry<RecruitmentDto.Address, Integer> entry :
                addressMap.entrySet()) {
            entry.getKey().setCount(entry.getValue());
        }
        return addressMap.keySet();
    }

    @Transactional
    public Page<RecruitmentDto.ResponseList> findAllJoinedRecruitments(Pageable pageable) {
        Member author = memberService.getCurrentMember();
        return recruitmentRepository.findAllJoined(author, pageable)
                .map(RecruitmentDto.ResponseList::new);
    }

    @Transactional
    public Page<RecruitmentDto.ResponseList> findMyRecruitments(Pageable pageable) {
        Member author = memberService.getCurrentMember();
        return recruitmentRepository.findAllByAuthor(author, pageable)
                .map(RecruitmentDto.ResponseList::new);
    }

    @Transactional
    public RecruitmentDto.Response closeRecruitment(Long recruitmentId) {
        Member author = memberService.getCurrentMember();
        Recruitment recruitment = getRecruitment(recruitmentId);
        if (author.equals(recruitment.getAuthor())) {
            recruitment.close();
        } else {
            throw new MemberNotAllowedException(author.getNickname());
        }
        return new RecruitmentDto.Response(recruitment);
    }


    @Transactional
    public RecruitmentDto.Response joinOrUnjoin(Long recruitmentId)
            throws RecruitmentIsFullException, RecruitmentNotActiveException {
        Member currentMember = memberService.getCurrentMember();

        Recruitment recruitment = getRecruitment(recruitmentId);
        Member author = recruitment.getAuthor();
        if (!author.isValid() || !recruitment.isActive())
            throw new RecruitmentNotActiveException(recruitmentId);

        if (currentMember.equals(author)) {
            throw new AlreadyJoined(currentMember.getNickname());
        }

        if (!checkSexRestriction(recruitment, currentMember))
            throw new RestrictionFailedException(currentMember.getEmail());

        if (recruitment.hasMember(currentMember))
            recruitment.removeMember(currentMember);
        else
            recruitment.addMember(currentMember);

        return new RecruitmentDto.Response(recruitment);
    }


    public Page<RecruitmentDto.Response> searchTitle(Condition.Search search, Pageable pageable) {
        return recruitmentRepository
                .searchByTitle(search, pageable)
                .map(RecruitmentDto.Response::new);
    }


    public Page<RecruitmentDto.Response> searchContent(Condition.Search search, Pageable pageable) {
        return recruitmentRepository
                .searchByContent(search, pageable)
                .map(RecruitmentDto.Response::new);
    }

    public Page<RecruitmentDto.Response> searchRestaurant(Condition.Search search, Pageable pageable) {
        return recruitmentRepository
                .searchByRestaurant(search, pageable)
                .map(RecruitmentDto.Response::new);
    }

//    public Page<RecruitmentDto.Response> searchAppointmentTime(String start, String end, Pageable pageable) {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
//        LocalDateTime startTime = LocalDateTime.parse(start, formatter);
//        LocalDateTime endTime = LocalDateTime.parse(end, formatter);
//        return recruitmentRepository.searchByAppointmentTime(
//                        startTime, endTime, pageable)
//                .map(RecruitmentDto.Response::new);
//    }

    public Page<RecruitmentDto.Response> searchByAllCondition(Condition.Search search, Pageable pageable) {
        return recruitmentRepository
                .searchByAll(search, pageable)
                .map(RecruitmentDto.Response::new);
    }


    public void reportRecruitment(Long recruitmentId) {
        Recruitment recruitment = getRecruitment(recruitmentId);
        Member member = memberService.getCurrentMember();
        reportService.reportWriting(member, recruitment);
    }

    @Transactional
    public void expireRecruitment() {
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
