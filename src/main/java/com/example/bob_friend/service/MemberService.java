package com.example.bob_friend.service;

import com.example.bob_friend.model.dto.MemberDto;
import com.example.bob_friend.model.entity.Authority;
import com.example.bob_friend.model.entity.Comment;
import com.example.bob_friend.model.entity.Member;
import com.example.bob_friend.model.entity.Recruitment;
import com.example.bob_friend.model.exception.MemberDuplicatedException;
import com.example.bob_friend.model.exception.MemberNotAllowedException;
import com.example.bob_friend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final RecruitmentRepository recruitmentRepository;
    private final ReplyRepository replyRepository;
    private final CommentRepository commentRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final WritingReportRepository reportRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;


    public Authentication signin(MemberDto.Login loginDto) {
        if (!isExistByEmail(loginDto.getEmail())) {
            throw new UsernameNotFoundException(loginDto.getEmail());
        }
        Authentication authentication = getAuthentication(loginDto.getEmail(),
                loginDto.getPassword());

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

    @Transactional
    public MemberDto.Response signup(MemberDto.Signup memberSignupDto) {
        if (memberRepository
                .existsMemberByEmail(memberSignupDto.getEmail())) {
            throw new MemberDuplicatedException(memberSignupDto.getEmail());
        }

        Authority authority = Authority.ROLE_USER;

        Member member = memberSignupDto
                .convertToEntityWithPasswordEncoder(passwordEncoder);
        member.setAuthorities(Collections.singleton(authority));
        Member save = memberRepository.save(member);

        emailService.sendMail(save.getEmail(), save.getEmail(),
                emailService.makeMailText(member));

        return new MemberDto.Response(save);
    }

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

    public void checkPassword(MemberDto.Delete delete) {
        Member currentMember = getCurrentMember();
        getAuthentication(currentMember.getEmail(), delete.getPassword());
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


    private Authentication getAuthentication(String email, String password) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(email, password);
        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);
        return authentication;
    }
}
