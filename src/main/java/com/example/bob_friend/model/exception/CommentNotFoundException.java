package com.example.bob_friend.model.exception;

import org.springframework.http.HttpStatus;

public class CommentNotFoundException extends CustomException {
    public CommentNotFoundException(Long commentId) {
        super("Comment (" + commentId + ") is not found");
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
