package com.example.bobfriend.model.exception;

public class RecruitmentIsFullException extends BusinessConflictException {
    public RecruitmentIsFullException() {
        super("만석입니다.");
    }
}
