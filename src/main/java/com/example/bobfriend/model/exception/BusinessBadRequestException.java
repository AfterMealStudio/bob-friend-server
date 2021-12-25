package com.example.bobfriend.model.exception;

public abstract class BusinessBadRequestException extends BusinessException{
    public BusinessBadRequestException(String message) {
        super(message);
    }
}
