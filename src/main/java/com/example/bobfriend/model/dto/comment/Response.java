package com.example.bobfriend.model.dto.comment;

import com.example.bobfriend.model.dto.MemberDto;
import com.example.bobfriend.model.dto.ReplyDto;
import com.example.bobfriend.model.entity.Comment;
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
    private MemberDto.Preview author;
    private String content;
    private List<ReplyDto.Response> replies;
    private Integer reportCount;
    private LocalDateTime createdAt;

    public Response(Comment comment) {
        this.id = comment.getId();
        this.author = new MemberDto.Preview(comment.getAuthor());
        this.content = comment.getContent();
        this.replies = comment.getReplies().stream()
                .map(ReplyDto.Response::new)
                .sorted(Comparator.comparing(ReplyDto.Response::getCreatedAt))
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

