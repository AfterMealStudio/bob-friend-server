package com.example.bobfriend.model.dto.member;

import com.example.bobfriend.validator.Password;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePassword {
    @Password
    private String password;
}

