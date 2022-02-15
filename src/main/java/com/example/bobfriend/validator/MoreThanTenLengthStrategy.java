package com.example.bobfriend.validator;

import org.springframework.stereotype.Component;

@Component(value = "moreThanTenLengthStrategy")
public class MoreThanTenLengthStrategy implements PasswordValidationStrategy {
    @Override
    public boolean isValid(String password) {
        int count = 0;
        if (isContainDigit(password)) count++;
        if (isContainLowerCase(password)) count++;
        if (isContainUpperCase(password)) count++;
        if (isContainSpecialCharacter(password)) count++;

        return count >= 2;
    }

    @Override
    public String getMessage() {
        return "{custom.validation.constraints.Password.MoreThanTenLength}";
    }
}
