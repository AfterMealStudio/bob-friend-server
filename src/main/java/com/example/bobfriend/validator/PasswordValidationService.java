package com.example.bobfriend.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class PasswordValidationService {

    private final Map<String, PasswordValidationStrategy> passwordValidationStrategyMap;
    PasswordValidationStrategy validationStrategy;

    @PostConstruct
    public void init() {
        validationStrategy = passwordValidationStrategyMap.get("shortLengthStrategy");
    }

    public boolean isValid(String password) {
        int length = password.length();

        if (length >= 10)
            this.validationStrategy = passwordValidationStrategyMap.get("moreThanTenLengthStrategy");
        else if (length >= 8)
            this.validationStrategy = passwordValidationStrategyMap.get("moreThanEightLengthStrategy");

        return validationStrategy.isValid(password);
    }

    public String getMessage() {
        return this.validationStrategy.getMessage();
    }
}
