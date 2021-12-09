package com.example.bobfriend.model.exception;

public class RefreshTokenNotFoundException extends CustomException {
    public RefreshTokenNotFoundException() {
        super("refresh token is not exist in database");
    }

//    @Override
//    public HttpStatus getHttpStatus() {
//        return HttpStatus.NOT_FOUND;
//    }
}
