package com.example.bobfriend.service;

import com.example.bobfriend.model.dto.member.Signup;
import com.example.bobfriend.model.entity.MemberAgreement;
import com.example.bobfriend.repository.MemberAgreementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberAgreementService {

    private final MemberAgreementRepository memberAgreementRepository;

    @Transactional
    public void save(Signup signupDto) {
        String email = signupDto.getEmail();
        Boolean privacyAgreement = signupDto.getPrivacyAgreement();
        Boolean serviceAgreement = signupDto.getServiceAgreement();

        MemberAgreement memberAgreement = MemberAgreement.builder()
                .email(email)
                .serviceAgreement(serviceAgreement)
                .privacyAgreement(privacyAgreement)
                .build();

        memberAgreementRepository.save(memberAgreement);
    }
}
