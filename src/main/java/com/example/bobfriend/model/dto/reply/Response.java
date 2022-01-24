package com.example.bobfriend.model.dto.reply;

import com.example.bobfriend.model.dto.member.Preview;
import com.example.bobfriend.model.entity.Reply;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Response {
    private Long id;
    private Preview author;
    private String content;
    private Integer reportCount;
    private LocalDateTime createdAt;

    public Response(Reply reply) {
        this.id = reply.getId();
        this.author = new Preview(reply.getAuthor());
        this.content = reply.getContent();
        this.reportCount = reply.getReportCount();
        this.createdAt = reply.getCreatedAt();
    }
}
