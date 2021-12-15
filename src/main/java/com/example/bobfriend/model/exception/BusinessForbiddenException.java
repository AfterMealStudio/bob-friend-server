package com.example.bobfriend.model.exception;

public abstract class BusinessForbiddenException extends BusinessException{
    public BusinessForbiddenException(String message) {
        super(message);
    }
}
