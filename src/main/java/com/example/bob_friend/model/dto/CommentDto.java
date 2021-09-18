package com.example.bob_friend.model.dto;

import com.example.bob_friend.model.entity.Comment;
import com.example.bob_friend.model.entity.Reply;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class CommentDto {
    @Setter
    @Getter
    @ToString
    public static class Request {
        private String content;

        public Comment convertToEntity() {
            return Comment.builder()
                    .content(content)
                    .replies(new HashSet<>())
                    .build();
        }
    }

    @Getter
    @Setter
    public static class Response {
        private Long id;
        private String author;
        private String content;
        private Set<Reply> replies;
        private LocalDateTime createdAt;

        public Response(Comment comment) {
            this.id = comment.getId();
            this.author = comment.getAuthor().getNickname();
            this.content = comment.getContent();
            this.replies = comment.getReplies();
            this.createdAt = comment.getCreatedAt();
        }
    }
}
