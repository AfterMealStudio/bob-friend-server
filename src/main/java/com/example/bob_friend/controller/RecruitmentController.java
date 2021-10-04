package com.example.bob_friend.controller;

import com.example.bob_friend.model.dto.CommentDto;
import com.example.bob_friend.model.dto.RecruitmentDto;
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
    public ResponseEntity getAllRecruitment(
            @RequestParam(value = "restaurantName", required = false) String restaurantName,
            @RequestParam(value = "restaurantAddress", required = false) String restaurantAddress) {
        List<RecruitmentDto.Response> responseDtoList = null;
        if (restaurantName == null) {
            if (restaurantAddress == null) {
                responseDtoList = recruitmentService.findAllAvailableRecruitments();
            } else {
                responseDtoList = recruitmentService
                        .findAllByRestaurantAddress(restaurantAddress);
            }
        } else {
            responseDtoList = recruitmentService
                    .findAllByRestaurantNameAndRestaurantAddress(restaurantName, restaurantAddress);
        }
        return ResponseEntity.ok(responseDtoList);
    }


    @GetMapping("/locations")
    public ResponseEntity getAllLocations() {
        return ResponseEntity.ok(recruitmentService.findAllAvailableLocations());
    }


    @GetMapping("/{recruitmentId}")
    public ResponseEntity getRecruitment(@PathVariable Long recruitmentId)
            throws RecruitmentNotFoundException {
        RecruitmentDto.Response recruitmentResponseDto = recruitmentService.findById(recruitmentId);
        return ResponseEntity.ok(recruitmentResponseDto);
    }

    @GetMapping("/my")
    public ResponseEntity getMyRecruitment() {
        List<RecruitmentDto.Response> myRecruitments =
                recruitmentService.findMyRecruitments();
        return ResponseEntity.ok(myRecruitments);
    }

    @GetMapping("/my/joined")
    public ResponseEntity getMyJoinedRecruitment() {
        List<RecruitmentDto.Response> allJoinedRecruitments =
                recruitmentService.findAllJoinedRecruitments();
        return ResponseEntity.ok(allJoinedRecruitments);
    }

    @PostMapping()
    public ResponseEntity createRecruitment(
            @RequestBody RecruitmentDto.Request recruitmentRequestDto) {
        RecruitmentDto.Response createdRecruitment = recruitmentService.createRecruitment(recruitmentRequestDto);
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
    public ResponseEntity deleteRecruitment(
            @PathVariable Long recruitmentId) {
        recruitmentService.delete(recruitmentId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{recruitmentId}")
    public ResponseEntity joinRecruitment(@PathVariable Long recruitmentId)
            throws RecruitmentAlreadyJoined {
        RecruitmentDto.Response join = recruitmentService.joinOrUnjoin(recruitmentId);
        return ResponseEntity.ok(join);
    }

    @GetMapping("/{recruitmentId}/comments")
    public ResponseEntity getAllComments(@PathVariable Long recruitmentId) {
        List<CommentDto.Response> allCommentByRecruitmentId = commentService
                .getAllCommentByRecruitmentId(recruitmentId);
        return ResponseEntity.ok(allCommentByRecruitmentId);
    }

    @PostMapping("/{recruitmentId}/comments")
    public ResponseEntity createComment(@PathVariable Long recruitmentId,
                                        @RequestBody CommentDto.Request commentRequestDto) {
        CommentDto.Response comment =
                commentService.createCommentToRecruitment(
                        commentRequestDto, recruitmentId);
        return ResponseEntity.ok(comment);
    }


}
