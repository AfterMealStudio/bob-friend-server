package com.example.bobfriend.model.dto.member;

import com.example.bobfriend.model.entity.Sex;
import com.example.bobfriend.validator.Password;
import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Signup extends Request {
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    protected String email;
    @NotBlank(message = "닉네임은 빈 칸일 수 없습니다.")
    protected String nickname;
    @Password
    protected String password;
    @NotNull(message = "성별을 체크해야 합니다.")
    protected Sex sex;
    @Past(message = "생년월일은 현재보다 이전 시점이어야 합니다.")
    protected LocalDate birth;

    @NotNull(message = "서비스 이용약관을 체크해야 합니다.")
    @AssertTrue(message = "서비스 이용약관에 동의하지 않을 경우 서비스를 이용할 수 없습니다.")
    private Boolean serviceAgreement;
    @NotNull(message = "개인정보 취급방침을 체크해야 합니다.")
    @AssertTrue(message = "개인정보 취급방침에 동의하지 않을 경우 서비스를 이용할 수 없습니다.")
    private Boolean privacyAgreement;
    @NotNull(message = "14세 이상 여부를 체크해야 합니다.")
    @AssertTrue(message = "14세 미만은 서비스를 이용할 수 없습니다.")
    private Boolean ageLimitAgreement;
}
