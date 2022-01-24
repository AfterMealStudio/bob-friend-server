package com.example.bobfriend.controller;

import com.example.bobfriend.model.dto.ErrorResponse;
import com.example.bobfriend.model.exception.BusinessBadRequestException;
import com.example.bobfriend.model.exception.BusinessConflictException;
import com.example.bobfriend.model.exception.BusinessForbiddenException;
import com.example.bobfriend.model.exception.BusinessNotFoundException;
import io.jsonwebtoken.ExpiredJwtException;
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
public class BusinessExceptionHandler {

    @ExceptionHandler(value = {BusinessConflictException.class})
    public ResponseEntity handleConflictException(BusinessConflictException e) {
        ErrorResponse response = ErrorResponse.of(e.getMessage(), e.getClass());
        return new ResponseEntity(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = {BusinessNotFoundException.class})
    public ResponseEntity handleNotFoundException(BusinessNotFoundException e) {
        ErrorResponse response = ErrorResponse.of(e.getMessage(), e.getClass());
        return new ResponseEntity(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {BusinessBadRequestException.class})
    public ResponseEntity handleBadRequestException(BusinessBadRequestException e) {
        ErrorResponse response = ErrorResponse.of(e.getMessage(), e.getClass());
        return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {BusinessForbiddenException.class})
    public ResponseEntity handleForbiddenException(BusinessForbiddenException e) {
        ErrorResponse response = ErrorResponse.of(e.getMessage(), e.getClass());
        return new ResponseEntity(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = UsernameNotFoundException.class)
    public ResponseEntity handleUsernameNotFound(UsernameNotFoundException e) {
        ErrorResponse response = ErrorResponse.of(
                e.getMessage() + " is not a member", e.getClass());
        return new ResponseEntity(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = ExpiredJwtException.class)
    public ResponseEntity handleJwtExpireException(ExpiredJwtException e) {
        ErrorResponse response = ErrorResponse.of(e.getMessage(), e.getClass());
        return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(value = NumberFormatException.class)
    public ResponseEntity handleNumberFormatException(NumberFormatException e) {
        String message = "input is not valid : " + e.getMessage();
        ErrorResponse response = ErrorResponse.of(message, e.getClass());
        return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity handleArgumentNotValidException(MethodArgumentNotValidException e) {
        StringBuilder message = new StringBuilder();
        for (FieldError error : e.getFieldErrors()) {
            message.append(error.getDefaultMessage());
        }
        ErrorResponse response = ErrorResponse.of(message.toString(), e.getClass());
        return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(value = MissingRequestValueException.class)
    public ResponseEntity handleMissingValueException(MissingRequestValueException e) {
        ErrorResponse response = ErrorResponse.of(e.getMessage(), e.getClass());
        return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = AuthenticationException.class)
    public ResponseEntity handleAuthenticationException(AuthenticationException e) {
        ErrorResponse response = ErrorResponse.of(e.getMessage(), e.getClass());
        return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
    }
}
