package com.example.bobfriend.controller;

import com.example.bobfriend.model.dto.ErrorResponse;
import com.example.bobfriend.model.exception.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler {


    private ErrorResponse getErrorResponse(String message, Class<? extends Throwable> throwable) {
        return new ErrorResponse(message, throwable.getSimpleName());
    }

    @ExceptionHandler(value = CustomException.class)
    public ResponseEntity handleCustomException(CustomException e) {

        return new ResponseEntity(getErrorResponse(e.getMessage(), e.getClass()), e.getHttpStatus());
    }

    @ExceptionHandler(value = NumberFormatException.class)
    public ResponseEntity handleNumberFormatException(NumberFormatException e) {
        String message = "input is not valid : "+ e.getMessage();
        return new ResponseEntity(getErrorResponse(message, e.getClass()), HttpStatus.BAD_REQUEST);
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
        return new ResponseEntity(getErrorResponse(message.toString(),e.getClass()), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(value = MissingRequestValueException.class)
    public ResponseEntity handleMissingValueException(MissingRequestValueException e) {
        return new ResponseEntity(getErrorResponse(e.getMessage(), e.getClass()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = UsernameNotFoundException.class)
    public ResponseEntity handleUsernameNotFound(UsernameNotFoundException e) {
        return new ResponseEntity(getErrorResponse(e.getMessage() + " is not a member", e.getClass()) , HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = AuthenticationException.class)
    public ResponseEntity handleAuthenticationException(AuthenticationException e) {
        return new ResponseEntity(getErrorResponse(e.getMessage(), e.getClass()), HttpStatus.BAD_REQUEST);
    }
}
