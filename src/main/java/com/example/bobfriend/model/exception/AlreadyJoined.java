package com.example.bobfriend.model.exception;

import org.springframework.http.HttpStatus;

public class AlreadyJoined extends CustomException {
    public AlreadyJoined(String currentMember) {
        super(currentMember+" is already joined in this recruitment");
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }
}
