package com.example.bobfriend.model.exception;

public class JwtInvalidException extends BusinessBadRequestException {
    public JwtInvalidException() {
        super("유효하지 않은 토큰입니다.");
    }
}
