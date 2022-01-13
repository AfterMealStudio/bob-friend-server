package com.example.bobfriend.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<Password, String> {
    private static final String UPPER_CASES = "[A-Z]";
    private static final String LOWER_CASES = "[a-z]";
    private static final String DIGITS = "[0-9]";
    private static final String SPECIAL_CHARACTERS = "[!#$%&()*+,./:;<=>?@^_`{|}~]";

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null) return false;

        int count = 0;
        if (isContainDigit(s)) count++;
        if (isContainLowerCase(s)) count++;
        if (isContainUpperCase(s)) count++;
        if (isContainSpecialCharacter(s)) count++;

        int length = s.length();
        if (length == 0) {
            constraintValidatorContext
                    .buildConstraintViolationWithTemplate("{password.too_short}");
        }
        if (length >= 10 && count >= 2) return true;
        else if (length >= 8 && count >= 3) return true;
        constraintValidatorContext
                .buildConstraintViolationWithTemplate("{password.message}");

        return false;
    }

    private boolean isContainUpperCase(String s) {
        Pattern pattern = Pattern.compile(UPPER_CASES);
        Matcher matcher = pattern.matcher(s);
        return matcher.find();
    }

    private boolean isContainLowerCase(String s) {
        Pattern pattern = Pattern.compile(LOWER_CASES);
        Matcher matcher = pattern.matcher(s);
        return matcher.find();
    }

    private boolean isContainDigit(String s) {
        Pattern pattern = Pattern.compile(DIGITS);
        Matcher matcher = pattern.matcher(s);
        return matcher.find();
    }

    private boolean isContainSpecialCharacter(String s) {
        Pattern pattern = Pattern.compile(SPECIAL_CHARACTERS);
        Matcher matcher = pattern.matcher(s);
        return matcher.find();
    }
}
