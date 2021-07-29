package com.example.bob_friend.model.card.dto;

import com.example.bob_friend.model.card.domain.Recruitment;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RecruitmentRequestDto {
    private String title;
    private String content;
    public RecruitmentRequestDto(Recruitment recruitment) {
    }
    public Recruitment convertToDomain() {
        return Recruitment.builder()
                .title(this.title)
                .content(this.content)
                .build();
    }
}
