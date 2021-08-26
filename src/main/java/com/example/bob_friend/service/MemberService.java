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

import java.util.Collections;

@RequiredArgsConstructor
@Service
public class MemberService {
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

        Authority authority = Authority.builder()
                .authorityName("ROLE_USER")
                .build();
        Member member = Member.builder()
                .email(memberSignupDto.getEmail())
                .username(memberSignupDto.getUsername())
                .password(passwordEncoder.encode(memberSignupDto.getPassword()))
                .authorities(Collections.singleton(authority))
                .activated(true)
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
    public void deleteByName(String username) {
        memberRepository.deleteByUsername(username);
    }

    @Transactional
    public void deleteById(Long memberId) {
        memberRepository.deleteById(memberId);
    }

    public boolean isExistById(String username) {
        return memberRepository.existsMemberByUsername(username);
    }
}
