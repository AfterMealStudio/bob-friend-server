package com.example.bobfriend.model.exception;

public class RestrictionFailedException extends CustomException{

    public RestrictionFailedException(String username) {
        super(username + " does not fit the restriction");
    }

//    @Override
//    public HttpStatus getHttpStatus() {
//        return HttpStatus.FORBIDDEN;
//    }
}
