package com.example.bob_friend.service;

import com.example.bob_friend.model.dto.MemberDto;
import com.example.bob_friend.model.entity.Authority;
import com.example.bob_friend.model.entity.Comment;
import com.example.bob_friend.model.entity.Member;
import com.example.bob_friend.model.entity.Recruitment;
import com.example.bob_friend.model.exception.MemberDuplicatedException;
import com.example.bob_friend.model.exception.MemberNotAllowedException;
import com.example.bob_friend.repository.MemberRepository;
import com.example.bob_friend.repository.CommentRepository;
import com.example.bob_friend.repository.RecruitmentRepository;
import com.example.bob_friend.repository.ReplyRepository;
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
    private final AuthenticationManagerBuilder authenticationManagerBuilder;


    public Authentication signin(MemberDto.Login loginDto) {
        if (!isExistByEmail(loginDto.getEmail())) {
            throw new UsernameNotFoundException(loginDto.getEmail() + " is not a member");
        }
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);

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
        Member member = memberRepository.findMemberWithAuthoritiesByEmail(email).orElseThrow(() -> {
            throw new UsernameNotFoundException(email);
        });
        return new MemberDto.Response(member);
    }

    @Transactional(readOnly = true)
    public MemberDto.Response getMyMemberWithAuthorities() {
        String currentUsername = getCurrentUsername();

        Member member = memberRepository.findMemberWithAuthoritiesByEmail(currentUsername).orElseThrow(
                () -> {
                    throw new UsernameNotFoundException(currentUsername);
                }
        );
        return new MemberDto.Response(member);
    }

    public String getCurrentUsername() {
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
    public Member getCurrentMember() {
        String currentUsername = getCurrentUsername();
        Member currentMember = memberRepository.getMemberByEmail(currentUsername);
        return currentMember;
    }

    @Transactional
    public MemberDto.Response reportMember(String username) {
        Member member = memberRepository.findMemberByEmail(username)
                .orElseThrow(() -> {
                            throw new UsernameNotFoundException("user not found");
                        }
                );
        member.increaseReportCount();
        return new MemberDto.Response((member));
    }

    @Transactional
    public void checkMemberWithCode(String email, String code) {
        Member member = memberRepository.getMemberByEmail(email);
        if (Integer.parseInt(code) == (member.hashCode())) {
            member.setVerified(true);
        }
    }

    @Transactional
    public void deleteById(Long memberId) {
        Member currentMember = getCurrentMember();
        if (currentMember.getId() != memberId)
            throw new MemberNotAllowedException(currentMember.getNickname());

        for (Recruitment recruitment :
                recruitmentRepository.findAllByAuthor(currentMember)) {
            recruitment.setAuthor(null);
        }

        for (Comment comment:
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


}
