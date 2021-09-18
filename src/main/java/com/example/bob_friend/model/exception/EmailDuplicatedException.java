package com.example.bob_friend.model.exception;

import org.springframework.http.HttpStatus;

public class EmailDuplicatedException extends CustomException {
    public EmailDuplicatedException(String email) {
        super("email [ " + email + " ] is already exist");
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }
}
