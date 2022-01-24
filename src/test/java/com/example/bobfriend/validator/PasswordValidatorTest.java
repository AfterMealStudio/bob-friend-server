package com.example.bobfriend.validator;

import com.example.bobfriend.model.dto.member.Signup;
import com.example.bobfriend.model.entity.Sex;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


@SpringBootTest
public class PasswordValidatorTest {
    @Autowired
    Validator validator;


    @Test
    void lessThanEightFailTest() {
        Signup signup = Signup.builder()
                .password("1234")
                .email("test@test.com")
                .nickname("testNickName")
                .birth(LocalDate.now().minusYears(10))
                .sex(Sex.NONE)
                .agree(true)
                .build();

        Set<ConstraintViolation<Signup>> validate = validator.validate(signup);

        ConstraintViolation<Signup> violation = validate.iterator().next();
        assertThat(violation.getMessage(), equalTo("{password.message.default}"));
    }


    @Test
    void moreThanEightLowerCaseAndDigitFailTest() {
        Signup signup = Signup.builder()
                .password("eight1234")
                .email("test@test.com")
                .nickname("testNickName")
                .birth(LocalDate.now().minusYears(10))
                .sex(Sex.NONE)
                .agree(true)
                .build();

        Set<ConstraintViolation<Signup>> validate = validator.validate(signup);

        ConstraintViolation<Signup> violation = validate.iterator().next();
        assertThat(violation.getMessage(), equalTo("{password.message.default}"));
    }


    @Test
    void moreThanEightLowerCaseAndUpperCaseFailTest() {
        Signup signup = Signup.builder()
                .password("eightQWER")
                .email("test@test.com")
                .nickname("testNickName")
                .birth(LocalDate.now().minusYears(10))
                .sex(Sex.NONE)
                .agree(true)
                .build();

        Set<ConstraintViolation<Signup>> validate = validator.validate(signup);

        ConstraintViolation<Signup> violation = validate.iterator().next();
        assertThat(violation.getMessage(), equalTo("{password.message.default}"));
    }


    @Test
    void moreThanEightLowerCaseAndSpecialCharacterFailTest() {
        Signup signup = Signup.builder()
                .password("eight!@#$")
                .email("test@test.com")
                .nickname("testNickName")
                .birth(LocalDate.now().minusYears(10))
                .sex(Sex.NONE)
                .agree(true)
                .build();

        Set<ConstraintViolation<Signup>> validate = validator.validate(signup);

        ConstraintViolation<Signup> violation = validate.iterator().next();
        assertThat(violation.getMessage(), equalTo("{password.message.default}"));
    }


    @Test
    void moreThanEightUpperCaseAndDigitFailTest() {
        Signup signup = Signup.builder()
                .password("EIGHT1234")
                .email("test@test.com")
                .nickname("testNickName")
                .birth(LocalDate.now().minusYears(10))
                .sex(Sex.NONE)
                .agree(true)
                .build();

        Set<ConstraintViolation<Signup>> validate = validator.validate(signup);

        ConstraintViolation<Signup> violation = validate.iterator().next();
        assertThat(violation.getMessage(), equalTo("{password.message.default}"));
    }


    @Test
    void moreThanEightUpperCaseAndSpecialCharacterFailTest() {
        Signup signup = Signup.builder()
                .password("EIGHT!@#$")
                .email("test@test.com")
                .nickname("testNickName")
                .birth(LocalDate.now().minusYears(10))
                .sex(Sex.NONE)
                .agree(true)
                .build();

        Set<ConstraintViolation<Signup>> validate = validator.validate(signup);

        ConstraintViolation<Signup> violation = validate.iterator().next();
        assertThat(violation.getMessage(), equalTo("{password.message.default}"));
    }


    @Test
    void moreThanTenUpperCaseFailTest() {
        Signup signup = Signup.builder()
                .password("MORETHANTEN")
                .email("test@test.com")
                .nickname("testNickName")
                .birth(LocalDate.now().minusYears(10))
                .sex(Sex.NONE)
                .agree(true)
                .build();

        Set<ConstraintViolation<Signup>> validate = validator.validate(signup);

        ConstraintViolation<Signup> violation = validate.iterator().next();
        assertThat(violation.getMessage(), equalTo("{password.message.default}"));
    }


    @Test
    void moreThanTenLowerCaseFailTest() {
        Signup signup = Signup.builder()
                .password("morethanten")
                .email("test@test.com")
                .nickname("testNickName")
                .birth(LocalDate.now().minusYears(10))
                .sex(Sex.NONE)
                .agree(true)
                .build();

        Set<ConstraintViolation<Signup>> validate = validator.validate(signup);

        ConstraintViolation<Signup> violation = validate.iterator().next();
        assertThat(violation.getMessage(), equalTo("{password.message.default}"));
    }

    @Test
    void moreThanTenDigitFailTest() {
        Signup signup = Signup.builder()
                .password("12345678901")
                .email("test@test.com")
                .nickname("testNickName")
                .birth(LocalDate.now().minusYears(10))
                .sex(Sex.NONE)
                .agree(true)
                .build();

        Set<ConstraintViolation<Signup>> validate = validator.validate(signup);

        ConstraintViolation<Signup> violation = validate.iterator().next();
        assertThat(violation.getMessage(), equalTo("{password.message.default}"));
    }


    @Test
    void moreThanTenSpecialCharacterFailTest() {
        Signup signup = Signup.builder()
                .password("!@#$%^&&*!@#")
                .email("test@test.com")
                .nickname("testNickName")
                .birth(LocalDate.now().minusYears(10))
                .sex(Sex.NONE)
                .agree(true)
                .build();

        Set<ConstraintViolation<Signup>> validate = validator.validate(signup);

        ConstraintViolation<Signup> violation = validate.iterator().next();
        assertThat(violation.getMessage(), equalTo("{password.message.default}"));
    }

