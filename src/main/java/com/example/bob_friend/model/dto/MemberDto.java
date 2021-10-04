package com.example.bob_friend.model.dto;

import com.example.bob_friend.model.entity.Member;
import com.example.bob_friend.model.entity.Sex;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;

public class MemberDto {
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class Login {

        @NotNull(message = "must be not null")
        private String email;
        @NotNull(message = "must not be null")
        private String password;
    }

    @ToString
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String email;
        private String nickname;
        private LocalDate birth;
        private Sex sex;
        private Integer reportCount;
        private Integer accumulatedReports;
        private boolean agree;
        private boolean active;

        public Response(Member member) {
            this.id = member.getId();
            this.email = member.getEmail();
            this.nickname = member.getNickname();
            this.birth = member.getBirth();
            this.sex = member.getSex();
            this.reportCount = member.getReportCount();
            this.accumulatedReports = member.getAccumulatedReports();
            this.agree = member.isAgree();
            this.active = member.isActive();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Response that = (Response) o;
            return id.equals(that.id) && Objects.equals(email, that.email) && Objects.equals(nickname, that.nickname)  && Objects.equals(birth, that.birth) && sex == that.sex;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, email, nickname, birth, sex);
        }
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class Signup {
        @NotNull(message = "must not be null")
        private String email;

        private String nickname;
        @NotNull(message = "must not be null")
        private String password;
        private Sex sex;
        private LocalDate birth;
        private boolean agree;

        public Member convertToEntityWithPasswordEncoder(PasswordEncoder passwordEncoder) {
            return Member.builder()
                    .email(this.email)
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

    @Data
    public static class DuplicationCheck {
        private boolean duplicated;

        public DuplicationCheck(boolean duplicated) {
            this.duplicated = duplicated;
        }
    }

    @Data
    @AllArgsConstructor
    public static class Preview {
        private String nickname;

        public Preview(Member member) {
            this.nickname = member.getNickname();
        }
    }
}
