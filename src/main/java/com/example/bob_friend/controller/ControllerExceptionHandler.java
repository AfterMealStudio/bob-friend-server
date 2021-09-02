package com.example.bob_friend.controller;

import com.example.bob_friend.model.exception.CustomException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(value = CustomException.class)
    public ResponseEntity handleCustomException(CustomException e) {
        return new ResponseEntity(e.getMessage(), e.getHttpStatus());
    }
}
