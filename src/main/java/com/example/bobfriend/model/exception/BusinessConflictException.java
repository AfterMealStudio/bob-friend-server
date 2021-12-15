package com.example.bobfriend.model.exception;

public abstract class BusinessConflictException extends BusinessException{
    public BusinessConflictException(String message) {
        super(message);
    }
}
