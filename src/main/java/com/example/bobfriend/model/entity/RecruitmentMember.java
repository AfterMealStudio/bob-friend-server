package com.example.bobfriend.model.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "recruitment_member")
@EqualsAndHashCode(of = {"recruitment", "member"})
public class RecruitmentMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "recruitment_id")
    private Recruitment recruitment;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    public RecruitmentMember(Recruitment recruitment, Member member) {
        this.recruitment = recruitment;
        this.member = member;
    }
}
