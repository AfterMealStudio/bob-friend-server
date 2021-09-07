package com.example.bob_friend.model.exception;

import org.springframework.http.HttpStatus;

public class NotAMemberOfRecruitentException extends CustomException {
    public NotAMemberOfRecruitentException(String username) {
        super(username + " is not a member");
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.FORBIDDEN;
    }
}
