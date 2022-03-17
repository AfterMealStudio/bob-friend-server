package com.example.bobfriend.validator;

import com.example.bobfriend.model.dto.member.Signup;
import com.example.bobfriend.model.entity.Sex;
import com.example.bobfriend.testconfig.JpaTestConfig;
import com.example.bobfriend.testconfig.MailTestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


@SpringBootTest
@Import(MailTestConfig.class)
@ActiveProfiles("test")
public class PasswordValidatorTest {
    @Autowired
    Validator validator;
    Signup signup;

    @BeforeEach
    void beforeEach() {
        signup = Signup.builder()
                .email("test@test.com")
                .nickname("testNickName")
                .birth(LocalDate.now().minusYears(10))
                .privacyAgreement(true)
                .serviceAgreement(true)
                .ageLimitAgreement(true)
                .sex(Sex.NONE)
                .build();
    }

    @Test
    void lessThanEightFailTest() {
        String password = "1234";
        signup.setPassword(password);
        Set<ConstraintViolation<Signup>> validate = validator.validate(signup);

        ConstraintViolation<Signup> violation = validate.iterator().next();
        assertThat(violation.getMessage(), equalTo("{custom.validation.constraints.Password.default}"));
    }


    @Test
    void moreThanEightLowerCaseAndDigitFailTest() {
        String eight1234 = "eight1234";
        signup.setPassword(eight1234);
        Set<ConstraintViolation<Signup>> validate = validator.validate(signup);

        ConstraintViolation<Signup> violation = validate.iterator().next();
        assertThat(violation.getMessage(), equalTo("{custom.validation.constraints.Password.default}"));
    }


    @Test
    void moreThanEightLowerCaseAndUpperCaseFailTest() {
        String eight1234 = "eightQWER";
        signup.setPassword(eight1234);
        Set<ConstraintViolation<Signup>> validate = validator.validate(signup);

        ConstraintViolation<Signup> violation = validate.iterator().next();
        assertThat(violation.getMessage(), equalTo("{custom.validation.constraints.Password.default}"));
    }


    @Test
    void moreThanEightLowerCaseAndSpecialCharacterFailTest() {
        String eight1234 = "eight!@#$";
        signup.setPassword(eight1234);
        Set<ConstraintViolation<Signup>> validate = validator.validate(signup);

        ConstraintViolation<Signup> violation = validate.iterator().next();
        assertThat(violation.getMessage(), equalTo("{custom.validation.constraints.Password.default}"));
    }


    @Test
    void moreThanEightUpperCaseAndDigitFailTest() {
        String eight1234 = "EIGHT1234";
        signup.setPassword(eight1234);
        Set<ConstraintViolation<Signup>> validate = validator.validate(signup);

        ConstraintViolation<Signup> violation = validate.iterator().next();
        assertThat(violation.getMessage(), equalTo("{custom.validation.constraints.Password.default}"));
    }


    @Test
    void moreThanEightUpperCaseAndSpecialCharacterFailTest() {
        String eight1234 = "EIGHT!@#$";
        signup.setPassword(eight1234);
        Set<ConstraintViolation<Signup>> validate = validator.validate(signup);

        ConstraintViolation<Signup> violation = validate.iterator().next();
        assertThat(violation.getMessage(), equalTo("{custom.validation.constraints.Password.default}"));
    }


    @Test
    void moreThanTenUpperCaseFailTest() {
        String eight1234 = "MORETHANTEN";
        signup.setPassword(eight1234);
        Set<ConstraintViolation<Signup>> validate = validator.validate(signup);

        ConstraintViolation<Signup> violation = validate.iterator().next();
        assertThat(violation.getMessage(), equalTo("{custom.validation.constraints.Password.default}"));
    }


    @Test
    void moreThanTenLowerCaseFailTest() {
        String eight1234 = "morethanten";
        signup.setPassword(eight1234);
        Set<ConstraintViolation<Signup>> validate = validator.validate(signup);

        ConstraintViolation<Signup> violation = validate.iterator().next();
        assertThat(violation.getMessage(), equalTo("{custom.validation.constraints.Password.default}"));
    }

