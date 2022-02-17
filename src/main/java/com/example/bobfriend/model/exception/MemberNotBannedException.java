package com.example.bobfriend.model.exception;

public class MemberNotBannedException extends BusinessNotFoundException {
    public MemberNotBannedException() {
        super("차단한 적 없는 사용자입니다.");
    }
}
