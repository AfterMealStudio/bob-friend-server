package com.example.bobfriend.model.exception;

public class RecruitmentNotActiveException extends BusinessForbiddenException {
    public RecruitmentNotActiveException(Long recruitmentId) {
        super("Recruitmet "+ recruitmentId + " is not active");
    }
}
