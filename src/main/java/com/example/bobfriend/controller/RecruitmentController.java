package com.example.bobfriend.controller;

import com.example.bobfriend.model.dto.Condition;
import com.example.bobfriend.model.dto.recruitment.Create;
import com.example.bobfriend.model.dto.recruitment.DetailResponse;
import com.example.bobfriend.model.dto.recruitment.SimpleResponse;
import com.example.bobfriend.model.exception.RecruitmentIsFullException;
import com.example.bobfriend.model.exception.RecruitmentNotActiveException;
import com.example.bobfriend.model.exception.RecruitmentNotFoundException;
import com.example.bobfriend.service.RecruitmentFindService;
import com.example.bobfriend.service.RecruitmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/recruitments")
public class RecruitmentController {
    private final RecruitmentService recruitmentService;
    private final RecruitmentFindService recruitmentFindService;

    @GetMapping()
    public ResponseEntity getAll(
            @RequestParam(name = "type", defaultValue = "all") Condition.SearchType type,
            @RequestParam(required = false) String address,
            @PageableDefault(sort = {"createdAt"}, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<SimpleResponse> responseDtoList = null;

        switch (type) {
            case owned: // 자기가 작성한
                responseDtoList = recruitmentFindService
                        .findMyRecruitments(pageable);
                break;
            case joined:// 자기가 참여한
                responseDtoList = recruitmentFindService
                        .findAllJoined(pageable);
                break;
            case available:// 참여 가능한
                responseDtoList = recruitmentFindService
                        .findAllAvailable(pageable);
                break;
            case specific: // 특정 위치에 있는
                responseDtoList = recruitmentService
                        .findAllByRestaurantAddress(address, pageable);
                break;
            case all: // 전체
                responseDtoList = recruitmentFindService
                        .findAll(pageable);
                break;
        }
        return ResponseEntity.ok(responseDtoList);
    }


    @GetMapping("/locations")
    public ResponseEntity getAllLocations(
            @RequestParam(name = "zoom") Integer zoom,
            @RequestParam(name = "latitude") Double latitude,
            @RequestParam(name = "longitude") Double longitude) {
        return ResponseEntity.ok(recruitmentFindService.findAllLocations(latitude, longitude, zoom));
    }


    @GetMapping("/{recruitmentId}")
    public ResponseEntity getRecruitment(@PathVariable Long recruitmentId)
            throws RecruitmentNotFoundException {
        DetailResponse recruitmentDetailResponseDto = recruitmentFindService.findById(recruitmentId);
        return ResponseEntity.ok(recruitmentDetailResponseDto);
    }

    @PostMapping
    public ResponseEntity create(
            @Valid @RequestBody Create recruitmentRequestDto) {
        DetailResponse createdRecruitment = recruitmentService.create(recruitmentRequestDto);
        return ResponseEntity.ok(createdRecruitment);
    }

    @PatchMapping("/{recruitmentId}/close")
    public ResponseEntity close(@PathVariable Long recruitmentId) {
        recruitmentService.close(recruitmentId);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/{recruitmentId}")
    public ResponseEntity delete(
            @PathVariable Long recruitmentId) {
        recruitmentService.delete(recruitmentId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{recruitmentId}")
    public ResponseEntity join(@PathVariable Long recruitmentId)
            throws RecruitmentIsFullException, RecruitmentNotActiveException {
        DetailResponse join = recruitmentService.joinOrUnjoin(recruitmentId);
        return ResponseEntity.ok(join);
    }

    @PatchMapping("/{recruitmentId}/report")
    public ResponseEntity report(@PathVariable Long recruitmentId) {
        recruitmentService.reportById(recruitmentId);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/search")
    public ResponseEntity search(
            @RequestParam(defaultValue = "all", name = "category") Condition.SearchCategory category,
            Condition.Search searchCondition,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<DetailResponse> searchResult = null;
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

        return ResponseEntity.ok(searchResult);
    }

}
