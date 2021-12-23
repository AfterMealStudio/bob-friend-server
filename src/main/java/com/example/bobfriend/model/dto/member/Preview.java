package com.example.bobfriend.model.dto.member;

import com.example.bobfriend.model.entity.Member;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class Preview {
    private Long id;
    private String nickname;
    private Double rating;

    public Preview(Member member) {
        this.id = member.getId();
        this.nickname = member.getNickname();
        this.rating = member.getRating();
    }

}
