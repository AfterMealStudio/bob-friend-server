package com.example.bob_friend.model.exception;

import org.springframework.http.HttpStatus;


public class MemberDuplicatedException extends CustomException {
    HttpStatus httpStatus = HttpStatus.CONFLICT;

    public MemberDuplicatedException(String username) {
        super("username [ " + username + " ] is already exist");
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
