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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RecruitmentService {
    private final RecruitmentRepository recruitmentRepository;
    private final ReportService reportService;
    private final MemberService memberService;


    public RecruitmentDto.Response findById(Long recruitmentId) {
        Recruitment recruitment = getRecruitment(recruitmentId);
        return new RecruitmentDto.Response(recruitment);
    }


    public Page<RecruitmentDto.Response> findAll(Pageable pageable) {
        Page<Recruitment> all = recruitmentRepository.findAll(pageable);
        return all.map(RecruitmentDto.Response::new);
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
        Recruitment recruitment = getRecruitment(recruitmentId);
        if (currentMember.equals(recruitment.getAuthor()))
            recruitmentRepository.deleteById(recruitmentId);
        else
            throw new MemberNotAllowedException(currentMember.getEmail());
    }


    public Page<RecruitmentDto.Response> findAllByRestaurantNameAndRestaurantAddress(
            String restaurantName,
            String restaurantAddress,
            Pageable pageable) {
        List<RecruitmentDto.Response> collect = recruitmentRepository
                .findAllByRestaurantNameAndRestaurantAddress(restaurantName,
                        restaurantAddress, pageable).stream()
                .map(RecruitmentDto.Response::new)
                .collect(Collectors.toList());
        return new PageImpl<>(collect);
    }


    public Page<RecruitmentDto.Response> findAllByRestaurantAddress(String restaurantAddress, Pageable pageable) {
        List<RecruitmentDto.Response> collect = recruitmentRepository
                .findAllByRestaurantAddress(restaurantAddress, pageable).stream()
                .map(RecruitmentDto.Response::new)
                .collect(Collectors.toList());
        return new PageImpl<>(collect);
    }


    public Page<RecruitmentDto.Response> findAllAvailableRecruitments(Pageable pageable) {
        Member currentMember = memberService.getCurrentMember();
        Page<Recruitment> all = recruitmentRepository.findAll(pageable);

        List<RecruitmentDto.Response> map = all.filter(recruitment ->
                        !recruitment.hasMember(currentMember) &&
                                !currentMember.equals(recruitment.getAuthor()))
                .map(RecruitmentDto.Response::new).toList();
        return new PageImpl<>(map);
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


    public Page<RecruitmentDto.Response> findAllJoinedRecruitments(Pageable pageable) {
        Member author = memberService.getCurrentMember();
        List<RecruitmentDto.Response> collect = recruitmentRepository.findAll(pageable).stream()
                .filter(recruitment ->
                        recruitment.hasMember(author) ||
                                author.equals(recruitment.getAuthor())
                ).map(RecruitmentDto.Response::new)
                .collect(Collectors.toList());
        return new PageImpl<>(collect);
    }


    public Page<RecruitmentDto.Response> findMyRecruitments(Pageable pageable) {
        Member author = memberService.getCurrentMember();
        List<RecruitmentDto.Response> collect = recruitmentRepository.findAllByAuthor(author, pageable).stream()
                .map(RecruitmentDto.Response::new)
                .collect(Collectors.toList());
        return new PageImpl<>(collect);
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

        validateRecruitment(recruitment);

        if (recruitment.hasMember(currentMember))
            recruitment.removeMember(currentMember);
        else
            recruitment.addMember(currentMember);

        return new RecruitmentDto.Response(recruitment);
    }



    public Page<RecruitmentDto.Response> searchTitle(String keyword, Pageable pageable) {
        List<RecruitmentDto.Response> collect = recruitmentRepository
                .findAllByTitleContaining(keyword, pageable)
                .stream().map(RecruitmentDto.Response::new)
                .collect(Collectors.toList());
        return new PageImpl<>(collect);
    }

    public Page<RecruitmentDto.Response> searchContent(String keyword, Pageable pageable) {
        List<RecruitmentDto.Response> collect = recruitmentRepository
                .findAllByContentContaining(keyword, pageable)
                .stream().map(RecruitmentDto.Response::new)
                .collect(Collectors.toList());
        return new PageImpl<>(collect);
    }

    public Page<RecruitmentDto.Response> searchRestaurant(String keyword, Pageable pageable) {
        List<RecruitmentDto.Response> collect = recruitmentRepository
                .findAllByRestaurantNameContaining(keyword, pageable)
                .stream().map(RecruitmentDto.Response::new)
                .collect(Collectors.toList());
        return new PageImpl<>(collect);
    }


    public void reportRecruitment(Long recruitmentId) {
        Recruitment recruitment = getRecruitment(recruitmentId);
        Member member = recruitment.getAuthor();
        reportService.reportWriting(member, recruitment);
    }




    private void validateRecruitment(Recruitment recruitment) {
        if (recruitment.isFull())
            throw new RecruitmentIsFullException(recruitment.getId());
        if (!recruitment.isActive())
            throw new RecruitmentNotActiveException(recruitment.getId());
    }


    private Recruitment getRecruitment(Long recruitmentId) {
        return recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> {
                    throw new RecruitmentNotFoundException(recruitmentId);
                });
    }

}
