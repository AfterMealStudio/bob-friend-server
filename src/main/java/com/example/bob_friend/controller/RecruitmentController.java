package com.example.bob_friend.controller;

import com.example.bob_friend.model.dto.RecruitmentDto;
import com.example.bob_friend.model.dto.SearchCondition;
import com.example.bob_friend.model.exception.RecruitmentIsFullException;
import com.example.bob_friend.model.exception.RecruitmentNotActiveException;
import com.example.bob_friend.model.exception.RecruitmentNotFoundException;
import com.example.bob_friend.service.RecruitmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/recruitments")
public class RecruitmentController {
    private final RecruitmentService recruitmentService;


    @GetMapping()
    public ResponseEntity getAllRecruitment(
            SearchCondition searchCondition,
            @PageableDefault(sort = {"createdAt"}, direction = Sort.Direction.ASC) Pageable pageable) {
        Page<RecruitmentDto.ResponseList> responseDtoList = recruitmentService
                .findAllByRestaurant(searchCondition, pageable);
        return ResponseEntity.ok(responseDtoList);
    }


    @GetMapping("/locations")
    public ResponseEntity getAllLocations() {
        return ResponseEntity.ok(recruitmentService.findAllLocations());
    }


    @GetMapping("/{recruitmentId}")
    public ResponseEntity getRecruitment(@PathVariable Long recruitmentId)
            throws RecruitmentNotFoundException {
        RecruitmentDto.Response recruitmentResponseDto = recruitmentService.findById(recruitmentId);
        return ResponseEntity.ok(recruitmentResponseDto);
    }

    @GetMapping("/my")
    public ResponseEntity getMyRecruitment(Pageable pageable) {
        Page<RecruitmentDto.ResponseList> myRecruitments =
                recruitmentService.findMyRecruitments(pageable);
        return ResponseEntity.ok(myRecruitments);
    }

    @GetMapping("/my/joined")
    public ResponseEntity getMyJoinedRecruitment(Pageable pageable) {
        Page<RecruitmentDto.ResponseList> allJoinedRecruitments =
                recruitmentService.findAllJoinedRecruitments(pageable);
        return ResponseEntity.ok(allJoinedRecruitments);
    }

    @PostMapping()
    public ResponseEntity createRecruitment(
            @RequestBody RecruitmentDto.Request recruitmentRequestDto) {
        RecruitmentDto.Response createdRecruitment = recruitmentService.createRecruitment(recruitmentRequestDto);
        return ResponseEntity.ok(createdRecruitment);
    }


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

    @PatchMapping("/{recruitmentId}/report")
    public ResponseEntity report(@PathVariable Long recruitmentId) {
        recruitmentService.reportRecruitment(recruitmentId);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/search")
    public ResponseEntity searchRecruitment(
            @RequestParam(defaultValue = "title", name = "category") Category category,
            SearchCondition searchCondition,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<RecruitmentDto.Response> searchResult = getResponses(category, searchCondition, pageable);

        return ResponseEntity.ok(searchResult);
    }

    private Page<RecruitmentDto.Response> getResponses(Category category, SearchCondition searchCondition, Pageable pageable) {
        Page<RecruitmentDto.Response> searchResult = null;

        switch (category) {
            case time:
                searchResult = recruitmentService.searchAppointmentTime(searchCondition.getStart(), searchCondition.getEnd(), pageable);
                break;
            case place:
                searchResult = recruitmentService.searchRestaurant(searchCondition.getKeyword(), pageable);
                break;
            case title:
                searchResult = recruitmentService.searchTitle(searchCondition.getKeyword(), pageable);
                break;
            case content:
                searchResult = recruitmentService.searchContent(searchCondition.getKeyword(), pageable);
                break;
            case all:
                searchResult = recruitmentService.searchByAllCondition(searchCondition.getKeyword(), pageable);
                break;
        }
        return searchResult;
    }

    private enum Category {
        title, content, place, time, all
    }
}
