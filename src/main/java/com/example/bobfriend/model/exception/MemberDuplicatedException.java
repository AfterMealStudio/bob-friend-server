package com.example.bobfriend.model.exception;

public class MemberDuplicatedException extends BusinessConflictException {
    public MemberDuplicatedException(String username) {
        super("username [ " + username + " ] is already exist");
    }
}
