package com.example.bobfriend.util;

import com.example.bobfriend.model.entity.Member;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

public class TestAuthenticationUtil {

    public static void setAuthentication(Member member) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        member.getEmail(),
                        member.getPassword(),
                        Collections.singleton(
                                new SimpleGrantedAuthority("ROLE_USER"))
                ));
    }
}
