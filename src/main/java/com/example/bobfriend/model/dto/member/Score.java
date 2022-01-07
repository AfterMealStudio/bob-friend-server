package com.example.bobfriend.model.dto.member;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;

@Getter
@Setter
public class Score {
    @Min(0)
    private Double score;
}
