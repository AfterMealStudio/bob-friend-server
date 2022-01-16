package com.example.bobfriend.validator;

import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
public class PasswordValidator implements ConstraintValidator<Password, String> {
    private PasswordValidationStrategy validationStrategy;

    public void setValidationStrategy(PasswordValidationStrategy validationStrategy) {
        this.validationStrategy = validationStrategy;
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null) return false;

        constraintValidatorContext
                .buildConstraintViolationWithTemplate(validationStrategy.getMessage());

        return validationStrategy.isValid(s);
    }

}
