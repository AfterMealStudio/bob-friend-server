package com.example.bobfriend.model.exception;

public abstract class CustomException extends RuntimeException{
    public CustomException(String message) {
        super(message);
    }

//    public abstract HttpStatus getHttpStatus();
}
