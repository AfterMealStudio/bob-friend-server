package com.example.bobfriend.model.dto.member;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Getter
@Setter
public class ResetPassword {
    @Email
    private String email;
    
    @NotBlank
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birth;
}
