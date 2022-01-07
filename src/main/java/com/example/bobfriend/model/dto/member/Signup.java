package com.example.bobfriend.model.dto.member;

import com.example.bobfriend.model.entity.Member;
import com.example.bobfriend.model.entity.Sex;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

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
public class Signup  extends Request{
    @Email
    private String email;
    @NotBlank
    private String nickname;
    @NotBlank
    private String password;
    @NotNull
    private Sex sex;
    @Past
    private LocalDate birth;
    @NotNull
    private Boolean agree;
}
