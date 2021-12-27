package com.example.bobfriend.model.exception;

public class RecruitmentIsFullException extends BusinessConflictException {
    public RecruitmentIsFullException(Long recruitmentId) {
        super("Recruitment(" + recruitmentId + ") is full");
    }

//    @Override
//    public HttpStatus getHttpStatus() {
//        return HttpStatus.CONFLICT;
//    }
}
