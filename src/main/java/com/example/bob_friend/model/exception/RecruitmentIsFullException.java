package com.example.bob_friend.model.exception;

import org.springframework.http.HttpStatus;

public class RecruitmentIsFullException extends CustomException {
    public RecruitmentIsFullException() {
        super("Recruitment is full");
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.INSUFFICIENT_STORAGE;
    }
}
