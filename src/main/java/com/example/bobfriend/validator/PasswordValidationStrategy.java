package com.example.bobfriend.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface PasswordValidationStrategy {
    String UPPER_CASES = "[A-Z]";
    String LOWER_CASES = "[a-z]";
    String DIGITS = "[0-9]";
    String SPECIAL_CHARACTERS = "[!#$%&()*+,./:;<=>?@^_`{|}~]";

    boolean isValid(String password);

    String getMessage();


    default boolean isContainUpperCase(String s) {
        Pattern pattern = Pattern.compile(UPPER_CASES);
        Matcher matcher = pattern.matcher(s);
        return matcher.find();
    }

    default boolean isContainLowerCase(String s) {
        Pattern pattern = Pattern.compile(LOWER_CASES);
        Matcher matcher = pattern.matcher(s);
        return matcher.find();
    }

    default boolean isContainDigit(String s) {
        Pattern pattern = Pattern.compile(DIGITS);
        Matcher matcher = pattern.matcher(s);
        return matcher.find();
    }

    default boolean isContainSpecialCharacter(String s) {
        Pattern pattern = Pattern.compile(SPECIAL_CHARACTERS);
        Matcher matcher = pattern.matcher(s);
        return matcher.find();
    }
}
