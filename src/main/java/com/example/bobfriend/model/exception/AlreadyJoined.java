package com.example.bobfriend.model.exception;

public class AlreadyJoined extends BusinessConflictException {
    public AlreadyJoined(String currentMember) {
        super(currentMember+" is already joined in this recruitment");
    }
}
