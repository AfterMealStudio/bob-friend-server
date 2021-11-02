package com.example.bob_friend.model.entity;

import com.example.bob_friend.model.Constant;
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

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "password")
    private String password;

    @Column(name = "sex")
    @Convert(converter = SexConverter.class)
    private Sex sex;

    @Column(name = "birth")
    private LocalDate birth;

    @Column(name = "report_count")
    private Integer reportCount;

    @Column(name = "accumulated_reports")
    private Integer accumulatedReports;

    @Column(name = "agree") // 광고성 메일 동의 여부
    private boolean agree;

    @Column(name = "active")
    private boolean active;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "report_start")
    private LocalDate reportStart;

    @Column(name = "report_end")
    private LocalDate reportEnd;

    @Column(name = "verified") // 이메일 인증 여부
    private boolean verified;

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
        return id == member.id && Objects.equals(email, member.email) && sex == member.sex && Objects.equals(birth, member.birth);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, sex, birth);
    }

    public void setAuthorities(Set<Authority> authorities) {
        this.authorities = authorities;
    }

    public void setActive() {
        this.active = true;
        this.reportStart = null;
        this.reportEnd = null;
    }

    public void setReportCount(Integer reportCount) {
        this.reportCount = reportCount;
    }

    public void increaseReportCount() {
        this.reportCount += 1;
        if (this.reportCount > Constant.REPORT_LIMIT) {
            this.reportCount = 0;
            this.accumulatedReports += 1;
            this.active = false;
            this.reportStart = LocalDate.now();
            this.reportEnd = calculateReportEnd();
        }
    }

    private LocalDate calculateReportEnd() {
        return LocalDate.now()
                .plusDays(Constant.REPORT_SUSPENSION_PERIOD *
                                this.accumulatedReports);
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }
}

