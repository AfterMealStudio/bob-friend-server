package com.example.bob_friend.controller;

import com.example.bob_friend.model.dto.RecruitmentRequestDto;
import com.example.bob_friend.model.dto.RecruitmentResponseDto;
import com.example.bob_friend.model.exception.RecruitmentNotFoundException;
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

    @PutMapping("/{recruitmentId}")
    public ResponseEntity updateRecruitment(@PathVariable Long recruitmentId, RecruitmentRequestDto incomingChanges) {
        RecruitmentResponseDto update = recruitmentService.update(recruitmentId, incomingChanges);
        return ResponseEntity.ok(update);
    }

    @DeleteMapping("/{recruitmentId}")
    public ResponseEntity deleteRecruitment(@PathVariable Long recruitmentId) {
        recruitmentService.delete(recruitmentId);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(value = RecruitmentNotFoundException.class)
    public ResponseEntity handleCardNotFound(RecruitmentNotFoundException exception) {
        // excetion handling
        return null;
    }
}
