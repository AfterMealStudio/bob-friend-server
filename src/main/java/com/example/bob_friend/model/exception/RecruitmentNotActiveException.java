package com.example.bob_friend.model.exception;

public class RecruitmentNotActiveException extends RuntimeException {
    public RecruitmentNotActiveException(Long recruitmentId) {
        super(recruitmentId + " is not active");
    }
}
