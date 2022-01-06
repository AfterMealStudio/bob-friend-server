package com.example.bobfriend.model.exception;

public class AlreadyReportedException extends BusinessConflictException {
    public AlreadyReportedException() {
        super("이미 신고한 게시물입니다.");
    }
}
