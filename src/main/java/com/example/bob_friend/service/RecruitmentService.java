package com.example.bob_friend.service;

import com.example.bob_friend.model.dto.RecruitmentDto;
import com.example.bob_friend.model.entity.Member;
import com.example.bob_friend.model.entity.Recruitment;
import com.example.bob_friend.model.exception.MemberNotAllowedException;
import com.example.bob_friend.model.exception.RecruitmentIsFullException;
import com.example.bob_friend.model.exception.RecruitmentNotActiveException;
import com.example.bob_friend.model.exception.RecruitmentNotFoundException;
import com.example.bob_friend.repository.RecruitmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RecruitmentService {
    private final RecruitmentRepository recruitmentRepository;
    private final MemberService memberService;

    public RecruitmentDto.Response findById(Long recruitmentId) {
        Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> {
                    throw new RecruitmentNotFoundException(recruitmentId);
                });
        return new RecruitmentDto.Response(recruitment);
    }


    public List<RecruitmentDto.Response> findAll() {
        return recruitmentRepository.findAll().stream()
                .map(RecruitmentDto.Response::new)
                .collect(Collectors.toList());
    }


    public RecruitmentDto.Response createRecruitment(RecruitmentDto.Request recruitmentRequestDto) {
        Member currentMember = memberService.getCurrentMember();
        Recruitment recruitment = recruitmentRequestDto.convertToDomain();
        recruitment.setAuthor(currentMember);
        Recruitment savedRecruitment = recruitmentRepository.save(recruitment);
        return new RecruitmentDto.Response(savedRecruitment);
    }


    public void delete(Long recruitmentId) {
        Member currentMember = memberService.getCurrentMember();
        Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> {
                    throw new RecruitmentNotFoundException(recruitmentId);
                });
        if (recruitment.getAuthor().equals(currentMember))
            recruitmentRepository.deleteById(recruitmentId);
        else
            throw new MemberNotAllowedException(currentMember.getEmail());
    }


//    public RecruitmentDto.Response update(Long recruitmentId,
//                                          RecruitmentDto.Request update) {
//        Recruitment recruitment = Recruitment.builder()
//                .id(recruitmentId)
//                .title(update.getTitle())
//                .content(update.getContent())
//                .build();
//
//        Recruitment savedRecruitment = recruitmentRepository.save(recruitment);
//        return new RecruitmentDto.Response(savedRecruitment);
//    }


    public List<RecruitmentDto.Response> findAllByRestaurantNameAndRestaurantAddress(
            String restaurantName,
            String restaurantAddress) {
        return recruitmentRepository
                .findAllByRestaurantNameAndRestaurantAddress(restaurantName,
                        restaurantAddress).stream()
                .map(RecruitmentDto.Response::new)
                .collect(Collectors.toList());
    }


    public List<RecruitmentDto.Response> findAllByRestaurantAddress(String restaurantAddress) {
        return recruitmentRepository
                .findAllByRestaurantAddress(restaurantAddress).stream()
                .map(RecruitmentDto.Response::new)
                .collect(Collectors.toList());
    }


    public List<RecruitmentDto.Response> findAllAvailableRecruitments() {
        Member currentMember = memberService.getCurrentMember();
        return recruitmentRepository.findAll().stream()
                .filter(recruitment ->
                        !recruitment.hasMember(currentMember) &&
                                !recruitment.getAuthor().equals(currentMember))
                .map(RecruitmentDto.Response::new)
                .collect(Collectors.toList());
    }

    public Set<RecruitmentDto.Address> findAllAvailableLocations() {
        Map<RecruitmentDto.Address, Integer> addressMap = new HashMap();

        Iterator<Recruitment> iterator =
                recruitmentRepository.findAll().iterator();
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


    public List<RecruitmentDto.Response> findAllJoinedRecruitments() {
        Member author = memberService.getCurrentMember();
        return recruitmentRepository.findAll().stream()
                .filter(recruitment ->
                        recruitment.hasMember(author) ||
                                recruitment.getAuthor().equals(author)
                ).map(RecruitmentDto.Response::new)
                .collect(Collectors.toList());
    }


    public List<RecruitmentDto.Response> findMyRecruitments() {
        Member author = memberService.getCurrentMember();
        return recruitmentRepository.findAllByAuthor(author).stream()
                .map(RecruitmentDto.Response::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public RecruitmentDto.Response closeRecruitment(Long recruitmentId) {
        Member author = memberService.getCurrentMember();
        Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> {
                    throw new RecruitmentNotFoundException(recruitmentId);
                });
        if (recruitment.getAuthor().equals(author)) {
            recruitment.setActive(false);
        } else {
            throw new MemberNotAllowedException(author.getNickname());
        }
        return new RecruitmentDto.Response(recruitment);
    }

    @Transactional
    public RecruitmentDto.Response joinOrUnjoin(Long recruitmentId)
            throws RecruitmentIsFullException, RecruitmentNotActiveException {
        Member currentMember = memberService.getCurrentMember();

        Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> {
                    throw new RecruitmentNotFoundException(recruitmentId);
                });

        validateRecruitment(recruitment);

        if (recruitment.hasMember(currentMember))
            recruitment.removeMember(currentMember);
        else
            recruitment.addMember(currentMember);

        return new RecruitmentDto.Response(recruitment);
    }


    private void validateRecruitment(Recruitment recruitment) {
        if (recruitment.isFull())
            throw new RecruitmentIsFullException(recruitment.getId());
        if (!recruitment.isActive())
            throw new RecruitmentNotActiveException(recruitment.getId());
    }

}
