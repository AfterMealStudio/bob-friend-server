package com.example.bob_friend.controller;

import com.example.bob_friend.model.dto.RecruitmentDto;
import com.example.bob_friend.model.exception.RecruitmentIsFullException;
import com.example.bob_friend.model.exception.RecruitmentNotActiveException;
import com.example.bob_friend.model.exception.RecruitmentNotFoundException;
import com.example.bob_friend.service.CommentService;
import com.example.bob_friend.service.RecruitmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/recruitments")
public class RecruitmentController {
    private final RecruitmentService recruitmentService;
    private final CommentService commentService;


    @GetMapping()
    public ResponseEntity getAllRecruitment(
            @RequestParam(value = "restaurantName", required = false) String restaurantName,
            @RequestParam(value = "restaurantAddress", required = false) String restaurantAddress,
            Pageable pageable) {
        Page<RecruitmentDto.Response> responseDtoList = null;
        if (restaurantName == null) {
            if (restaurantAddress == null) {
                responseDtoList = recruitmentService.findAll(pageable);
            } else {
                responseDtoList = recruitmentService
                        .findAllByRestaurantAddress(restaurantAddress, pageable);
            }
        } else {
            responseDtoList = recruitmentService
                    .findAllByRestaurantNameAndRestaurantAddress(restaurantName, restaurantAddress, pageable);
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
    public ResponseEntity getMyRecruitment(Pageable pageable) {
        Page<RecruitmentDto.Response> myRecruitments =
                recruitmentService.findMyRecruitments(pageable);
        return ResponseEntity.ok(myRecruitments);
    }

    @GetMapping("/my/joined")
    public ResponseEntity getMyJoinedRecruitment(Pageable pageable) {
        Page<RecruitmentDto.Response> allJoinedRecruitments =
                recruitmentService.findAllJoinedRecruitments(pageable);
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
            throws RecruitmentIsFullException, RecruitmentNotActiveException {
        RecruitmentDto.Response join = recruitmentService.joinOrUnjoin(recruitmentId);
        return ResponseEntity.ok(join);
    }


    @GetMapping("/search")
    public ResponseEntity searchRecruitment(
            @RequestParam(defaultValue = "title") Category category,
            @RequestParam String keyword,
            Pageable pageable) {

        Page<RecruitmentDto.Response> searchResult = null;

        switch (category) {
            case time:
                break;
            case place:
                searchResult = recruitmentService.searchRestaurant(keyword, pageable);
                break;
            case title:
                searchResult = recruitmentService.searchTitle(keyword, pageable);
                break;
            case content:
                searchResult = recruitmentService.searchContent(keyword, pageable);
                break;
        }

        return ResponseEntity.ok(searchResult);
    }


    private enum Category {
        title, content, place, time
    }
}
