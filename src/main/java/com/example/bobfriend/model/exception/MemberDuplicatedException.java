package com.example.bobfriend.model.exception;

public class MemberDuplicatedException extends BusinessConflictException {
    public MemberDuplicatedException() {
        super("이미 존재하는 회원입니다.");
    }
}
