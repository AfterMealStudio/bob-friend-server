package com.example.bobfriend.model.exception;

public class RefreshTokenNotFoundException extends BusinessNotFoundException {
    public RefreshTokenNotFoundException() {
        super("리프레시 토큰을 찾을 수 없습니다.");
    }
}
