package com.example.bob_friend.model.exception;

import org.springframework.http.HttpStatus;

public class RecruitmentNotActiveException extends CustomException {
    public RecruitmentNotActiveException(Long recruitmentId) {
        super(recruitmentId + " is not active");
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.FORBIDDEN;
    }
}
