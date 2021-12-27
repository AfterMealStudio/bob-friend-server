package com.example.bobfriend.model.exception;

public abstract class BusinessNotFoundException extends BusinessException{
    public BusinessNotFoundException(String message) {
        super(message);
    }
}
