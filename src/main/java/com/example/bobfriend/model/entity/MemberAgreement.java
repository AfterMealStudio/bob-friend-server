package com.example.bobfriend.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "member_agreement")
@Entity
public class MemberAgreement {

    @Id
    @Column(name = "email")
    private String email;

    @Column(name = "privacy_agreement")
    private Boolean privacyAgreement;
    @Column(name = "service_agreement")
    private Boolean serviceAgreement;
}