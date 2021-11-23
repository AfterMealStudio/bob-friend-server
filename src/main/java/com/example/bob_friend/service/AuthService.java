package com.example.bob_friend.service;

import com.example.bob_friend.jwt.JwtTokenProvider;
import com.example.bob_friend.model.dto.MemberDto;
import com.example.bob_friend.model.dto.TokenDto;
import com.example.bob_friend.model.entity.Authority;
import com.example.bob_friend.model.entity.Member;
import com.example.bob_friend.model.entity.RefreshToken;
import com.example.bob_friend.model.exception.JwtInvalidException;
import com.example.bob_friend.model.exception.MemberDuplicatedException;
import com.example.bob_friend.model.exception.RefreshTokenNotFoundException;
import com.example.bob_friend.model.exception.RefreshTokenNotMatchException;
import com.example.bob_friend.repository.MemberRepository;
import com.example.bob_friend.repository.RefreshTokenRepository;
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

        Member member = memberSignupDto
                .convertToEntityWithPasswordEncoder(passwordEncoder);
        member.setAuthorities(Collections.singleton(authority));
        Member save = memberRepository.save(member);

        emailService.sendMail(save.getEmail(), save.getEmail(),
                emailService.makeMailText(member));

        return new MemberDto.Response(save);
    }

    @Transactional
    public TokenDto signin(MemberDto.Login loginDto) {
        if (!memberRepository.existsMemberByEmail(loginDto.getEmail())) {
            throw new UsernameNotFoundException(loginDto.getEmail());
        }
        Authentication authentication = getAuthentication(loginDto.getEmail(),
                loginDto.getPassword());
        TokenDto tokenDto = tokenProvider.createToken(authentication);

        RefreshToken refreshToken = RefreshToken.builder()
                .id(authentication.getName())
                .token(tokenDto.getRefreshToken())
                .build();
        refreshTokenRepository.save(refreshToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return tokenDto;
    }

    @Transactional
    public TokenDto reissue(TokenDto tokenDto) {
        if (!tokenProvider.validateToken(tokenDto.getRefreshToken())) {
            throw new JwtInvalidException();
        }

        Authentication authentication = tokenProvider.getAuthentication(tokenDto.getAccessToken());

        RefreshToken refreshToken = refreshTokenRepository.findById(authentication.getName())
                .orElseThrow(RefreshTokenNotFoundException::new);

        if (!refreshToken.getToken().equals(tokenDto.getRefreshToken())) {
            throw new RefreshTokenNotMatchException();
        }
        if (!tokenProvider.validateToken(refreshToken.getToken())) {
            throw new JwtInvalidException();
        }

        TokenDto token = tokenProvider.createToken(authentication);

        refreshTokenRepository.save(refreshToken.updateToken(tokenDto.getRefreshToken()));

        return token;
    }

    public void checkPassword(MemberDto.Delete delete) {
        Member currentMember = memberService.getCurrentMember();
        getAuthentication(currentMember.getEmail(), delete.getPassword());
    }

    private Authentication getAuthentication(String email, String password) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(email, password);
        return authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);
    }
}
