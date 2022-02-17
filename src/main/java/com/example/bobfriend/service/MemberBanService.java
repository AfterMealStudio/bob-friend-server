package com.example.bobfriend.service;

import com.example.bobfriend.model.entity.Member;
import com.example.bobfriend.model.entity.MemberBan;
import com.example.bobfriend.model.exception.MemberNotFoundException;
import com.example.bobfriend.repository.MemberBanRepository;
import com.example.bobfriend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberBanService {

    private final MemberRepository memberRepository;
    private final MemberBanRepository memberBanRepository;
    private final MemberService memberService;

    @Transactional
    public void ban(String nickname) {
        Member currentMember = memberService.getCurrentMember();
        Member member = memberRepository.findMemberByNickname(nickname)
                .orElseThrow(() -> new MemberNotFoundException());

        MemberBan memberBan = MemberBan.builder()
                .member(currentMember)
                .bannedMember(member)
                .build();

        memberBanRepository.save(memberBan);
    }


    @Transactional
    public void cancel(String nickname) {
        Member currentMember = memberService.getCurrentMember();
        Member member = memberRepository.findMemberByNickname(nickname)
                .orElseThrow(() -> new MemberNotFoundException());

        MemberBan memberBan = memberBanRepository.findByMemberAndAndBannedMember(currentMember, member).orElseThrow(
                () -> new MemberNotFoundException());

        memberBanRepository.delete(memberBan);
    }
}
