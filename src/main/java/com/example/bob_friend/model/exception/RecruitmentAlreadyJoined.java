package com.example.bob_friend.model.exception;

public class RecruitmentAlreadyJoined extends RuntimeException {
    public RecruitmentAlreadyJoined(String currentMember) {
        super(currentMember+" is already joined in this recruitment");
    }
}
