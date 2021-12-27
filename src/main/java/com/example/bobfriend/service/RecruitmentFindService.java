package com.example.bobfriend.service;

import com.example.bobfriend.model.dto.Condition;
import com.example.bobfriend.model.dto.RecruitmentDto;
import com.example.bobfriend.model.entity.Member;
import com.example.bobfriend.model.entity.Recruitment;
import com.example.bobfriend.model.exception.RecruitmentNotActiveException;
import com.example.bobfriend.model.exception.RecruitmentNotFoundException;
import com.example.bobfriend.repository.RecruitmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@RequiredArgsConstructor
@Service
public class RecruitmentFindService {
    private final RecruitmentRepository recruitmentRepository;
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
    public Page<RecruitmentDto.ResponseList> findAllByRestaurant(
            Condition.Search searchCondition,
            Pageable pageable) {
        return recruitmentRepository
                .findAllByRestaurant(searchCondition, pageable)
                .map(RecruitmentDto.ResponseList::new);
    }


    @Transactional
    public Page<RecruitmentDto.ResponseList> findAllAvailable(Pageable pageable) {
        Member currentMember = memberService.getCurrentMember();
        return recruitmentRepository
                .findAllAvailable(currentMember, pageable)
                .map(RecruitmentDto.ResponseList::new);
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
            RecruitmentDto.Address address =
                    new RecruitmentDto.Address(recruitment);
            addressMap.put(address, addressMap.getOrDefault(address, 0) + 1);
        }

        for (Map.Entry<RecruitmentDto.Address, Integer> entry :
                addressMap.entrySet()) {
            entry.getKey().setCount(entry.getValue());
        }
        return new RecruitmentDto.AddressCollection(new ArrayList<>(addressMap.keySet()));
    }


    @Transactional
    public Page<RecruitmentDto.ResponseList> findAllJoined(Pageable pageable) {
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
}
