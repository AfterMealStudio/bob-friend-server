package com.example.bob_friend.model.dto;

import com.example.bob_friend.model.entity.Comment;
import com.example.bob_friend.model.entity.Reply;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class CommentDto {
    @Setter
    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        private String content;


        public Request(Comment comment) {
            this.content = comment.getContent();
        }

        public Comment convertToEntity() {
            return Comment.builder()
                    .content(content)
                    .replies(new HashSet<>())
                    .build();
        }
    }

    @Getter
    @Setter
    @ToString
    public static class Response {
        private Long id;
        private MemberDto.Preview author;
        private String content;
        private Set<ReplyDto.Response> replies;
        private Integer reportCount;
        private LocalDateTime createdAt;

        public Response(Comment comment) {
            this.id = comment.getId();
            this.author = new MemberDto.Preview(comment.getAuthor());
            this.content = comment.getContent();
            this.replies = comment.getReplies().stream()
                    .map(reply -> new ReplyDto.Response(reply))
                    .collect(Collectors.toSet());
            this.reportCount = comment.getReportCount();
            this.createdAt = comment.getCreatedAt();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Response response = (Response) o;
            return Objects.equals(id, response.id) && Objects.equals(author, response.author);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, author);
        }
    }
}
