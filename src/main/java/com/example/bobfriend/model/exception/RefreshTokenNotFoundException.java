package com.example.bobfriend.model.exception;

public class RefreshTokenNotFoundException extends BusinessNotFoundException {
    public RefreshTokenNotFoundException() {
        super("refresh token is not exist in database");
    }
}
