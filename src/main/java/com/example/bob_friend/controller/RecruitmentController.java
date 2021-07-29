package com.example.bob_friend.controller;

import com.example.bob_friend.model.card.domain.Recruitment;
import com.example.bob_friend.model.card.dto.RecruitmentRequestDto;
import com.example.bob_friend.model.card.dto.RecruitmentResponseDto;
import com.example.bob_friend.model.card.exception.RecruitmentNotFoundException;
import com.example.bob_friend.service.RecruitmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/recruitments")
public class RecruitmentController {
    private final RecruitmentService recruitmentService;

    @GetMapping()
    public ResponseEntity getAllRecruitment() {
        List<RecruitmentResponseDto> responseDtoList = recruitmentService.findAll();
        return ResponseEntity.ok(responseDtoList);
    }

    @GetMapping("/{recruitmentId}")
    public ResponseEntity getRecruitment(@PathVariable Long recruitmentId) {
        RecruitmentResponseDto recruitmentResponseDto = recruitmentService.findById(recruitmentId);
        return ResponseEntity.ok(recruitmentResponseDto);
    }

    @PostMapping()
    public ResponseEntity createRecruitment(RecruitmentRequestDto recruitmentRequestDto) {
        RecruitmentResponseDto createdRecruitment = recruitmentService.add(recruitmentRequestDto);
        return ResponseEntity.ok(createdRecruitment);
    }



    @ExceptionHandler(value = RecruitmentNotFoundException.class)
    public ResponseEntity handleCardNotFound(RecruitmentNotFoundException exception) {
        // excetion handling
        return null;
    }
}
