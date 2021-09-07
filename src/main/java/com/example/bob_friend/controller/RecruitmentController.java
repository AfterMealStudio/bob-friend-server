package com.example.bob_friend.controller;

import com.example.bob_friend.model.dto.CommentRequestDto;
import com.example.bob_friend.model.dto.CommentResponseDto;
import com.example.bob_friend.model.dto.RecruitmentRequestDto;
import com.example.bob_friend.model.dto.RecruitmentResponseDto;
import com.example.bob_friend.model.entity.Comment;
import com.example.bob_friend.model.exception.NotAMemberOfRecruitentException;
import com.example.bob_friend.model.exception.RecruitmentAlreadyJoined;
import com.example.bob_friend.model.exception.RecruitmentNotFoundException;
import com.example.bob_friend.service.RecruitmentCommentService;
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
    private final RecruitmentCommentService commentService;


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

    @GetMapping("/my")
    public ResponseEntity getMyRecruitment() {
        List<RecruitmentResponseDto> myRecruitments =
                recruitmentService.findMyRecruitments();
        return ResponseEntity.ok(myRecruitments);
    }

    @GetMapping("/my/joined")
    public ResponseEntity getMyJoinedRecruitment() {
        List<RecruitmentResponseDto> allJoinedRecruitments =
                recruitmentService.findAllJoinedRecruitments();
        return ResponseEntity.ok(allJoinedRecruitments);
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

    @GetMapping("/{recruitmentId}/comments")
    public ResponseEntity getAllComments(@PathVariable Long recruitmentId) {
        List<CommentResponseDto> allCommentByRecruitmentId = commentService
                .getAllCommentByRecruitmentId(recruitmentId);
        return ResponseEntity.ok(allCommentByRecruitmentId);
    }

    @PostMapping("/{recruitmentId}/comments")
    public ResponseEntity createComment(@PathVariable Long recruitmentId,
                                        @RequestBody CommentRequestDto commentRequestDto) {
        CommentResponseDto comment =
                commentService.createCommentToRecruitment(
                        commentRequestDto, recruitmentId);
        return ResponseEntity.ok(comment);
    }


}
