package com.example.bobfriend.model.exception;

public class RecruitmentAlreadyBannedException extends BusinessConflictException {
    public RecruitmentAlreadyBannedException() {
        super("이미 차단된 글입니다.");
    }
}
