package com.example.bobfriend.model.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "recruitment_ban")
public class RecruitmentBan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Member member;

    @OneToOne
    private Recruitment recruitment;

    @Builder
    public RecruitmentBan(Member member, Recruitment recruitment) {
        this.member = member;
        this.recruitment = recruitment;
    }
}
