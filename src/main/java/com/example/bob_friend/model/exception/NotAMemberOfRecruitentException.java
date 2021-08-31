package com.example.bob_friend.model.exception;

public class NotAMemberOfRecruitentException extends RuntimeException {
    public NotAMemberOfRecruitentException(String username) {
        super(username + " is not a member");
    }
}
