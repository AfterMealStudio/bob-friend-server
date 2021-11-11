package com.example.bob_friend.service;

import com.example.bob_friend.model.dto.RecruitmentDto;
import com.example.bob_friend.model.entity.Member;
import com.example.bob_friend.model.entity.Recruitment;
import com.example.bob_friend.model.entity.Sex;
import com.example.bob_friend.model.exception.*;
import com.example.bob_friend.repository.RecruitmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RecruitmentService {
    private final RecruitmentRepository recruitmentRepository;
    private final ReportService reportService;
    private final MemberService memberService;

    @Transactional
    public RecruitmentDto.Response findById(Long recruitmentId) {
        Recruitment recruitment = checkAppointmentTime(getRecruitment(recruitmentId));
        return new RecruitmentDto.Response(recruitment);
    }

    @Transactional
    public Page<RecruitmentDto.ResponseList> findAll(Pageable pageable) {
        return new PageImpl<>(
                recruitmentRepository.findAll(pageable).stream()
                        .map(this::checkAppointmentTime)
                        .filter(Recruitment::isActive)
                        .collect(Collectors.toList()))
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
    public void delete(Long recruitmentId) {
        Member currentMember = memberService.getCurrentMember();
        Recruitment recruitment = getRecruitment(recruitmentId);
        if (currentMember.equals(recruitment.getAuthor()))
            recruitmentRepository.deleteById(recruitmentId);
        else
            throw new MemberNotAllowedException(currentMember.getEmail());
    }


    @Transactional
    public Page<RecruitmentDto.ResponseList> findAllByRestaurantNameAndRestaurantAddress(
            String restaurantName,
            String restaurantAddress,
            Pageable pageable) {
        List<RecruitmentDto.ResponseList> collect = recruitmentRepository
                .findAllByRestaurantNameAndRestaurantAddress(restaurantName,
                        restaurantAddress, pageable).stream()
                .map(recruitment -> new RecruitmentDto.ResponseList(
                        checkAppointmentTime(recruitment)))
                .collect(Collectors.toList());

        return new PageImpl<>(collect);
    }


    @Transactional
    public Page<RecruitmentDto.ResponseList> findAllByRestaurantAddress(String restaurantAddress, Pageable pageable) {
        List<RecruitmentDto.ResponseList> collect = recruitmentRepository
                .findAllByRestaurantAddress(restaurantAddress, pageable).stream()
                .map(recruitment -> new RecruitmentDto.ResponseList(
                        checkAppointmentTime(recruitment)))
                .collect(Collectors.toList());
        return new PageImpl<>(collect);
    }


    @Transactional
    public Page<RecruitmentDto.ResponseList> findAllAvailableRecruitments(Pageable pageable) {
        Member currentMember = memberService.getCurrentMember();
        Page<Recruitment> all = recruitmentRepository.findAll(pageable);

        List<RecruitmentDto.ResponseList> map = all.filter(recruitment ->
                        !recruitment.hasMember(currentMember) &&
                                !currentMember.equals(recruitment.getAuthor()))
                .map(recruitment -> new RecruitmentDto.ResponseList(
                        checkAppointmentTime(recruitment))).toList();
        return new PageImpl<>(map);
    }

    @Transactional
    public Set<RecruitmentDto.Address> findAllLocations() {
        Map<RecruitmentDto.Address, Integer> addressMap = new HashMap();

        Iterator<Recruitment> iterator =
                recruitmentRepository.findAll().stream()
                        .map(this::checkAppointmentTime)
                        .filter(recruitment -> recruitment.isActive())
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
        List<RecruitmentDto.ResponseList> collect = recruitmentRepository.findAll(pageable).stream()
                .filter(recruitment ->
                        recruitment.hasMember(author) ||
                                author.equals(recruitment.getAuthor())
                ).map(recruitment -> new RecruitmentDto.ResponseList(
                        checkAppointmentTime(recruitment)))
                .collect(Collectors.toList());
        return new PageImpl<>(collect);
    }

    @Transactional
    public Page<RecruitmentDto.ResponseList> findMyRecruitments(Pageable pageable) {
        Member author = memberService.getCurrentMember();
        List<RecruitmentDto.ResponseList> collect = recruitmentRepository.findAllByAuthor(author, pageable).stream()
                .map(recruitment -> new RecruitmentDto.ResponseList(
                        checkAppointmentTime(recruitment)))
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
        Member author = recruitment.getAuthor();
        if (!author.isValid())
            throw new RecruitmentNotActiveException(recruitmentId);

        if (currentMember.equals(author)) {
            throw new AlreadyJoined(currentMember.getNickname());
        }

        if (!checkSexRestriction(recruitment, currentMember))
            throw new RestrictionFailedException(currentMember.getEmail());

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
