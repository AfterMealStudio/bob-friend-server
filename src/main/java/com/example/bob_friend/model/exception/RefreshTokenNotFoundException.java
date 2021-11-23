package com.example.bob_friend.model.exception;

import org.springframework.http.HttpStatus;

public class RefreshTokenNotFoundException extends CustomException {
    public RefreshTokenNotFoundException() {
        super("refresh token is not exist in database");
    }

    @Override
    public HttpStatus getHttpStatus() {
        return null;
    }
}
