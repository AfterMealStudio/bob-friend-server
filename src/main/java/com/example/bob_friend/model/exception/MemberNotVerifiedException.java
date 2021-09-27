package com.example.bob_friend.model.exception;

import org.springframework.http.HttpStatus;

public class MemberNotVerifiedException extends CustomException {
    public MemberNotVerifiedException(String email) {
        super("Please check your email : " + email);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.UNAUTHORIZED;
    }
}
