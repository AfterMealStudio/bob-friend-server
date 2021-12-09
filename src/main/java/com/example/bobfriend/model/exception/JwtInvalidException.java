package com.example.bobfriend.model.exception;

public class JwtInvalidException extends CustomException{
    public JwtInvalidException() {
        super("token is invalid");
    }

//    @Override
//    public HttpStatus getHttpStatus() {
//        return HttpStatus.BAD_REQUEST;
//    }
}
