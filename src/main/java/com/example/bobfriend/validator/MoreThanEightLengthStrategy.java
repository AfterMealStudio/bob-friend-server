package com.example.bobfriend.validator;

public class MoreThanEightLengthStrategy implements PasswordValidationStrategy {
    @Override
    public boolean isValid(String password) {
        int count = 0;

        if (isContainDigit(password)) count++;
        if (isContainLowerCase(password)) count++;
        if (isContainUpperCase(password)) count++;
        if (isContainSpecialCharacter(password)) count++;

        return count >= 3;
    }

    @Override
    public String getMessage() {
        return "{password.message.default}";
    }
}
