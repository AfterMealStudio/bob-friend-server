package com.example.bobfriend.model.exception;

public class MemberNotAllowedException extends BusinessForbiddenException {
    public MemberNotAllowedException() {
        super("권한이 없습니다.");
    }
}
