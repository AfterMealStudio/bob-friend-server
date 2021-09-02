package com.example.bob_friend.model.exception;

import org.springframework.http.HttpStatus;

public class RecruitmentNotFoundException extends CustomException {
    public RecruitmentNotFoundException(Long recruitmentId) {
        super("Recruitment (" + recruitmentId + ") is not found");
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
