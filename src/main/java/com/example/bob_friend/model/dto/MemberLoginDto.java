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

    @NotNull(message = "must be not null")
    private String email;
    @NotNull(message = "must not be null")
    private String password;
}
