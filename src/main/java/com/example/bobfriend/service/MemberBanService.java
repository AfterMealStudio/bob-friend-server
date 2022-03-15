package com.example.bobfriend.service;

import com.example.bobfriend.model.entity.Member;
import com.example.bobfriend.model.entity.MemberBan;
import com.example.bobfriend.model.exception.MemberAlreadyBannedException;
import com.example.bobfriend.model.exception.MemberNotBannedException;
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
        Member member = getMemberOrThrowNotFound(nickname);

        memberBanRepository.findByMemberAndBannedMember(currentMember, member)
                .ifPresent((memberBan) -> {
                            throw new MemberAlreadyBannedException();
                        }
                );

        MemberBan memberBan = MemberBan.builder()
                .member(currentMember)
                .bannedMember(member)
                .build();

        memberBanRepository.save(memberBan);
    }


    @Transactional
    public void cancel(String nickname) {
        Member currentMember = memberService.getCurrentMember();
        Member member = getMemberOrThrowNotFound(nickname);

        MemberBan memberBan = memberBanRepository.findByMemberAndBannedMember(currentMember, member)
                .orElseThrow(() -> new MemberNotBannedException());

        memberBanRepository.delete(memberBan);
    }

    private Member getMemberOrThrowNotFound(String nickname) {
        return memberRepository.findMemberByNickname(nickname)
                .orElseThrow(() -> new MemberNotFoundException());
    }
}
