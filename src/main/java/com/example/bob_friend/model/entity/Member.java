package com.example.bob_friend.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "member")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private long id;

    @Column(name = "email")
    private String email;

    @Column(name = "username")
    private String username;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "password")
    private String password;

    @Column(name = "sex")
    @Enumerated(EnumType.STRING)
    private Sex sex;

    @Column(name = "birth")
    private LocalDate birth;

    @Column(name = "report_count")
    private Integer reportCount;

    @Column(name = "agree") // 광고성 메일 동의 여부
    private boolean agree;

    @Column(name = "active")
    private boolean active;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void createAt() {
        this.createdAt = LocalDateTime.now();
    }


    @ElementCollection
    @JoinColumn(name = "authority")
    @Enumerated(EnumType.STRING)
    private Set<Authority> authorities;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return id == member.id && active == member.active && Objects.equals(email, member.email) && Objects.equals(username, member.username) && sex == member.sex && Objects.equals(birth, member.birth) && Objects.equals(reportCount, member.reportCount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, username, sex, birth, reportCount, active);
    }
}
