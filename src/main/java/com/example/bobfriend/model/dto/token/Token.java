package com.example.bobfriend.model.dto.token;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Token {
    private String accessToken;
    private String refreshToken;
}
