package com.example.bob_friend.model.dto;

import com.example.bob_friend.model.entity.Comment;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;

@Setter
@Getter
@ToString
public class CommentRequestDto {
    private String content;

    public Comment convertToEntity() {
        return Comment.builder()
                .content(content)
                .replies(new HashSet<>())
                .build();
    }
}


