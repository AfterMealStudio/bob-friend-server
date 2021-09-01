package com.example.bob_friend.controller;

import com.example.bob_friend.model.dto.RecruitmentRequestDto;
import com.example.bob_friend.model.dto.RecruitmentResponseDto;
import com.example.bob_friend.model.exception.NotAMemberOfRecruitentException;
import com.example.bob_friend.model.exception.RecruitmentAlreadyJoined;
import com.example.bob_friend.model.exception.RecruitmentIsFullException;
import com.example.bob_friend.model.exception.RecruitmentNotFoundException;
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

    @GetMapping()
    public ResponseEntity getAllRecruitment() {
        List<RecruitmentResponseDto> responseDtoList = recruitmentService.findAll();
        return ResponseEntity.ok(responseDtoList);
    }

    @GetMapping("/{recruitmentId}")
    public ResponseEntity getRecruitment(@PathVariable Long recruitmentId)
            throws RecruitmentNotFoundException {
        RecruitmentResponseDto recruitmentResponseDto = recruitmentService.findById(recruitmentId);
        return ResponseEntity.ok(recruitmentResponseDto);
    }

    @PostMapping()
    public ResponseEntity createRecruitment(
            @RequestBody RecruitmentRequestDto recruitmentRequestDto) {
        RecruitmentResponseDto createdRecruitment = recruitmentService.add(recruitmentRequestDto);
        return ResponseEntity.ok(createdRecruitment);
    }

//    @PutMapping("/{recruitmentId}")
//    public ResponseEntity updateRecruitment(
//            @PathVariable Long recruitmentId,
//            RecruitmentRequestDto incomingChanges) {
//        RecruitmentResponseDto update = recruitmentService.update(recruitmentId, incomingChanges);
//        return ResponseEntity.ok(update);
//    }

    @DeleteMapping("/{recruitmentId}")
    public ResponseEntity deleteRecruitment(@PathVariable Long recruitmentId) {
        recruitmentService.delete(recruitmentId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{recruitmentId}")
    public ResponseEntity joinRecruitment(@PathVariable Long recruitmentId)
            throws RecruitmentAlreadyJoined {
        RecruitmentResponseDto join = recruitmentService.join(recruitmentId);
        return ResponseEntity.ok(join);
    }

    @PatchMapping("/{recruitmentId}/unjoin")
    public ResponseEntity unJoinRecruitment(@PathVariable Long recruitmentId)
            throws NotAMemberOfRecruitentException {
        RecruitmentResponseDto unjoin = recruitmentService.unJoin(recruitmentId);
        return ResponseEntity.ok(unjoin);
    }

    @ExceptionHandler(value = RecruitmentNotFoundException.class)
    public ResponseEntity handleRecruitmentNotFound(RecruitmentNotFoundException e) {
        return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = RecruitmentIsFullException.class)
    public ResponseEntity handleRecruitmentIsFull(RecruitmentIsFullException e) {
        return new ResponseEntity(e.getMessage(), HttpStatus.INSUFFICIENT_STORAGE);
    }

    @ExceptionHandler(value = NotAMemberOfRecruitentException.class)
    public ResponseEntity handleNotMember(NotAMemberOfRecruitentException e) {
        return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = RecruitmentAlreadyJoined.class)
    public ResponseEntity handleAlreadyJoinedException(RecruitmentAlreadyJoined e) {
        return new ResponseEntity(e.getMessage(),HttpStatus.CONFLICT);
    }

}
