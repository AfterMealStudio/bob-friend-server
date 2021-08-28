package com.example.bob_friend.model.exception;

public class RecruitmentIsFullException extends RuntimeException {
    public RecruitmentIsFullException() {
        super("Recruitment is full");
    }
}
