package com.example.bob_friend.controller;

import com.example.bob_friend.model.exception.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(value = CustomException.class)
    public ResponseEntity handleCustomException(CustomException e) {
        return new ResponseEntity(e.getMessage(), e.getHttpStatus());
    }

    @ExceptionHandler(value = NumberFormatException.class)
    public ResponseEntity handleNumberFormatException(NumberFormatException e) {
        String message = "input is not valid : "+ e.getMessage();
        return new ResponseEntity(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity handleArgumentNotValidException(MethodArgumentNotValidException e) {
        StringBuilder message = new StringBuilder();
        for (FieldError error : e.getFieldErrors()) {
            message.append(error.getField());
            message.append(" ");
            message.append(error.getDefaultMessage());
            message.append(", input : ");
            message.append(error.getRejectedValue());
        }
        return new ResponseEntity(message.toString(), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(value = MissingRequestValueException.class)
    public ResponseEntity handleMissingValueException(MissingRequestValueException e) {
        return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
