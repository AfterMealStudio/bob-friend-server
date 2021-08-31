package com.example.bob_friend.service;

import com.example.bob_friend.model.dto.RecruitmentRequestDto;
import com.example.bob_friend.model.dto.RecruitmentResponseDto;
import com.example.bob_friend.model.entity.Member;
import com.example.bob_friend.model.exception.RecruitmentAlreadyJoined;
import com.example.bob_friend.model.exception.RecruitmentIsFullException;
import com.example.bob_friend.model.exception.RecruitmentNotActiveException;

import java.util.List;

public interface RecruitmentService {
    RecruitmentResponseDto findById(Long recruitmentId);

    List<RecruitmentResponseDto> findAll();

    List<RecruitmentResponseDto> findAllByRestaurantName(String restaurantName);

    List<RecruitmentResponseDto> findMyRecruitments();
    List<RecruitmentResponseDto> findAllJoinedRecruitments();
    RecruitmentResponseDto add(RecruitmentRequestDto recruitmentRequestDto);

    void delete(Long recruitmentId);

    RecruitmentResponseDto update(Long recruitmentId, RecruitmentRequestDto update);

    RecruitmentResponseDto join(Long recruitmentId) throws RecruitmentIsFullException, RecruitmentNotActiveException, RecruitmentAlreadyJoined;

    RecruitmentResponseDto unJoin(Long recruitmentId);

}
