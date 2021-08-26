package com.example.bob_friend.model.exception;

public class RecruitmentIsFullException extends RuntimeException {
    public RecruitmentIsFullException(Long recruitmentId) {
        super(recruitmentId+" is full");
    }
}
