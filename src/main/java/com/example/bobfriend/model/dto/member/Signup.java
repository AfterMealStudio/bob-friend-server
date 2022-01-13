package com.example.bobfriend.model.dto.member;

import com.example.bobfriend.model.entity.Sex;
import com.example.bobfriend.validator.Password;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Signup extends Request {
    @Email
    protected String email;
    @NotBlank
    protected String nickname;
    @Password
    protected String password;
    @NotNull
    protected Sex sex;
    @Past
    protected LocalDate birth;
    @NotNull
    protected Boolean agree;
}
