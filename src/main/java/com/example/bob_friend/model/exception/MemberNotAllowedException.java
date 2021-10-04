package com.example.bob_friend.model.exception;

import org.springframework.http.HttpStatus;

public class MemberNotAllowedException extends CustomException{
    public MemberNotAllowedException(String member) {
        super(member + " has no permission");
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.UNAUTHORIZED;
    }
}
