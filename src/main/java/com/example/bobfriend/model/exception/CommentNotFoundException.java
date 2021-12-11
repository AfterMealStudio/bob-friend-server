package com.example.bobfriend.model.exception;

public class CommentNotFoundException extends BusinessNotFoundException {
    public CommentNotFoundException(Long commentId) {
        super("Comment (" + commentId + ") is not found");
    }
}
