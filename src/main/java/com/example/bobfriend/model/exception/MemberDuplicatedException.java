package com.example.bobfriend.model.exception;

public class MemberDuplicatedException extends CustomException {
//    HttpStatus httpStatus = HttpStatus.CONFLICT;

    public MemberDuplicatedException(String username) {
        super("username [ " + username + " ] is already exist");
    }

//    @Override
//    public HttpStatus getHttpStatus() {
//        return httpStatus;
//    }
}
