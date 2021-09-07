package com.example.bob_friend.model.dto;

import com.example.bob_friend.model.entity.Member;
import com.example.bob_friend.model.entity.Sex;
import lombok.*;

import java.time.LocalDate;
import java.util.Objects;

@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponseDto {
    private Long id;
    private String email;
    private String username;
    private String nickname;
    private LocalDate birth;
    private Sex sex;
    private Integer reportCount;
    private Integer accumulatedReports;
    private boolean active;

    public MemberResponseDto(Member member) {
        this.id = member.getId();
        this.email = member.getEmail();
        this.username = member.getUsername();
        this.nickname = member.getNickname();
        this.birth = member.getBirth();
        this.sex = member.getSex();
        this.reportCount = member.getReportCount();
        this.accumulatedReports = member.getAccumulatedReports();
        this.active = member.isActive();
    }

    public Member convertToEntity() {
        return Member.builder()
                .id(this.id)
                .email(this.email)
                .username(this.username)
                .nickname(this.nickname)
                .sex(this.sex)
                .birth(this.birth)
                .reportCount(this.reportCount)
                .accumulatedReports(this.accumulatedReports)
                .active(this.active)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemberResponseDto that = (MemberResponseDto) o;
        return id.equals(that.id) && Objects.equals(email, that.email) && Objects.equals(nickname, that.nickname) && Objects.equals(username, that.username) && Objects.equals(birth, that.birth) && sex == that.sex;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, username, nickname, birth, sex);
    }
}
