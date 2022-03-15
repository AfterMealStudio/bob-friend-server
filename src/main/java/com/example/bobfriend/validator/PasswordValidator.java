package com.example.bobfriend.validator;


import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


@RequiredArgsConstructor
public class PasswordValidator implements ConstraintValidator<Password, String> {

    private final PasswordValidationService passwordValidationService;


    @Override
    public void initialize(Password constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null) return false;
        
        boolean validationResult = passwordValidationService.isValid(s);

        constraintValidatorContext
                .buildConstraintViolationWithTemplate(passwordValidationService.getMessage());

        return validationResult;
    }

}
