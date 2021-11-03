package com.example.bob_friend.model.exception;

import org.springframework.http.HttpStatus;

public class MemberWithdrawalException extends CustomException {
    public MemberWithdrawalException() {
        super("author is left");
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
