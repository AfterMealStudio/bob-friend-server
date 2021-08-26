package com.example.bob_friend.service;

import com.example.bob_friend.model.dto.RecruitmentRequestDto;
import com.example.bob_friend.model.dto.RecruitmentResponseDto;
import com.example.bob_friend.model.entity.Member;
import com.example.bob_friend.model.entity.Recruitment;
import com.example.bob_friend.model.exception.*;
import com.example.bob_friend.repository.RecruitmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RecruitmentServiceImpl implements RecruitmentService {
    private final RecruitmentRepository recruitmentRepository;

    @Override
    public RecruitmentResponseDto findById(Long recruitmentId) {
        Recruitment byId = recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> {
                    throw new RecruitmentNotFoundException(recruitmentId);
                });
        return new RecruitmentResponseDto(byId);
    }

    @Override
    public List<RecruitmentResponseDto> findAll() {
        return recruitmentRepository.findAll().stream()
                .map(RecruitmentResponseDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public RecruitmentResponseDto add(RecruitmentRequestDto recruitmentRequestDto) {
        Recruitment recruitment = recruitmentRequestDto.convertToDomain();
//        recruitment.getMembers().add(recruitment.getAuthor());
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
    public List<RecruitmentResponseDto> findAllByRestaurantName(String restaurantName) {
        return recruitmentRepository.findAllByRestaurantName(restaurantName).stream()
                .map(RecruitmentResponseDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public RecruitmentResponseDto join(Long recruitmentId, Member member) {
        Recruitment recruitment = recruitmentRepository.findById(recruitmentId).orElseThrow(() -> {
            throw new RecruitmentNotFoundException(recruitmentId);
        });
        if (isAlreadyJoin(recruitment,member)) throw new MemberDuplicatedException(member.getUsername());
        return new RecruitmentResponseDto(addMemberToRecruitment(recruitment, member));
    }

    private Recruitment addMemberToRecruitment(Recruitment recruitment, Member member) {
        if (isValidRecruitment(recruitment)) {
            if (!member.isActivated()) {
                throw new MemberNotActivatedException(member.getUsername());
            }
            recruitment.getMembers().add(member);
            increaseCurrentNumberOfPeople(recruitment);
        }
        return recruitmentRepository.save(recruitment);
    }

    private void increaseCurrentNumberOfPeople(Recruitment recruitment) {
        if (isValidRecruitment(recruitment)) {
            recruitment.setCurrentNumberOfPeople(recruitment.getCurrentNumberOfPeople() + 1);
        }
    }
    private void decreaseCurrentNumberOfPeople(Recruitment recruitment) {
        recruitment.setCurrentNumberOfPeople(recruitment.getCurrentNumberOfPeople()-1);
    }

    private boolean isValidRecruitment(Recruitment recruitment) {
        if (recruitment.isFull()) throw new RecruitmentIsFullException(recruitment.getId());
        if (!recruitment.isActive()) throw new RecruitmentNotActiveException(recruitment.getId());
        return true;
    }

    private boolean isAlreadyJoin(Recruitment recruitment, Member member) {
        return recruitment.getMembers().contains(member);
    }

    @Override
    public void unJoin(Long recruitmentId, Member member) {
        Recruitment recruitment = recruitmentRepository.findById(recruitmentId).orElseThrow(() -> {
            throw new RecruitmentNotFoundException(recruitmentId);
        });
        if (isAlreadyJoin(recruitment,member)) {
            recruitment.getMembers().remove(member);
            decreaseCurrentNumberOfPeople(recruitment);
            recruitmentRepository.save(recruitment);
        }
    }
}
