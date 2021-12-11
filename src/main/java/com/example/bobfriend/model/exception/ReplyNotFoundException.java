package com.example.bobfriend.model.exception;

public class ReplyNotFoundException extends BusinessNotFoundException {
    public ReplyNotFoundException(Long replyId) {
        super(replyId + " not found");
    }
}
