package com.example.bob_friend.model.exception;

import org.springframework.http.HttpStatus;

public class RecruitmentAlreadyJoined extends CustomException {
    public RecruitmentAlreadyJoined(String currentMember) {
        super(currentMember+" is already joined in this recruitment");
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }
}
