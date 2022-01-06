package com.example.bobfriend.model.exception;

public class CommentNotFoundException extends BusinessNotFoundException {
    public CommentNotFoundException() {
        super("댓글을 찾을 수 없습니다.");
    }
}
