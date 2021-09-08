package com.example.bob_friend.service;

import com.example.bob_friend.model.dto.RecruitmentRequestDto;
import com.example.bob_friend.model.dto.RecruitmentResponseDto;
import com.example.bob_friend.model.entity.Member;
import com.example.bob_friend.model.entity.Recruitment;
import com.example.bob_friend.model.exception.RecruitmentIsFullException;
import com.example.bob_friend.model.exception.RecruitmentNotActiveException;
import com.example.bob_friend.model.exception.RecruitmentNotFoundException;
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

    public RecruitmentResponseDto createRecruitment(RecruitmentRequestDto recruitmentRequestDto) {
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

    public List<RecruitmentResponseDto> findAllAvailableRecruitments() {
        Member currentMember = memberService.getCurrentMember();
        return recruitmentRepository.findAll().stream()
                .filter(recruitment ->
                        !recruitment.hasMember(currentMember) &&
                                !recruitment.getAuthor().equals(currentMember)
                ).map(RecruitmentResponseDto::new)
                .collect(Collectors.toList());
    }

    public List<RecruitmentResponseDto> findAllJoinedRecruitments() {
        Member author = memberService.getCurrentMember();
        return recruitmentRepository.findAll().stream()
                .filter(recruitment ->
                        recruitment.hasMember(author) ||
                                recruitment.getAuthor().equals(author)
                ).map(RecruitmentResponseDto::new)
                .collect(Collectors.toList());
    }

    public List<RecruitmentResponseDto> findMyRecruitments() {
        Member author = memberService.getCurrentMember();
        return recruitmentRepository.findAllByAuthor(author).stream()
                .map(RecruitmentResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public RecruitmentResponseDto joinOrUnjoin(Long recruitmentId) throws RecruitmentIsFullException, RecruitmentNotActiveException {
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

        return new RecruitmentResponseDto(recruitment);
    }

    private void validateRecruitment(Recruitment recruitment) {
        if (recruitment.isFull())
            throw new RecruitmentIsFullException(recruitment.getId());
        if (!recruitment.isActive())
            throw new RecruitmentNotActiveException(recruitment.getId());
    }

}
