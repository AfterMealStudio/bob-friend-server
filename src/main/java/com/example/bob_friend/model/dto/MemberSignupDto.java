package com.example.bob_friend.model.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberSignupDto {
    @NotNull
    private String email;
    @NotNull
    private String username;
    @NotNull
    private String password;
}
