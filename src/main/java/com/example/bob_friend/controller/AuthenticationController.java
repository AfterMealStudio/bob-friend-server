package com.example.bob_friend.controller;

import com.example.bob_friend.jwt.JwtTokenProvider;
import com.example.bob_friend.model.dto.MemberLoginDto;
import com.example.bob_friend.model.dto.TokenDto;
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

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/")
public class AuthenticationController {
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MemberService memberService;
    @Value("${jwt.header}")
    private String AUTHENTICATION_HEADER;

    @PostMapping("/signin")
    public ResponseEntity authorize(@Valid @RequestBody MemberLoginDto loginDto) throws AuthenticationException {
        if (!memberService.isExistById(loginDto.getUsername())) {
            throw new UsernameNotFoundException(loginDto.getUsername() + " is not a member");
        }
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.createToken(authentication);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(AUTHENTICATION_HEADER, jwt);
        return new ResponseEntity(new TokenDto(jwt), httpHeaders, HttpStatus.OK);
    }

    @ExceptionHandler(value = UsernameNotFoundException.class)
    public ResponseEntity handleUsernameNotFound(UsernameNotFoundException e) {
        return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(value = AuthenticationException.class)
    public ResponseEntity handleAuthenticationException(AuthenticationException e) {
        return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
