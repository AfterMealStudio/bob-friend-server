package com.example.bob_friend.service;

import com.example.bob_friend.model.entity.Member;
import com.example.bob_friend.model.exception.MemberNotActivatedException;
import com.example.bob_friend.repository.MemberRepository;
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
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        return memberRepository.findMemberWithAuthoritiesByUsername(username)
                .map(member -> createUser(username, member))
                .orElseThrow(() -> new UsernameNotFoundException(username + " not found"));
    }

    private User createUser(String username, Member member) {
        if (!member.isActive()) {
            throw new MemberNotActivatedException(username);
        }
        List<GrantedAuthority> grantedAuthorityList = member.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthorityName()))
                .collect(Collectors.toList());
        return new User(member.getUsername(),
                member.getPassword(),
                grantedAuthorityList);
    }
}
