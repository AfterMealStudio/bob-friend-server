package com.example.bob_friend.model.exception;

public class RecruitmentNotFoundException extends RuntimeException {
    public RecruitmentNotFoundException(Long recruitmentId) {
        super("Recruitment ("+recruitmentId+") is not found" );
    }
}
