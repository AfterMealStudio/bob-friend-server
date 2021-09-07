package com.example.bob_friend.model.dto;

import com.example.bob_friend.model.entity.Comment;
import com.example.bob_friend.model.entity.Reply;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
public class CommentResponseDto {
    private Long id;
    private String author;
    private String content;
    private Set<Reply> replies;
    private LocalDateTime createdAt;

    public CommentResponseDto(Comment comment) {
        this.id = comment.getId();
        this.author = comment.getAuthor().getNickname();
        this.content = comment.getContent();
        this.replies = comment.getReplies();
        this.createdAt = comment.getCreatedAt();
    }
}
