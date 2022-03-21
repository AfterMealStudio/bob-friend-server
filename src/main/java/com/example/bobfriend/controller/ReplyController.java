package com.example.bobfriend.controller;

import com.example.bobfriend.model.dto.reply.Create;
import com.example.bobfriend.model.dto.reply.Response;
import com.example.bobfriend.service.ReplyService;
import com.example.bobfriend.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/recruitments/{recruitmentId}/comments/{commentId}/replies")
public class ReplyController {

    private final ReplyService replyService;
    private final ReportService reportService;

    @PostMapping()
    public ResponseEntity create(
            @PathVariable Long commentId,
            @Valid @RequestBody Create replyDto) {
        Response reply =
                replyService.create(commentId, replyDto);

        return ResponseEntity.ok(reply);
    }

    @DeleteMapping("/{replyId}")
    public ResponseEntity delete(
            @PathVariable Long replyId) {
        replyService.delete(replyId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("{replyId}/report")
    public ResponseEntity report(@PathVariable Long replyId) {
        reportService.reportWriting(replyId);
        return ResponseEntity.ok().build();
    }
}
