package com.example.bobfriend.validator;

import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
public class PasswordValidator implements ConstraintValidator<Password, String> {
    private final PasswordValidationStrategy validationStrategy;

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null) return false;

        boolean validationResult = validationStrategy.isValid(s);
        constraintValidatorContext
                .buildConstraintViolationWithTemplate(validationStrategy.getMessage());

        return validationResult;
    }

}
