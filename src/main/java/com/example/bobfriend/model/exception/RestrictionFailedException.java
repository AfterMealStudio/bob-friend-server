package com.example.bobfriend.model.exception;

public class RestrictionFailedException extends BusinessForbiddenException {
    public RestrictionFailedException(String username) {
        super(username + " does not fit the restriction");
    }
}
