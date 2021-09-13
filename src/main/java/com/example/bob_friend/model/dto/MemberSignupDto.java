package com.example.bob_friend.model.dto;

import com.example.bob_friend.model.entity.Member;
import com.example.bob_friend.model.entity.Sex;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

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

    private String nickname;
    @NotNull
    private String password;
    private Sex sex;
    private LocalDate birth;
    private boolean agree;

    public Member convertToEntityWithPasswordEncoder(PasswordEncoder passwordEncoder) {
        return Member.builder()
                .email(this.email)
                .username(this.username)
                .nickname(this.nickname)
                .password(passwordEncoder.encode(this.password))
                .birth(this.birth)
                .sex(this.sex)
                .reportCount(0)
                .accumulatedReports(0)
                .agree(this.agree)
                .active(true)
                .build();
    }
}
