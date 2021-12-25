package com.example.bobfriend.model.exception;

public class JwtInvalidException extends BusinessBadRequestException {
    public JwtInvalidException() {
        super("token is invalid");
    }
}
