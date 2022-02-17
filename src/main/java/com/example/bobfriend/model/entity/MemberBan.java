package com.example.bobfriend.model.entity;

import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@EqualsAndHashCode(of = {"member", "bannedMember"})
@Table(name = "member_ban")
@Entity
public class MemberBan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Member member;

    @OneToOne
    private Member bannedMember;

}
