package com.example.bob_friend.model.card.dto;

import com.example.bob_friend.model.card.domain.Recruitment;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RecruitmentResponseDto {
    private long id;
    public RecruitmentResponseDto(Recruitment recruitment) {
        this.id = recruitment.getId();
    }
}
