package com.example.bob_friend.controller;

import com.example.bob_friend.model.card.domain.Recruitment;
import com.example.bob_friend.model.card.exception.RecruitmentNotFoundException;
import com.example.bob_friend.repository.RecruitmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/recruitments")
public class RecruitmentController {

    private final RecruitmentRepository recruitmentRepository;

    @GetMapping("/{recruitmentId}")
    public ResponseEntity getCard(@PathVariable Long recruitmentId) {
        Recruitment recruitment = recruitmentRepository.findById(recruitmentId).orElseThrow(
                () -> new RecruitmentNotFoundException()
        );
        return ResponseEntity.ok(recruitment);
    }

    @ExceptionHandler(value = RecruitmentNotFoundException.class)
    public ResponseEntity handleCardNotFound(RecruitmentNotFoundException exception) {
        // excetion handling
        return null;
    }
}
