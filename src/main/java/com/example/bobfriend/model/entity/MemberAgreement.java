package com.example.bobfriend.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "member_agreement")
@Entity
public class MemberAgreement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Member member;

    @Column(name = "privacy_agreement")
    private Boolean privacyAgreement;
    @Column(name = "service_agreement")
    private Boolean serviceAgreement;
}