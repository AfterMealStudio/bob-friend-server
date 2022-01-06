package com.example.bobfriend.model.exception;

public class RefreshTokenNotMatchException extends BusinessBadRequestException {
    public RefreshTokenNotMatchException() {
        super("토큰의 정보가 저장된 것과 다릅니다.");
    }
}
