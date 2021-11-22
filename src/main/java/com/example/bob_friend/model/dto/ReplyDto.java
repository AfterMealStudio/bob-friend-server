package com.example.bob_friend.model.dto;

import com.example.bob_friend.model.entity.Reply;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class ReplyDto {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Request {
        @NotBlank
        private String content;

        public Reply convertToEntity() {
            return Reply.builder()
                    .content(content)
                    .build();
        }

        public Request(Reply reply) {
            this.content = reply.getContent();
        }
    }

    @Getter
    @Setter
    public static class Response {
        private Long id;
        private MemberDto.Preview author;
        private String content;
        private Integer reportCount;
        private LocalDateTime createdAt;

        public Response(Reply reply) {
            this.id = reply.getId();
            this.author = new MemberDto.Preview(reply.getAuthor());
            this.content = reply.getContent();
            this.reportCount = reply.getReportCount();
            this.createdAt = reply.getCreatedAt();
        }
    }
}
