package com.example.bob_friend.service;

import com.example.bob_friend.model.dto.RecruitmentRequestDto;
import com.example.bob_friend.model.dto.RecruitmentResponseDto;
import com.example.bob_friend.model.entity.Member;

import java.util.List;

public interface RecruitmentService {
    RecruitmentResponseDto findById(Long recruitmentId);

    List<RecruitmentResponseDto> findAll();

    List<RecruitmentResponseDto> findAllByRestaurantName(String restaurantName);

    RecruitmentResponseDto add(RecruitmentRequestDto recruitmentRequestDto);

    void delete(Long recruitmentId);

    RecruitmentResponseDto update(Long recruitmentId, RecruitmentRequestDto update);

    RecruitmentResponseDto join(Long recruitmentId, Member member);

    void unJoin(Long recruitmentId, Member member);
}
