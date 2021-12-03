package com.example.bobfriend.model.exception;

import org.springframework.http.HttpStatus;

public class MemberNotActiveException extends CustomException {
    HttpStatus httpStatus = HttpStatus.LOCKED;
    public MemberNotActiveException(String username) {
        super(username + " is not active");
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
