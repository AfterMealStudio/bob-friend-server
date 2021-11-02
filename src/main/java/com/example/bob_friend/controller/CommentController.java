package com.example.bob_friend.controller;

import com.example.bob_friend.model.dto.CommentDto;
import com.example.bob_friend.model.dto.ReplyDto;
import com.example.bob_friend.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/recruitments/{recruitmentId}/comments")
public class CommentController {
    private final CommentService commentService;

    @GetMapping()
    public ResponseEntity getAllComments(@PathVariable Long recruitmentId) {
        List<CommentDto.Response> allCommentByRecruitmentId = commentService
                .getAllCommentByRecruitmentId(recruitmentId);
        return ResponseEntity.ok(allCommentByRecruitmentId);
    }

    @PostMapping()
    public ResponseEntity createComment(
            @PathVariable Long recruitmentId,
            @RequestBody CommentDto.Request commentRequestDto) {
        CommentDto.Response comment =
                commentService.createComment(
                        commentRequestDto, recruitmentId);
        return ResponseEntity.ok(comment);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity deleteComment(
            @PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{commentId}/report")
    public ResponseEntity reportComment(@PathVariable Long commentId) {
        commentService.reportComment(commentId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{commentId}/replies")
    public ResponseEntity createReplyToComment(
            @PathVariable Long commentId,
            @RequestBody ReplyDto.Request replyDto) {
        ReplyDto.Response reply =
                commentService.createReply(commentId, replyDto);

        return ResponseEntity.ok(reply);
    }

    @DeleteMapping("/{commentId}/replies/{replyId}")
    public ResponseEntity deleteReply(
            @PathVariable Long replyId) {
        commentService.deleteReply(replyId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{commentId}/replies/{replyId}/report")
    public ResponseEntity reportReply(@PathVariable Long replyId) {
        commentService.reportReply(replyId);
        return ResponseEntity.ok().build();
    }
}
