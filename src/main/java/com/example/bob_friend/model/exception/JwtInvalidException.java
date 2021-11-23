package com.example.bob_friend.model.exception;

import org.springframework.http.HttpStatus;

public class JwtInvalidException extends CustomException{
    public JwtInvalidException() {
        super("token is invalid");
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
