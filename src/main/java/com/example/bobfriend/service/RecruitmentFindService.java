package com.example.bobfriend.service;

import com.example.bobfriend.model.dto.recruitment.Address;
import com.example.bobfriend.model.dto.recruitment.Addresses;
import com.example.bobfriend.model.dto.recruitment.DetailResponse;
import com.example.bobfriend.model.dto.recruitment.SimpleResponse;
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
    public DetailResponse findById(Long recruitmentId) {
        Recruitment recruitment = getRecruitment(recruitmentId);
        return new DetailResponse(recruitment);
    }


    @Transactional
    public Page<SimpleResponse> findAll(Pageable pageable) {
        return recruitmentRepository.findAll(pageable)
                .map(SimpleResponse::new);
    }


    @Transactional
    public Page<SimpleResponse> findAllByRestaurantAddress(
            String address,
            Pageable pageable) {
        return recruitmentRepository
                .findAllByAddress(address, pageable)
                .map(SimpleResponse::new);
    }


    @Transactional
    public Page<SimpleResponse> findAllAvailable(Pageable pageable) {
        Member currentMember = memberService.getCurrentMember();
        return recruitmentRepository
                .findAllAvailable(currentMember, pageable)
                .map(SimpleResponse::new);
    }


    @Transactional
    public Addresses findAllLocations(Double latitude, Double longitude, Integer zoomLevel) {
        Map<Address, Integer> addressMap = new HashMap();

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
        return new Addresses(new ArrayList<>(addressMap.keySet()));
    }


    @Transactional
    public Page<SimpleResponse> findAllJoined(Pageable pageable) {
        Member author = memberService.getCurrentMember();
        return recruitmentRepository.findAllJoined(author, pageable)
                .map(SimpleResponse::new);
    }


    @Transactional
    public Page<SimpleResponse> findMyRecruitments(Pageable pageable) {
        Member author = memberService.getCurrentMember();
        return recruitmentRepository.findAllByAuthor(author, pageable)
                .map(SimpleResponse::new);
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
