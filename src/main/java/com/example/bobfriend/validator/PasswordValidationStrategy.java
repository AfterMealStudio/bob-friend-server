package com.example.bobfriend.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface PasswordValidationStrategy {
    String UPPER_CASES = "[A-Z]";
    String LOWER_CASES = "[a-z]";
    String DIGITS = "[0-9]";
    String SPECIAL_CHARACTERS = "[!#$%&()*+,./:;<=>?@^_`{|}~]";

    Pattern upperCasePattern = Pattern.compile(UPPER_CASES);
    Pattern lowerCasePattern = Pattern.compile(LOWER_CASES);
    Pattern digitsPattern = Pattern.compile(DIGITS);
    Pattern specialCharacterPattern = Pattern.compile(SPECIAL_CHARACTERS);

    boolean isValid(String password);

    String getMessage();


    default boolean isContainPattern(String s, Pattern pattern) {
        Matcher matcher = pattern.matcher(s);
        return matcher.find();
    }

    default boolean isContainUpperCase(String s) {
        return isContainPattern(s, upperCasePattern);
    }

    default boolean isContainLowerCase(String s) {
        return isContainPattern(s, lowerCasePattern);
    }

    default boolean isContainDigit(String s) {
        return isContainPattern(s, digitsPattern);
    }

    default boolean isContainSpecialCharacter(String s) {
        return isContainPattern(s, specialCharacterPattern);
    }
}
