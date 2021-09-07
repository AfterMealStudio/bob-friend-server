package com.example.bob_friend.service;

import com.example.bob_friend.model.dto.RecruitmentRequestDto;
import com.example.bob_friend.model.dto.RecruitmentResponseDto;
import com.example.bob_friend.model.entity.Member;
import com.example.bob_friend.model.entity.Recruitment;
import com.example.bob_friend.model.exception.*;
import com.example.bob_friend.repository.RecruitmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RecruitmentServiceImpl implements RecruitmentService {
    private final RecruitmentRepository recruitmentRepository;
    private final MemberService memberService;

    @Transactional
    @Override
    public RecruitmentResponseDto findById(Long recruitmentId) {
        Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> {
                    throw new RecruitmentNotFoundException(recruitmentId);
                });
        return new RecruitmentResponseDto(recruitment);
    }

    @Override
    public List<RecruitmentResponseDto> findAll() {
        return recruitmentRepository.findAll().stream()
                .map(RecruitmentResponseDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public RecruitmentResponseDto add(RecruitmentRequestDto recruitmentRequestDto) {
        Member currentMember = memberService.getCurrentMember();
        Recruitment recruitment = recruitmentRequestDto.convertToDomain();
        recruitment.setAuthor(currentMember);
        Recruitment savedRecruitment = recruitmentRepository.save(recruitment);
        return new RecruitmentResponseDto(savedRecruitment);
    }

    @Override
    public void delete(Long recruitmentId) {
        recruitmentRepository.deleteById(recruitmentId);
    }

    @Override
    public RecruitmentResponseDto update(Long recruitmentId,
                                         RecruitmentRequestDto update) {
        Recruitment recruitment = Recruitment.builder()
                .id(recruitmentId)
                .title(update.getTitle())
                .content(update.getContent())
                .build();

        Recruitment savedRecruitment = recruitmentRepository.save(recruitment);
        return new RecruitmentResponseDto(savedRecruitment);
    }

    @Override
    public List<RecruitmentResponseDto> findAllByRestaurantName(
            String restaurantName) {
        return recruitmentRepository.findAllByRestaurantName(restaurantName).stream()
                .map(RecruitmentResponseDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<RecruitmentResponseDto> findAllJoinedRecruitments() {
        Member author = memberService.getCurrentMember();
        return recruitmentRepository.findAll().stream()
                .filter(recruitment ->
                        recruitment.getMembers().contains(author) ||
                                recruitment.getAuthor().equals(author)
                ).map(RecruitmentResponseDto::new).collect(Collectors.toList());
    }

    @Override
    public List<RecruitmentResponseDto> findMyRecruitments() {
        Member author = memberService.getCurrentMember();
        return recruitmentRepository.findAllByAuthor(author).stream()
                .map(RecruitmentResponseDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public RecruitmentResponseDto join(Long recruitmentId)
            throws RecruitmentAlreadyJoined {
        Member currentMember = memberService.getCurrentMember();

        Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> {
                    throw new RecruitmentNotFoundException(recruitmentId);
                });
        validateRecruitment(recruitment);

        if (isAlreadyJoined(recruitment, currentMember)) {
            throw new RecruitmentAlreadyJoined(currentMember.getUsername());
        }
        if (isAuthor(recruitment)) {
            throw new RecruitmentAlreadyJoined(currentMember.getUsername());
        }
        return new RecruitmentResponseDto(
                addMemberToRecruitment(recruitment, currentMember));
    }

    @Override
    public RecruitmentResponseDto unJoin(Long recruitmentId)
            throws NotAMemberOfRecruitentException {
        Member currentMember = memberService.getCurrentMember();

        Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> {
                    throw new RecruitmentNotFoundException(recruitmentId);
                });
        validateRecruitment(recruitment);

        if (!isAlreadyJoined(recruitment, currentMember)) {
            throw new NotAMemberOfRecruitentException(currentMember.getUsername());
        }

        if (isAuthor(recruitment)) {
            throw new NotAMemberOfRecruitentException(currentMember.getUsername());
        }

        return new RecruitmentResponseDto(
                removeMemberFromRecruitment(recruitment, currentMember));

    }

    private Recruitment addMemberToRecruitment(
            Recruitment recruitment,
            Member member)
            throws RecruitmentIsFullException, RecruitmentNotActiveException {
        validateRecruitment(recruitment);
        if (!member.isActive()) {
            throw new MemberNotActivatedException(member.getUsername());
        }
        recruitment.getMembers().add(member);
        increaseCurrentNumberOfPeople(recruitment);

        return recruitmentRepository.save(recruitment);
    }

    private Recruitment removeMemberFromRecruitment(Recruitment recruitment,
                                                   Member member) {
        recruitment.getMembers().remove(member);
        decreaseCurrentNumberOfPeople(recruitment);
        return recruitmentRepository.save(recruitment);
    }

    private void increaseCurrentNumberOfPeople(Recruitment recruitment) {
        validateRecruitment(recruitment);
        int currentNumberOfPeople = recruitment.getCurrentNumberOfPeople() + 1;
        recruitment.setCurrentNumberOfPeople(currentNumberOfPeople);
        if (currentNumberOfPeople >= recruitment.getTotalNumberOfPeople()) {
            recruitment.setFull(true);
        }
    }

    private void decreaseCurrentNumberOfPeople(Recruitment recruitment) {
        recruitment.setCurrentNumberOfPeople(
                recruitment.getCurrentNumberOfPeople() - 1);
    }

    private void validateRecruitment(Recruitment recruitment) {
        if (recruitment.isFull())
            throw new RecruitmentIsFullException();
        if (!recruitment.isActive())
            throw new RecruitmentNotActiveException(recruitment.getId());
    }

    private boolean isAlreadyJoined(Recruitment recruitment, Member member) {
        return recruitment.getMembers().contains(member);
    }

    private boolean isAuthor(Recruitment recruitment) {
        Member currentMember = memberService.getCurrentMember();
        return recruitment.getAuthor().equals(currentMember);
    }
}
