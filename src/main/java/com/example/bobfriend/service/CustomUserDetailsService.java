package com.example.bobfriend.service;

import com.example.bobfriend.model.entity.Member;
import com.example.bobfriend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String username) {
        return memberRepository.findMemberWithAuthoritiesByEmail(username)
                .map(member -> {
                    member.validateIfSuspension();
                    return createUser(member);
                })
                .orElseThrow(() -> new UsernameNotFoundException(username + " not found"));
    }

    private User createUser(Member member) {
        List<GrantedAuthority> grantedAuthorityList = member.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.name()))
                .collect(Collectors.toList());
        return new User(member.getEmail(),
                member.getPassword(),
                member.isVerified(),
                true,
                true,
                member.isActive(),
                grantedAuthorityList);
    }
}
