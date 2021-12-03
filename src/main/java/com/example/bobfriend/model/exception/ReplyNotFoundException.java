package com.example.bobfriend.model.exception;

import org.springframework.http.HttpStatus;

public class ReplyNotFoundException extends CustomException {
    public ReplyNotFoundException(Long replyId) {
        super(replyId + " not found");
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
