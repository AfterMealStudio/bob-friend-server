package com.example.bobfriend.validator;

import org.springframework.stereotype.Component;

@Component
public class PasswordValidationStrategySelector implements PasswordValidationStrategy {
    private PasswordValidationStrategy passwordValidationStrategy;


    @Override
    public boolean isValid(String password) {
        int length = password.length();
        if (length >= 10)
            this.passwordValidationStrategy = new MoreThanTenLengthStrategy();
        else if (length >= 8)
            this.passwordValidationStrategy = new MoreThanEightLengthStrategy();
        else
            this.passwordValidationStrategy = new ShortLengthStrategy();

        return passwordValidationStrategy.isValid(password);
    }

    @Override
    public String getMessage() {
        return this.passwordValidationStrategy.getMessage();
    }
}
