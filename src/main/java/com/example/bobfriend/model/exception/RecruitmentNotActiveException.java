package com.example.bobfriend.model.exception;

public class RecruitmentNotActiveException extends CustomException {
    public RecruitmentNotActiveException(Long recruitmentId) {
        super("Recruitmet "+ recruitmentId + " is not active");
    }

//    @Override
//    public HttpStatus getHttpStatus() {
//        return HttpStatus.FORBIDDEN;
//    }
}