    @Test
    void moreThanTenDigitFailTest() {
        String eight1234 = "12345678901";
        signup.setPassword(eight1234);
        Set<ConstraintViolation<Signup>> validate = validator.validate(signup);

        ConstraintViolation<Signup> violation = validate.iterator().next();
        assertThat(violation.getMessage(), equalTo("{custom.validation.constraints.Password.default}"));
    }


    @Test
    void moreThanTenSpecialCharacterFailTest() {
        String eight1234 = "!@#$%^&&*!@#";
        signup.setPassword(eight1234);
        Set<ConstraintViolation<Signup>> validate = validator.validate(signup);

        ConstraintViolation<Signup> violation = validate.iterator().next();
        assertThat(violation.getMessage(), equalTo("{custom.validation.constraints.Password.default}"));
    }

    @Test
    void moreThanTenAndUpperCaseAndLowerCaseSuccessTest() {
        String eight1234 = "moreThanTen";
        signup.setPassword(eight1234);

        Set<ConstraintViolation<Signup>> validate = validator.validate(signup);
        assertThat(validate.size(), equalTo(0));
    }


    @Test
    void moreThanTenAndUpperCaseAndDigitSuccessTest() {
        String eight1234 = "MORETHANTEN1";
        signup.setPassword(eight1234);

        Set<ConstraintViolation<Signup>> validate = validator.validate(signup);
        assertThat(validate.size(), equalTo(0));
    }


    @Test
    void moreThanTenUpperCaseAndSpecialCharacterSuccessTest() {
        String eight1234 = "MORETHANTEN!";
        signup.setPassword(eight1234);

        Set<ConstraintViolation<Signup>> validate = validator.validate(signup);
        assertThat(validate.size(), equalTo(0));
    }


    @Test
    void moreThanTenLowerCaseAndDigitSuccessTest() {
        String eight1234 = "morethanten2";
        signup.setPassword(eight1234);

        Set<ConstraintViolation<Signup>> validate = validator.validate(signup);
        assertThat(validate.size(), equalTo(0));
    }


    @Test
    void moreThanTenLowerCaseAndSpecialCharacterSuccessTest() {
        String eight1234 = "morethanten!";
        signup.setPassword(eight1234);

        Set<ConstraintViolation<Signup>> validate = validator.validate(signup);
        assertThat(validate.size(), equalTo(0));
    }


    @Test
    void moreThanTenDigitAndSpecialCharacterSuccessTest() {
        String eight1234 = "123456789!";
        signup.setPassword(eight1234);

        Set<ConstraintViolation<Signup>> validate = validator.validate(signup);
        assertThat(validate.size(), equalTo(0));
    }

    @Test
    void moreThanEightUpperCaseAndLowerCaseAndSpecialCharacterSuccessTest() {
        String eight1234 = "eightqQ!";
        signup.setPassword(eight1234);

        Set<ConstraintViolation<Signup>> validate = validator.validate(signup);
        assertThat(validate.size(), equalTo(0));
    }


    @Test
    void moreThanEightUpperCaseAndLowerCaseAndDigitSuccessTest() {
        String eight1234 = "eightqQ1";
        signup.setPassword(eight1234);

        Set<ConstraintViolation<Signup>> validate = validator.validate(signup);
        assertThat(validate.size(), equalTo(0));
    }


    @Test
    void moreThanEightUpperCaseAndDigitAndSpecialCaseSuccessTest() {
        String eight1234 = "EIGHT12!";
        signup.setPassword(eight1234);

        Set<ConstraintViolation<Signup>> validate = validator.validate(signup);
        assertThat(validate.size(), equalTo(0));
    }


    @Test
    void moreThanEightLowerCaseAndDigitAndSpecialCaseSuccessTest() {
        String eight1234 = "eight12!";
        signup.setPassword(eight1234);

        Set<ConstraintViolation<Signup>> validate = validator.validate(signup);
        assertThat(validate.size(), equalTo(0));
    }
}


