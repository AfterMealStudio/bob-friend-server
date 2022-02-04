package com.example.bobfriend.repository;

import com.example.bobfriend.model.entity.MemberAgreement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberAgreementRepository extends JpaRepository<MemberAgreement, String> {
}