    @Test
    void moreThanTenAndUpperCaseAndLowerCaseSuccessTest() {
        Signup moreThanTenUpperCaseAndLowerCase = Signup.builder()
                .password("moreThanTen")
                .email("test@test.com")
                .nickname("testNickName")
                .birth(LocalDate.now().minusYears(10))
                .sex(Sex.NONE)
                .agree(true)
                .build();

        Set<ConstraintViolation<Signup>> validate = validator.validate(moreThanTenUpperCaseAndLowerCase);
        assertThat(validate.size(), equalTo(0));
    }


    @Test
    void moreThanTenAndUpperCaseAndDigitSuccessTest() {
        Signup moreThanTenUpperCaseAndDigit = Signup.builder()
                .password("MORETHANTEN1")
                .email("test@test.com")
                .nickname("testNickName")
                .birth(LocalDate.now().minusYears(10))
                .sex(Sex.NONE)
                .agree(true)
                .build();

        Set<ConstraintViolation<Signup>> validate = validator.validate(moreThanTenUpperCaseAndDigit);
        assertThat(validate.size(), equalTo(0));
    }


    @Test
    void moreThanTenUpperCaseAndSpecialCharacterSuccessTest() {
        Signup moreThanTenUpperCaseAndSpecialCharacter = Signup.builder()
                .password("MORETHANTEN!")
                .email("test@test.com")
                .nickname("testNickName")
                .birth(LocalDate.now().minusYears(10))
                .sex(Sex.NONE)
                .agree(true)
                .build();

        Set<ConstraintViolation<Signup>> validate = validator.validate(moreThanTenUpperCaseAndSpecialCharacter);
        assertThat(validate.size(), equalTo(0));
    }


    @Test
    void moreThanTenLowerCaseAndDigitSuccessTest() {
        Signup moreThanTenLowerCaseAndDigit = Signup.builder()
                .password("morethanten2")
                .email("test@test.com")
                .nickname("testNickName")
                .birth(LocalDate.now().minusYears(10))
                .sex(Sex.NONE)
                .agree(true)
                .build();

        Set<ConstraintViolation<Signup>> validate = validator.validate(moreThanTenLowerCaseAndDigit);
        assertThat(validate.size(), equalTo(0));
    }


    @Test
    void moreThanTenLowerCaseAndSpecialCharacterSuccessTest() {
        Signup moreThanTenLowerCaseAndSpecialCharacter = Signup.builder()
                .password("morethanten!")
                .email("test@test.com")
                .nickname("testNickName")
                .birth(LocalDate.now().minusYears(10))
                .sex(Sex.NONE)
                .agree(true)
                .build();

        Set<ConstraintViolation<Signup>> validate = validator.validate(moreThanTenLowerCaseAndSpecialCharacter);
        assertThat(validate.size(), equalTo(0));
    }


    @Test
    void moreThanTenDigitAndSpecialCharacterSuccessTest() {
        Signup moreThanTenDigitAndSpecialCharacter = Signup.builder()
                .password("123456789!")
                .email("test@test.com")
                .nickname("testNickName")
                .birth(LocalDate.now().minusYears(10))
                .sex(Sex.NONE)
                .agree(true)
                .build();

        Set<ConstraintViolation<Signup>> validate = validator.validate(moreThanTenDigitAndSpecialCharacter);
        assertThat(validate.size(), equalTo(0));
    }

    @Test
    void moreThanEightUpperCaseAndLowerCaseAndSpecialCharacterSuccessTest() {
        Signup moreThanTenDigitAndSpecialCharacter = Signup.builder()
                .password("eightqQ!")
                .email("test@test.com")
                .nickname("testNickName")
                .birth(LocalDate.now().minusYears(10))
                .sex(Sex.NONE)
                .agree(true)
                .build();

        Set<ConstraintViolation<Signup>> validate = validator.validate(moreThanTenDigitAndSpecialCharacter);
        assertThat(validate.size(), equalTo(0));
    }


    @Test
    void moreThanEightUpperCaseAndLowerCaseAndDigitSuccessTest() {
        Signup moreThanTenDigitAndSpecialCharacter = Signup.builder()
                .password("eightqQ1")
                .email("test@test.com")
                .nickname("testNickName")
                .birth(LocalDate.now().minusYears(10))
                .sex(Sex.NONE)
                .agree(true)
                .build();

        Set<ConstraintViolation<Signup>> validate = validator.validate(moreThanTenDigitAndSpecialCharacter);
        assertThat(validate.size(), equalTo(0));
    }


    @Test
    void moreThanEightUpperCaseAndDigitAndSpecialCaseSuccessTest() {
        Signup moreThanTenDigitAndSpecialCharacter = Signup.builder()
                .password("EIGHT12!")
                .email("test@test.com")
                .nickname("testNickName")
                .birth(LocalDate.now().minusYears(10))
                .sex(Sex.NONE)
                .agree(true)
                .build();

        Set<ConstraintViolation<Signup>> validate = validator.validate(moreThanTenDigitAndSpecialCharacter);
        assertThat(validate.size(), equalTo(0));
    }


    @Test
    void moreThanEightLowerCaseAndDigitAndSpecialCaseSuccessTest() {
        Signup moreThanTenDigitAndSpecialCharacter = Signup.builder()
                .password("eight12!")
                .email("test@test.com")
                .nickname("testNickName")
                .birth(LocalDate.now().minusYears(10))
                .sex(Sex.NONE)
                .agree(true)
                .build();

        Set<ConstraintViolation<Signup>> validate = validator.validate(moreThanTenDigitAndSpecialCharacter);
        assertThat(validate.size(), equalTo(0));
    }
}


