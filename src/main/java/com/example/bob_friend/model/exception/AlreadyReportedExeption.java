package com.example.bob_friend.model.exception;

import com.example.bob_friend.model.entity.Writing;
import org.springframework.http.HttpStatus;

public class AlreadyReportedExeption extends CustomException{
    public AlreadyReportedExeption(Writing writing) {
        super("you already report "+ writing.getDiscriminatorValue() +writing.getId());

    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }
}
