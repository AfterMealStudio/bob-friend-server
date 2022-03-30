package com.example.bobfriend.service;

import com.example.bobfriend.model.entity.Member;
import com.example.bobfriend.model.entity.Recruitment;
import com.example.bobfriend.model.entity.RecruitmentBan;
import com.example.bobfriend.model.exception.MemberNotFoundException;
import com.example.bobfriend.model.exception.RecruitmentAlreadyBannedException;
import com.example.bobfriend.model.exception.RecruitmentNotFoundException;
import com.example.bobfriend.repository.MemberRepository;
import com.example.bobfriend.repository.RecruitmentBanRepository;
import com.example.bobfriend.repository.RecruitmentRepository;
import com.example.bobfriend.util.AuthenticationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecruitmentBanService {

    private final RecruitmentRepository recruitmentRepository;
    private final MemberRepository memberRepository;
    private final RecruitmentBanRepository recruitmentBanRepository;

    public void ban(Long id) {
        Member currentMember = getCurrentMember();
        Recruitment recruitment = getRecruitment(id);

        if (recruitmentBanRepository.existsByMemberAndRecruitment(currentMember,recruitment))
            throw new RecruitmentAlreadyBannedException();

        RecruitmentBan ban = RecruitmentBan.builder()
                .member(currentMember)
                .recruitment(recruitment)
                .build();

        recruitmentBanRepository.save(ban);
    }

    private Member getCurrentMember() {
        String username = AuthenticationUtil.getCurrentUsername();
        return memberRepository.findMemberByEmail(username)
                .orElseThrow(MemberNotFoundException::new);
    }

    private Recruitment getRecruitment(Long id) {
        return recruitmentRepository.findById(id).orElseThrow(
                () -> new RecruitmentNotFoundException(id));
    }
}
