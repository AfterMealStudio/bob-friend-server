package com.example.bobfriend.validator;

import org.springframework.stereotype.Component;

@Component(value = "shortLengthStrategy")
public class ShortLengthStrategy implements PasswordValidationStrategy {
    @Override
    public boolean isValid(String password) {
        return false;
    }

    @Override
    public String getMessage() {
        return "{custom.validation.constraints.Password.ShortLength}";
    }
}
