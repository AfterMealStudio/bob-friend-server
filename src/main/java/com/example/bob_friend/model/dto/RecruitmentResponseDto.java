package com.example.bob_friend.model.dto;

import com.example.bob_friend.model.domain.Recruitment;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RecruitmentResponseDto {
    private long id;
    private String title;
    private String content;
    public RecruitmentResponseDto(Recruitment recruitment) {
        this.id = recruitment.getId();
        this.title = recruitment.getTitle();
        this.content = recruitment.getContent();
    }
}
