package com.example.bobfriend.model.exception;

public class RecruitmentNotFoundException extends BusinessNotFoundException {
    public RecruitmentNotFoundException() {
        super("게시글을 찾을 수 없습니다.");
    }
}
