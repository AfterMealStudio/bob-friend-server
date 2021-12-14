package com.example.bobfriend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class TokenDto {

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    public static class Token {
        private String accessToken;
        private String refreshToken;
    }


    @Getter
    @Setter
    public static class Validation {
        private Boolean isValid;

        public Validation(Boolean isValid) {
            this.isValid = isValid;
        }
    }
}
