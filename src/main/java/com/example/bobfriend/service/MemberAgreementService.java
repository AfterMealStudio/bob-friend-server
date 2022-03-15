package com.example.bobfriend.service;

import com.example.bobfriend.model.dto.member.Signup;
import com.example.bobfriend.model.entity.Member;
import com.example.bobfriend.model.entity.MemberAgreement;
import com.example.bobfriend.model.exception.MemberNotFoundException;
import com.example.bobfriend.repository.MemberAgreementRepository;
import com.example.bobfriend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberAgreementService {

    private final MemberAgreementRepository memberAgreementRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void save(Signup signupDto) {
        String email = signupDto.getEmail();
        Member member = memberRepository.findMemberByEmail(email)
                .orElseThrow(() -> new MemberNotFoundException());
        Boolean privacyAgreement = signupDto.getPrivacyAgreement();
        Boolean serviceAgreement = signupDto.getServiceAgreement();

        MemberAgreement memberAgreement = MemberAgreement.builder()
                .member(member)
                .serviceAgreement(serviceAgreement)
                .privacyAgreement(privacyAgreement)
                .build();

        memberAgreementRepository.save(memberAgreement);
    }
}
