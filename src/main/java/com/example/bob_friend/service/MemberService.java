package com.example.bob_friend.service;

import com.example.bob_friend.model.dto.MemberDto;
import com.example.bob_friend.model.entity.Authority;
import com.example.bob_friend.model.entity.Member;
import com.example.bob_friend.model.exception.MemberDuplicatedException;
import com.example.bob_friend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Member signup(MemberDto memberDto) {
        if (memberRepository
                .findMemberWithAuthoritiesByUsername(
                        memberDto.getUsername()
                )
                .orElse(null) != null
        ) throw new MemberDuplicatedException(memberDto.getUsername() + " is already exist");

        Authority authority = Authority.builder()
                .authorityName("ROLE_USER")
                .build();
        Member member = Member.builder()
                .email(memberDto.getEmail())
                .username(memberDto.getUsername())
                .password(passwordEncoder.encode(memberDto.getPassword()))
                .authorities(Collections.singleton(authority))
                .activated(true)
                .build();
        return memberRepository.save(member);
    }

    @Transactional(readOnly = true)
    public Optional<Member> getMemberWithAuthorities(String username) {
        return memberRepository.findMemberWithAuthoritiesByUsername(username);
    }

    @Transactional(readOnly = true)
    public Optional<Member> getMyMemberWithAuthorities() {
        return getCurrentUsername().flatMap(memberRepository::findMemberWithAuthoritiesByUsername);
    }

    public Optional<String> getCurrentUsername() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) return Optional.empty();

        String username = null;
        if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails user = (UserDetails) authentication.getPrincipal();
            username = user.getUsername();
        } else if (authentication.getPrincipal() instanceof String) {
            username = (String) authentication.getPrincipal();
        }

        return Optional.ofNullable(username);
    }
}
