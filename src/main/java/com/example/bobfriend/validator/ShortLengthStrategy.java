package com.example.bobfriend.validator;

public class ShortLengthStrategy implements PasswordValidationStrategy {
    @Override
    public boolean isValid(String password) {
        return false;
    }

    @Override
    public String getMessage() {
        return "{password.message.too_short}";
    }
}
