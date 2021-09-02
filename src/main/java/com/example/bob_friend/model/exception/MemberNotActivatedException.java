package com.example.bob_friend.model.exception;

import org.springframework.http.HttpStatus;

public class MemberNotActivatedException extends CustomException {
    HttpStatus httpStatus = HttpStatus.LOCKED;
    public MemberNotActivatedException(String username) {
        super(username + " is not active");
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
