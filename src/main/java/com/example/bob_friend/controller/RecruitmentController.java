package com.example.bob_friend.controller;

import com.example.bob_friend.model.dto.RecruitmentDto;
import com.example.bob_friend.model.dto.Condition;
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
            @RequestParam(name = "type", defaultValue = "all") Condition.SearchType type,
            Condition.Search searchCondition,
            @PageableDefault(sort = {"createdAt"}, direction = Sort.Direction.ASC) Pageable pageable) {
        Page<RecruitmentDto.ResponseList> responseDtoList = null;

        switch (type) {
            case owned: // 자기가 작성한
                responseDtoList = recruitmentService
                        .findMyRecruitments(pageable);
                break;
            case joined:// 자기가 참여한
                responseDtoList = recruitmentService
                        .findAllJoinedRecruitments(pageable);
                break;
            case available:// 참여 가능한
                responseDtoList = recruitmentService
                        .findAllAvailableRecruitments(pageable);
                break;
            case all: // 전체
                responseDtoList = recruitmentService
                        .findAllByRestaurant(searchCondition, pageable);
        }
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

    @PostMapping
    public ResponseEntity createRecruitment(
            @RequestBody RecruitmentDto.Request recruitmentRequestDto) {
        RecruitmentDto.Response createdRecruitment = recruitmentService.createRecruitment(recruitmentRequestDto);
        return ResponseEntity.ok(createdRecruitment);
    }

    @PatchMapping("/{recruitmentId}/close")
    public ResponseEntity closeRecruitment(@PathVariable Long recruitmentId) {
        recruitmentService.closeRecruitment(recruitmentId);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/{recruitmentId}")
    public ResponseEntity deleteRecruitment(
            @PathVariable Long recruitmentId) {
        recruitmentService.deleteRecruitment(recruitmentId);
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
            @RequestParam(defaultValue = "all", name = "category") Condition.SearchCategory category,
            Condition.Search searchCondition,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<RecruitmentDto.Response> searchResult = getResponses(category, searchCondition, pageable);

        return ResponseEntity.ok(searchResult);
    }

    private Page<RecruitmentDto.Response> getResponses(Condition.SearchCategory category, Condition.Search searchCondition, Pageable pageable) {
        Page<RecruitmentDto.Response> searchResult = null;
        switch (category) {
            case place:
                searchResult = recruitmentService.searchRestaurant(searchCondition, pageable);
                break;
            case title:
                searchResult = recruitmentService.searchTitle(searchCondition, pageable);
                break;
            case content:
                searchResult = recruitmentService.searchContent(searchCondition, pageable);
                break;
            case all:
                searchResult = recruitmentService.searchByAllCondition(searchCondition, pageable);
                break;
        }
        return searchResult;
    }

}
