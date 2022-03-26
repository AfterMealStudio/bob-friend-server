package com.example.bobfriend.model.exception;

public class WritingNotFoundException extends BusinessNotFoundException {
    public WritingNotFoundException() {
        super("게시물을 찾을 수 없습니다.");
    }
}
