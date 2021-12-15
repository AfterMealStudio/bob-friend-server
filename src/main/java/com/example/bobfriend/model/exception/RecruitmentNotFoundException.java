package com.example.bobfriend.model.exception;

public class RecruitmentNotFoundException extends BusinessNotFoundException {
    public RecruitmentNotFoundException(Long recruitmentId) {
        super("Recruitment (" + recruitmentId + ") is not found");
    }
}
