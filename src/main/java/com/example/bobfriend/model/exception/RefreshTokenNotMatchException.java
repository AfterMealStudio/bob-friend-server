package com.example.bobfriend.model.exception;

public class RefreshTokenNotMatchException extends CustomException{
    public RefreshTokenNotMatchException() {
        super("token does not match");
    }

//    @Override
//    public HttpStatus getHttpStatus() {
//        return HttpStatus.BAD_REQUEST;
//    }
}
