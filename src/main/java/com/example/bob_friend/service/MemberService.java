package com.example.bob_friend.service;

import com.example.bob_friend.model.dto.MemberSignupDto;
import com.example.bob_friend.model.dto.MemberResponseDto;
import com.example.bob_friend.model.entity.Authority;
import com.example.bob_friend.model.entity.Member;
import com.example.bob_friend.model.exception.MemberDuplicatedException;
import com.example.bob_friend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;

@RequiredArgsConstructor
@Service
public class MemberService {
    private final int REPORT_DAY = 3;

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public MemberResponseDto signup(MemberSignupDto memberSignupDto) {
        if (memberRepository
                .findMemberWithAuthoritiesByUsername(
                        memberSignupDto.getUsername()
                )
                .orElse(null) != null
        ) throw new MemberDuplicatedException(memberSignupDto.getUsername());

        Authority authority = Authority.ROLE_USER;

        Member member = Member.builder()
                .email(memberSignupDto.getEmail())
                .username(memberSignupDto.getUsername())
                .nickname(memberSignupDto.getNickname())
                .password(passwordEncoder.encode(memberSignupDto.getPassword()))
                .birth(memberSignupDto.getBirth())
                .sex(memberSignupDto.getSex())
                .reportCount(0)
                .authorities(Collections.singleton(authority))
                .agree(memberSignupDto.isAgree())
                .active(true)
                .build();
        return new MemberResponseDto(memberRepository.save(member));
    }

    @Transactional(readOnly = true)
    public MemberResponseDto getMemberWithAuthorities(String username) {
        Member member = memberRepository.findMemberWithAuthoritiesByUsername(username).orElseThrow(() -> {
            throw new UsernameNotFoundException(username);
        });
        return new MemberResponseDto(member);
    }

    @Transactional(readOnly = true)
    public MemberResponseDto getMyMemberWithAuthorities() {
        String currentUsername = getCurrentUsername();

        Member member = memberRepository.findMemberWithAuthoritiesByUsername(currentUsername).orElseThrow(
                () -> {
                    throw new UsernameNotFoundException(currentUsername);
                }
        );
        return new MemberResponseDto(member);
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
        Member currentMember = memberRepository.findMemberByUsername(currentUsername).orElseThrow(
                () -> {
                    throw new UsernameNotFoundException("user not found");
                }
        );
        return currentMember;
    }

    @Transactional
    public void setMemberActive(Member member) {
        member.setActive(true);
        member.setReportStart(null);
        member.setReportEnd(null);
        member.setReportCount(0);
        saveMember(member);
    }

    @Transactional
    public MemberResponseDto reportMember(String username) {
        Member member = memberRepository.findMemberByUsername(username).orElseThrow(
                () -> {
                    throw new UsernameNotFoundException("user not found");
                }
        );
        member.setReportCount(member.getReportCount()+1);
        if (member.getReportCount() > 3) {
            member.setActive(false);
            member.setReportStart(LocalDate.now());
            member.setReportEnd(LocalDate.now().plusDays(REPORT_DAY));
        }
        return new MemberResponseDto((member));
    }

    @Transactional
    public void saveMember(Member member) {
        memberRepository.save(member);
    }

    @Transactional
    public void deleteByName(String username) {
        memberRepository.deleteByUsername(username);
    }

    @Transactional
    public void deleteById(Long memberId) {
        memberRepository.deleteById(memberId);
    }

    public boolean isExistByUsername(String username) {
        return memberRepository.existsMemberByUsername(username);
    }

    public boolean isExistByEmail(String email) {
        return memberRepository.existsMemberByEmail(email);
    }

    public boolean isExistByNickname(String nickname) {
        return memberRepository.existsMemberByNickname(nickname);
    }
}
