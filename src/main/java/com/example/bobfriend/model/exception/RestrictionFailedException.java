package com.example.bobfriend.model.exception;

public class RestrictionFailedException extends BusinessForbiddenException {
    public RestrictionFailedException() {
        super("조건과 맞지 않습니다.");
    }
}
