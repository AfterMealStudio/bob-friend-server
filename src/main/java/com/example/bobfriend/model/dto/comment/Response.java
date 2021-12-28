package com.example.bobfriend.model.dto.comment;

import com.example.bobfriend.model.dto.member.Preview;
import com.example.bobfriend.model.entity.Comment;

import com.example.bobfriend.model.entity.Reply;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@Setter
public class Response {
    private Long id;
    private Preview author;
    private String content;
    private List<com.example.bobfriend.model.dto.reply.Response> replies;
    private Integer reportCount;
    private LocalDateTime createdAt;

    public Response(Comment comment) {
        this.id = comment.getId();
        this.author = new Preview(comment.getAuthor());
        this.content = comment.getContent();
        this.replies = comment.getReplies().stream()
                .map(reply -> new com.example.bobfriend.model.dto.reply.Response(reply))
                .sorted(Comparator.comparing(response -> response.getCreatedAt()))
                .collect(Collectors.toList());
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

