package com.example.bobfriend.model.exception;

public class MemberAlreadyBannedException extends BusinessConflictException {
    public MemberAlreadyBannedException() {
        super("이미 차단한 회원입니다.");
    }
}
