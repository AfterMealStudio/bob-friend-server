package com.example.bobfriend.model.exception;

public class RecruitmentNotActiveException extends BusinessForbiddenException {
    public RecruitmentNotActiveException() {
        super("유효하지 않은 게시글입니다.");
    }
}
