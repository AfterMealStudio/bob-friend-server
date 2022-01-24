package com.example.bobfriend.model.exception;

public class MemberNotAllowedException extends BusinessForbiddenException {
    public MemberNotAllowedException(String member) {
        super(member + " has no permission");
    }
}
