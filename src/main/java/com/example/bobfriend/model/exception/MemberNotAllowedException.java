package com.example.bobfriend.model.exception;

public class MemberNotAllowedException extends CustomException{
    public MemberNotAllowedException(String member) {
        super(member + " has no permission");
    }

//    @Override
//    public HttpStatus getHttpStatus() {
//        return HttpStatus.FORBIDDEN;
//    }
}
