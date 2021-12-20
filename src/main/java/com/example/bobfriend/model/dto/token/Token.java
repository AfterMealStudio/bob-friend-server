package com.example.bobfriend.model.dto.token;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class Token {
    private String accessToken;
    private String refreshToken;
}
