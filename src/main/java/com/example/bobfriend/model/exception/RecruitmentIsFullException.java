package com.example.bobfriend.model.exception;

public class RecruitmentIsFullException extends CustomException {
    public RecruitmentIsFullException(Long recruitmentId) {
        super("Recruitment(" + recruitmentId + ") is full");
    }

//    @Override
//    public HttpStatus getHttpStatus() {
//        return HttpStatus.CONFLICT;
//    }
}
