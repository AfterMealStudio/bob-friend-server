package com.example.bob_friend.model.exception;

public class MemberDuplicatedException extends RuntimeException {
    public MemberDuplicatedException(String username) {
        super(username + " is already exist");
    }
}
