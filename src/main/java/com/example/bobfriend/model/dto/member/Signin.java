package com.example.bobfriend.model.dto.member;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Signin {
    @NotNull
    private String email;
    @NotBlank
    private String password;
}
