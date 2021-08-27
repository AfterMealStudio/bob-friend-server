package com.example.bob_friend.model.dto;

import com.example.bob_friend.model.entity.Sex;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MemberSignupDto {
    @NotNull
    private String email;
    @NotNull
    private String username;
    @NotNull
    private String password;
    private Sex sex;
    private LocalDate birth;

}
