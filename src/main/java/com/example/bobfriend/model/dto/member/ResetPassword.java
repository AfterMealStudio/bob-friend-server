package com.example.bobfriend.model.dto.member;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ResetPassword {
    private String email;
    private LocalDate birth;
}
