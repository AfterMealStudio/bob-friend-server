package com.example.bobfriend.model.dto.reply;

import com.example.bobfriend.model.entity.Reply;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
public class Create {
    @NotBlank
    private String content;

    public Create(Reply reply) {
        this.content = reply.getContent();
    }
}