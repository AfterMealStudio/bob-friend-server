package com.example.bobfriend.model.exception;

public class AlreadyJoined extends BusinessConflictException {
    public AlreadyJoined() {
        super("이미 참여 중입니다.");
    }
}
