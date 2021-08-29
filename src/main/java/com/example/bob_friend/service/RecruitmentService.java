package com.example.bob_friend.service;

import com.example.bob_friend.model.dto.RecruitmentRequestDto;
import com.example.bob_friend.model.dto.RecruitmentResponseDto;
import com.example.bob_friend.model.exception.RecruitmentIsFullException;
import com.example.bob_friend.model.exception.RecruitmentNotActiveException;

import java.util.List;

public interface RecruitmentService {
    RecruitmentResponseDto findById(Long recruitmentId);

    List<RecruitmentResponseDto> findAll();

    List<RecruitmentResponseDto> findAllByRestaurantName(String restaurantName);

    RecruitmentResponseDto add(RecruitmentRequestDto recruitmentRequestDto);

    void delete(Long recruitmentId);

    RecruitmentResponseDto update(Long recruitmentId, RecruitmentRequestDto update);

    RecruitmentResponseDto joinOrUnJoin(Long recruitmentId) throws RecruitmentIsFullException, RecruitmentNotActiveException;

}
