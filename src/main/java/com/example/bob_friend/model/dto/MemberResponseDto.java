package com.example.bob_friend.model.dto;

import com.example.bob_friend.model.entity.Member;
import com.example.bob_friend.model.entity.Sex;
import lombok.*;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponseDto {
    private Long id;
    private String email;
    private String username;
    private LocalDate birth;
    private Sex sex;
    private Integer reportCount;
    private boolean active;

    public MemberResponseDto(Member member) {
        this.id = member.getId();
        this.email = member.getEmail();
        this.username = member.getUsername();
        this.birth = member.getBirth();
        this.sex = member.getSex();
        this.reportCount = member.getReportCount();
        this.active = member.isActive();
    }

    public Member convertToEntity() {
        return Member.builder()
                .id(this.id)
                .email(this.email)
                .username(this.username)
                .sex(this.sex)
                .birth(this.birth)
                .reportCount(this.reportCount)
                .active(this.active)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemberResponseDto that = (MemberResponseDto) o;
        return active == that.active && id.equals(that.id) && Objects.equals(email, that.email) && Objects.equals(username, that.username) && Objects.equals(birth, that.birth) && sex == that.sex && Objects.equals(reportCount, that.reportCount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, username, birth, sex, reportCount, active);
    }
}
