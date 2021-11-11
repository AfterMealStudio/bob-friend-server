package com.example.bob_friend.controller;

import com.example.bob_friend.jwt.JwtTokenProvider;
import com.example.bob_friend.model.dto.MemberDto;
import com.example.bob_friend.model.dto.TokenDto;
import com.example.bob_friend.model.exception.MemberNotVerifiedException;
import com.example.bob_friend.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/")
public class AuthenticationController {
    private final JwtTokenProvider tokenProvider;
    private final MemberService memberService;


    @PostMapping("/signin")
    public ResponseEntity authorize(@Valid @RequestBody MemberDto.Login loginDto)
            throws AuthenticationException, MemberNotVerifiedException {
        Authentication authentication = memberService.signin(loginDto);

        String jwt = tokenProvider.createToken(authentication);

        return new ResponseEntity(new TokenDto(jwt), HttpStatus.OK);
    }

    @GetMapping("/validate")
    public ResponseEntity validateToken(HttpServletRequest request) {
        String token = tokenProvider.resolveToken(request);
        return ResponseEntity.ok(tokenProvider.validateToken(token));
    }


}
