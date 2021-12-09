package com.example.bobfriend.model.exception;

import com.example.bobfriend.model.entity.Writing;

public class AlreadyReportedExeption extends CustomException{
    public AlreadyReportedExeption(Writing writing) {
        super("you already report "+ writing.getDiscriminatorValue() +writing.getId());

    }

//    @Override
//    public HttpStatus getHttpStatus() {
//        return HttpStatus.CONFLICT;
//    }
}
