package com.example.bobfriend.model.dto.member;

import com.example.bobfriend.validator.PasswordCorrect;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Delete {
    @PasswordCorrect
    private String password;
}
