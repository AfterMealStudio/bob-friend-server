package com.example.bob_friend.service;

import com.example.bob_friend.model.dto.RecruitmentRequestDto;
import com.example.bob_friend.model.dto.RecruitmentResponseDto;
import com.example.bob_friend.model.entity.Recruitment;
import com.example.bob_friend.model.exception.RecruitmentNotFoundException;
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
        Recruitment byId = recruitmentRepository.findById(recruitmentId).orElseThrow(() -> {
            throw new RecruitmentNotFoundException();
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
        Recruitment savedRecruitment = recruitmentRepository.save(recruitment);
        return new RecruitmentResponseDto(savedRecruitment);
    }

    @Override
    public void delete(Long recruitmentId) {
        recruitmentRepository.deleteById(recruitmentId);
    }

    @Override
    public RecruitmentResponseDto update(Long recruitmentId, RecruitmentRequestDto update) {
        Recruitment recruitment = Recruitment.builder()
                .id(recruitmentId)
                .title(update.getTitle())
                .content(update.getContent())
                .build();

        Recruitment savedRecruitment = recruitmentRepository.save(recruitment);
        return new RecruitmentResponseDto(savedRecruitment);
    }
}
