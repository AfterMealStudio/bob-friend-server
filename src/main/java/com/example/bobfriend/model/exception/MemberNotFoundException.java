package com.example.bobfriend.model.exception;

public class MemberNotFoundException extends BusinessNotFoundException {
    public MemberNotFoundException() {
        super("member is not found");
    }
}
