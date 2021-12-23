package com.example.bobfriend.model.dto.member;

import com.example.bobfriend.model.entity.Member;
import com.example.bobfriend.model.entity.Sex;
import lombok.*;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Response {
    private Long id;
    private String email;
    private String nickname;
    private LocalDate birth;
    private Sex sex;
    private Integer reportCount;
    private Integer accumulatedReports;
    private Double rating;
    private Boolean agree;
    private Boolean active;

    public Response(Member member) {
        this.id = member.getId();
        this.email = member.getEmail();
        this.nickname = member.getNickname();
        this.birth = member.getBirth();
        this.sex = member.getSex();
        this.reportCount = member.getReportCount();
        this.accumulatedReports = member.getAccumulatedReports();
        this.rating = member.getRating();
        this.agree = member.isAgree();
        this.active = member.isActive();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Response that = (Response) o;
        return id.equals(that.id) && Objects.equals(email, that.email) && Objects.equals(nickname, that.nickname) && Objects.equals(birth, that.birth) && sex == that.sex;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, nickname, birth, sex);
    }
}
