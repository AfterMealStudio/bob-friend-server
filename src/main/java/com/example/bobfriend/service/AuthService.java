package com.example.bobfriend.service;

import com.example.bobfriend.jwt.JwtTokenProvider;
import com.example.bobfriend.model.dto.MemberDto;
import com.example.bobfriend.model.dto.token.Token;
import com.example.bobfriend.model.entity.Authority;
import com.example.bobfriend.model.entity.Member;
import com.example.bobfriend.model.entity.RefreshToken;
import com.example.bobfriend.model.exception.JwtInvalidException;
import com.example.bobfriend.model.exception.MemberDuplicatedException;
import com.example.bobfriend.model.exception.RefreshTokenNotFoundException;
import com.example.bobfriend.model.exception.RefreshTokenNotMatchException;
import com.example.bobfriend.repository.MemberRepository;
import com.example.bobfriend.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;


    @Transactional
    public MemberDto.Response signup(MemberDto.Signup memberSignupDto) {
        if (memberRepository
                .existsMemberByEmail(memberSignupDto.getEmail())) {
            throw new MemberDuplicatedException(memberSignupDto.getEmail());
        }

        Authority authority = Authority.ROLE_USER;

        Member member = memberService.convertToEntity(memberSignupDto);
        member.setAuthorities(Collections.singleton(authority));
        Member save = memberRepository.save(member);

        emailService.sendMail(save.getEmail(), save.getEmail(),
                emailService.makeMailText(member));

        return new MemberDto.Response(save);
    }

    @Transactional
    public Token signin(MemberDto.Login loginDto) {
        if (!memberRepository.existsMemberByEmail(loginDto.getEmail())) {
            throw new UsernameNotFoundException(loginDto.getEmail());
        }
        Authentication authentication = getAuthentication(loginDto.getEmail(),
                loginDto.getPassword());

        Token tokenDto = tokenProvider.createToken(authentication);

        RefreshToken refreshToken = RefreshToken.builder()
                .id(authentication.getName())
                .token(tokenDto.getRefreshToken())
                .build();

        refreshTokenRepository.save(refreshToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return tokenDto;
    }

    @Transactional
    public Token issueToken(Token tokenDto) {
        Authentication authentication = tokenProvider.getAuthentication(tokenDto.getAccessToken());

        RefreshToken refreshToken = refreshTokenRepository.findById(authentication.getName())
                .orElseThrow(RefreshTokenNotFoundException::new);

        if (!refreshToken.getToken().equals(tokenDto.getRefreshToken())) {
            throw new RefreshTokenNotMatchException();
        }

        if (!tokenProvider.validateRefreshToken(refreshToken.getToken())) {
            throw new JwtInvalidException();
        }

        Token token = tokenProvider.createToken(authentication);

        refreshTokenRepository.save(refreshToken.updateToken(tokenDto.getRefreshToken()));

        return token;
    }


    private Authentication getAuthentication(String email, String password) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(email, password);
        return authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);
    }
}
