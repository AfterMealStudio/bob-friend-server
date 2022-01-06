package com.example.bobfriend.model.exception;

public class ReplyNotFoundException extends BusinessNotFoundException {
    public ReplyNotFoundException() {
        super("대댓글을 찾을 수 없습니다.");
    }
}
