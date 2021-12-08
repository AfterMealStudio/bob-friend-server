package com.example.bobfriend.service;

import com.example.bobfriend.model.dto.MemberDto;
import com.example.bobfriend.model.entity.Comment;
import com.example.bobfriend.model.entity.Member;
import com.example.bobfriend.model.entity.Recruitment;
import com.example.bobfriend.model.exception.MemberDuplicatedException;
import com.example.bobfriend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;


    @Transactional(readOnly = true)
    public MemberDto.Response getMemberWithAuthorities(String email) {
        Member member = getMember(email);
        return new MemberDto.Response(member);
    }

    @Transactional(readOnly = true)
    public MemberDto.Response getMyMemberWithAuthorities() {
        String currentUsername = getCurrentUsername();

        Member member = getMember(currentUsername);
        return new MemberDto.Response(member);
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
    public void delete(MemberDto.Delete delete) {
        Member currentMember = getCurrentMember();

        if (!passwordEncoder.matches(delete.getPassword(),
                currentMember.getPassword())) {
            throw new BadCredentialsException("password not correct");
        }

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

        memberRepository.delete(currentMember);
    }


    @Transactional
    public MemberDto.Response update(MemberDto.Update update) {
        if (memberRepository.existsMemberByNickname(update.getNickname()))
            throw new MemberDuplicatedException(update.getNickname());
        Member currentMember = getCurrentMember();
        Member incoming = convertToEntity(update);
        return new MemberDto.Response(currentMember.update(incoming));
    }


    public boolean isExistByEmail(String email) {
        return memberRepository.existsMemberByEmail(email);
    }

    public MemberDto.DuplicationCheck checkExistByEmail(String email) {
        return new MemberDto.DuplicationCheck(
                memberRepository.existsMemberByEmail(email));
    }

    public MemberDto.DuplicationCheck checkExistByNickname(String nickname) {
        return new MemberDto.DuplicationCheck(
                memberRepository.existsMemberByNickname(nickname));
    }

    @Transactional
    public MemberDto.Response rateMember(String nickname, MemberDto.Rate rate) {
        Member member = getMemberByNickname(nickname);
        Double score = rate.getScore();
        member.addRating(score);
        return new MemberDto.Response(member);
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


    @Transactional
    Member getCurrentMember() {
        String currentUsername = getCurrentUsername();
        Member currentMember = getMember(currentUsername);
        return currentMember;
    }


    Member convertToEntity(MemberDto.Request request) {
        return Member.builder()
                .email(request.getEmail())
                .nickname(request.getNickname())
                .password((request.getPassword() == null) ? null : passwordEncoder.encode(request.getPassword()))
                .birth(request.getBirth())
                .sex(request.getSex())
                .agree(request.getAgree())
                .build();
    }

}
