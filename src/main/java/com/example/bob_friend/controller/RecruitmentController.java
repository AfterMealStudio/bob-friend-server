package com.example.bob_friend.controller;

import com.example.bob_friend.model.dto.MemberResponseDto;
import com.example.bob_friend.model.dto.RecruitmentRequestDto;
import com.example.bob_friend.model.dto.RecruitmentResponseDto;
import com.example.bob_friend.model.exception.RecruitmentIsFullException;
import com.example.bob_friend.model.exception.RecruitmentNotFoundException;
import com.example.bob_friend.service.MemberService;
import com.example.bob_friend.service.RecruitmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/recruitments")
public class RecruitmentController {
    private final RecruitmentService recruitmentService;
    private final MemberService memberService;
    @GetMapping()
    public ResponseEntity getAllRecruitment() {
        List<RecruitmentResponseDto> responseDtoList = recruitmentService.findAll();
        return ResponseEntity.ok(responseDtoList);
    }

    @GetMapping("/{recruitmentId}")
    public ResponseEntity getRecruitment(@PathVariable Long recruitmentId) throws RecruitmentNotFoundException {
        RecruitmentResponseDto recruitmentResponseDto = recruitmentService.findById(recruitmentId);
        return ResponseEntity.ok(recruitmentResponseDto);
    }

    @PostMapping()
    public ResponseEntity createRecruitment(@RequestBody RecruitmentRequestDto recruitmentRequestDto) {
        String currentUsername = memberService.getCurrentUsername();
        MemberResponseDto currentMember = memberService.getMemberWithAuthorities(currentUsername);
        recruitmentRequestDto.setAuthor(currentMember.convertToEntity());
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

    @PatchMapping("/{recruitmentId}")
    public ResponseEntity joinRecruitment(@PathVariable Long recruitmentId) {
        String currentUsername = memberService.getCurrentUsername();
        MemberResponseDto currentMember = memberService.getMemberWithAuthorities(currentUsername);
        RecruitmentResponseDto join = recruitmentService.joinOrUnJoin(recruitmentId, currentMember.convertToEntity());
        return ResponseEntity.ok(join);
    }

    @ExceptionHandler(value = RecruitmentNotFoundException.class)
    public ResponseEntity handleRecruitmentNotFound(RecruitmentNotFoundException e) {
        return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = RecruitmentIsFullException.class)
    public ResponseEntity handleRecruitmentIsFull(RecruitmentIsFullException e) {
        return new ResponseEntity(e.getMessage(), HttpStatus.INSUFFICIENT_STORAGE);
    }

}
