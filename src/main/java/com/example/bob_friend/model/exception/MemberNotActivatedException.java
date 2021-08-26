package com.example.bob_friend.model.exception;

public class MemberNotActivatedException extends RuntimeException {
    public MemberNotActivatedException(String username) {
        super(username + " is not active");
    }
}
