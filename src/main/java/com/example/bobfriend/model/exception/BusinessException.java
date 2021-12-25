package com.example.bobfriend.model.exception;

public abstract class BusinessException extends RuntimeException{
    public BusinessException(String message) {
        super(message);
    }
}
