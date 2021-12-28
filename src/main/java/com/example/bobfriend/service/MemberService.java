package com.example.bobfriend.service;

import com.example.bobfriend.model.dto.member.DuplicationCheck;
import com.example.bobfriend.model.dto.member.Response;
import com.example.bobfriend.model.dto.member.Score;
import com.example.bobfriend.model.entity.Comment;
import com.example.bobfriend.model.entity.Member;
import com.example.bobfriend.model.entity.Recruitment;
import com.example.bobfriend.model.exception.MemberNotAllowedException;
import com.example.bobfriend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final RecruitmentRepository recruitmentRepository;
    private final ReplyRepository replyRepository;
    private final CommentRepository commentRepository;
    private final WritingReportRepository reportRepository;


    @Transactional(readOnly = true)
    public Response getMemberWithAuthorities(String email) {
        Member member = getMember(email);
        return new Response(member);
    }

    @Transactional(readOnly = true)
    public Response getMyMemberWithAuthorities() {
        String currentUsername = getCurrentUsername();

        Member member = getMember(currentUsername);
        return new Response(member);
    }

    @Transactional
    public Member getCurrentMember() {
        String currentUsername = getCurrentUsername();
        Member currentMember = getMember(currentUsername);
        return currentMember;
    }


    @Transactional
    public void checkMemberWithCode(String email, String code) {
        Member member = memberRepository.findMemberByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));
        if (Integer.parseInt(code) == (member.hashCode())) {
            member.emailVerify();
        }
    }


    @Transactional
    public void deleteById(Long memberId) {
        Member currentMember = getCurrentMember();
        if (currentMember.getId() != memberId)
            throw new MemberNotAllowedException(currentMember.getNickname());

        reportRepository.deleteAllByMember(currentMember);

        for (Recruitment recruitment :
                recruitmentRepository.findAllByAuthor(currentMember)) {
            recruitment.setAuthor(null);
        }

        for (Comment comment :
                commentRepository.findAllByAuthor(currentMember)) {
            comment.clear();
        }

//        for (Reply reply:
//        replyRepository.findAllByAuthor(currentMember)) {
//            replyRepository.delete(reply);
//        }
        replyRepository.deleteAllByAuthor(currentMember);

        memberRepository.deleteById(memberId);
    }

    public boolean isExistByEmail(String email) {
        return memberRepository.existsMemberByEmail(email);
    }

    public DuplicationCheck checkExistByEmail(String email) {
        return new DuplicationCheck(
                memberRepository.existsMemberByEmail(email));
    }

    public DuplicationCheck checkExistByNickname(String nickname) {
        return new DuplicationCheck(
                memberRepository.existsMemberByNickname(nickname));
    }

    @Transactional
    public Response rateMember(String nickname, Score rate) {
        Member member = getMemberByNickname(nickname);
        Double score = rate.getScore();
        member.addRating(score);
        return new Response(member);
    }

    private Member getMemberByNickname(String nickname) {
        return memberRepository.findMemberByNickname(nickname)
                .orElseThrow(() -> {
                            throw new UsernameNotFoundException(
                                    nickname + " not found");
                        }
                );
    }

    private Member getMember(String currentUsername) {
        return memberRepository.findMemberWithAuthoritiesByEmail(currentUsername)
                .orElseThrow(() -> {
                            throw new UsernameNotFoundException(currentUsername);
                        }
                );
    }

    private String getCurrentUsername() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new AuthenticationException("Authentication not found") {
                @Override
                public String getMessage() {
                    return super.getMessage();
                }
            };
        }

        String username = null;
        if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails user = (UserDetails) authentication.getPrincipal();
            username = user.getUsername();
        } else if (authentication.getPrincipal() instanceof String) {
            username = (String) authentication.getPrincipal();
        }

        return username;
    }


}
