package com.example.bob_friend.model.exception;

import org.springframework.http.HttpStatus;

public class RestrictionFailedException extends CustomException{

    public RestrictionFailedException(String username) {
        super(username + " does not fit the restriction");
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.FORBIDDEN;
    }
}
