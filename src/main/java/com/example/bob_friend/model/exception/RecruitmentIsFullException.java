package com.example.bob_friend.model.exception;

import org.springframework.http.HttpStatus;

public class RecruitmentIsFullException extends CustomException {
    public RecruitmentIsFullException(Long recruitmentId) {
        super("Recruitment(" + recruitmentId + ") is full");
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.INSUFFICIENT_STORAGE;
    }
}
