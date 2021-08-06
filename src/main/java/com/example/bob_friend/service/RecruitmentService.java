package com.example.bob_friend.service;

import com.example.bob_friend.model.dto.RecruitmentRequestDto;
import com.example.bob_friend.model.dto.RecruitmentResponseDto;

import java.util.List;

public interface RecruitmentService {
    RecruitmentResponseDto findById(Long recruitmentId);

    List<RecruitmentResponseDto> findAll();

    RecruitmentResponseDto add(RecruitmentRequestDto recruitmentRequestDto);

    void delete(Long recruitmentId);

    RecruitmentResponseDto update(Long recruitmentId, RecruitmentRequestDto update);
}
