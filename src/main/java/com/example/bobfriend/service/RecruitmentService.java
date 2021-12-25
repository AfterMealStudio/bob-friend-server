package com.example.bobfriend.service;

import com.example.bobfriend.model.dto.Condition;
import com.example.bobfriend.model.dto.recruitment.Address;
import com.example.bobfriend.model.dto.recruitment.Create;
import com.example.bobfriend.model.dto.recruitment.Response;
import com.example.bobfriend.model.dto.recruitment.ResponseCollection;
import com.example.bobfriend.model.entity.Member;
import com.example.bobfriend.model.entity.Recruitment;
import com.example.bobfriend.model.entity.Sex;
import com.example.bobfriend.model.exception.*;
import com.example.bobfriend.repository.RecruitmentRepository;
import com.example.bobfriend.repository.WritingReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Service
public class RecruitmentService {
    private final RecruitmentRepository recruitmentRepository;
    private final ReportService reportService;
    private final WritingReportRepository reportRepository;
    private final MemberService memberService;

    @Transactional
    public Response findById(Long recruitmentId) {
        Recruitment recruitment = getRecruitment(recruitmentId);
        return new Response(recruitment);
    }

    @Transactional
    public Page<ResponseCollection> findAll(Pageable pageable) {
        return recruitmentRepository.findAll(pageable)
                .map(ResponseCollection::new);
    }


    @Transactional
    public Response createRecruitment(Create recruitmentRequestDto) {
        Member currentMember = memberService.getCurrentMember();
        Recruitment recruitment = recruitmentRequestDto.convertToDomain();
        recruitment.setAuthor(currentMember);
        Recruitment savedRecruitment = recruitmentRepository.save(recruitment);
        return new Response(savedRecruitment);
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
    public Page<ResponseCollection> findAllByRestaurant(
            Condition.Search searchCondition,
            Pageable pageable) {
        return recruitmentRepository
                .findAllByRestaurant(searchCondition, pageable)
                .map(ResponseCollection::new);
    }


    @Transactional
    public Page<ResponseCollection> findAllAvailableRecruitments(Pageable pageable) {
        Member currentMember = memberService.getCurrentMember();
        return recruitmentRepository
                .findAllAvailable(currentMember, pageable)
                .map(ResponseCollection::new);
    }

    @Transactional
    public RecruitmentDto.AddressCollection findAllLocations(Double latitude, Double longitude, Integer zoomLevel) {
        Map<RecruitmentDto.Address, Integer> addressMap = new HashMap();

        // 0.05가 지도 상에서 대충 500m 정도
        // 줌 레벨이 -2 ~ 12까지의 값을 가짐
        double bound = 0.05 * (zoomLevel + 3);

        List<Recruitment> allByLocation = recruitmentRepository.findAllByLocation(latitude, longitude, bound);
        Iterator<Recruitment> iterator =
                allByLocation.iterator();

        while (iterator.hasNext()) {
            Recruitment recruitment = iterator.next();
            Address address =
                    new Address(recruitment);
            addressMap.put(address, addressMap.getOrDefault(address, 0) + 1);
        }

        for (Map.Entry<Address, Integer> entry :
                addressMap.entrySet()) {
            entry.getKey().setCount(entry.getValue());
        }
        return new RecruitmentDto.AddressCollection(new ArrayList<>(addressMap.keySet()));
    }

    @Transactional
    public Page<ResponseCollection> findAllJoinedRecruitments(Pageable pageable) {
        Member author = memberService.getCurrentMember();
        return recruitmentRepository.findAllJoined(author, pageable)
                .map(ResponseCollection::new);
    }

    @Transactional
    public Page<ResponseCollection> findMyRecruitments(Pageable pageable) {
        Member author = memberService.getCurrentMember();
        return recruitmentRepository.findAllByAuthor(author, pageable)
                .map(ResponseCollection::new);
    }

    @Transactional
    public Response closeRecruitment(Long recruitmentId) {
        Member author = memberService.getCurrentMember();
        Recruitment recruitment = getRecruitment(recruitmentId);
        if (author.equals(recruitment.getAuthor())) {
            recruitment.close();
        } else {
            throw new MemberNotAllowedException(author.getNickname());
        }
        return new Response(recruitment);
    }


    @Transactional
    public Response joinOrUnjoin(Long recruitmentId)
            throws RecruitmentIsFullException, RecruitmentNotActiveException {
        Member currentMember = memberService.getCurrentMember();

        Recruitment recruitment = getRecruitment(recruitmentId);
        Member author = recruitment.getAuthor();

        if (author.isUnknown() || !recruitment.isActive())
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

        return new Response(recruitment);
    }


    public Page<Response> searchTitle(Condition.Search search, Pageable pageable) {
        return recruitmentRepository
                .searchByTitle(search, pageable)
                .map(Response::new);
    }


    public Page<Response> searchContent(Condition.Search search, Pageable pageable) {
        return recruitmentRepository
                .searchByContent(search, pageable)
                .map(Response::new);
    }

    public Page<Response> searchRestaurant(Condition.Search search, Pageable pageable) {
        return recruitmentRepository
                .searchByRestaurant(search, pageable)
                .map(Response::new);
    }


    public Page<Response> searchByAllCondition(Condition.Search search, Pageable pageable) {
        return recruitmentRepository
                .searchByAll(search, pageable)
                .map(Response::new);
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
