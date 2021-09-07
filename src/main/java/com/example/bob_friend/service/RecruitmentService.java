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
public class RecruitmentService {
    private final RecruitmentRepository recruitmentRepository;
    private final MemberService memberService;

    @Transactional
    public RecruitmentResponseDto findById(Long recruitmentId) {
        Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> {
                    throw new RecruitmentNotFoundException(recruitmentId);
                });
        return new RecruitmentResponseDto(recruitment);
    }

    public List<RecruitmentResponseDto> findAll() {
        return recruitmentRepository.findAll().stream()
                .map(RecruitmentResponseDto::new)
                .collect(Collectors.toList());
    }

    public RecruitmentResponseDto add(RecruitmentRequestDto recruitmentRequestDto) {
        Member currentMember = memberService.getCurrentMember();
        Recruitment recruitment = recruitmentRequestDto.convertToDomain();
        recruitment.setAuthor(currentMember);
        Recruitment savedRecruitment = recruitmentRepository.save(recruitment);
        return new RecruitmentResponseDto(savedRecruitment);
    }

    public void delete(Long recruitmentId) {
        recruitmentRepository.deleteById(recruitmentId);
    }

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

    public List<RecruitmentResponseDto> findAllByRestaurantName(
            String restaurantName) {
        return recruitmentRepository.findAllByRestaurantName(restaurantName).stream()
                .map(RecruitmentResponseDto::new)
                .collect(Collectors.toList());
    }

    public List<RecruitmentResponseDto> findAllJoinedRecruitments() {
        Member author = memberService.getCurrentMember();
        return recruitmentRepository.findAll().stream()
                .filter(recruitment ->
                        recruitment.getMembers().contains(author) ||
                                recruitment.getAuthor().equals(author)
                ).map(RecruitmentResponseDto::new).collect(Collectors.toList());
    }

    public List<RecruitmentResponseDto> findMyRecruitments() {
        Member author = memberService.getCurrentMember();
        return recruitmentRepository.findAllByAuthor(author).stream()
                .map(RecruitmentResponseDto::new)
                .collect(Collectors.toList());
    }

    public RecruitmentResponseDto joinOrUnjoin(Long recruitmentId) {
        Member currentMember = memberService.getCurrentMember();

        Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> {
                    throw new RecruitmentNotFoundException(recruitmentId);
                });
        validateRecruitment(recruitment);

        if (isAuthor(recruitment)) {
            throw new RecruitmentAlreadyJoined(currentMember.getUsername());
        }

        Recruitment recruitment1 = null;

        if (isAlreadyJoined(recruitment, currentMember)) {
            recruitment1 = removeMemberFromRecruitment(recruitment, currentMember);
        } else {
            recruitment1 = addMemberToRecruitment(recruitment, currentMember);
        }

        return new RecruitmentResponseDto(recruitment1);
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
