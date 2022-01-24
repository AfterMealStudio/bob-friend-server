package com.example.bobfriend.model.exception;

public class RefreshTokenNotMatchException extends BusinessBadRequestException {
    public RefreshTokenNotMatchException() {
        super("token does not match");
    }
}
