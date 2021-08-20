package com.example.bob_friend.model.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MemberLoginDto {

    @NotNull
    private String username;
    @NotNull
    private String password;
}
