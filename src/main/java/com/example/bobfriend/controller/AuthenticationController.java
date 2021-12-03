package com.example.bobfriend.controller;

import com.example.bobfriend.jwt.JwtTokenProvider;
import com.example.bobfriend.model.dto.MemberDto;
import com.example.bobfriend.model.dto.TokenDto;
import com.example.bobfriend.model.exception.MemberDuplicatedException;
import com.example.bobfriend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/")
public class AuthenticationController {
    private final JwtTokenProvider tokenProvider;
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity signup(@Valid @RequestBody MemberDto.Signup memberSignupDto) throws MemberDuplicatedException {
        return ResponseEntity.ok(authService.signup(memberSignupDto));
    }

    @PostMapping("/signin")
    public ResponseEntity signin(@Valid @RequestBody MemberDto.Login loginDto)
            throws AuthenticationException {
        TokenDto tokenDto = authService.signin(loginDto);
        return new ResponseEntity(tokenDto, HttpStatus.OK);
    }

    @PostMapping("/reissue")
    public ResponseEntity reissueToken(@RequestBody TokenDto token) {
        return ResponseEntity.ok(authService.reissue(token));
    }

    @GetMapping("/validate")
    public ResponseEntity validateToken(HttpServletRequest request) {
        String token = tokenProvider.resolveToken(request);
        return ResponseEntity.ok(tokenProvider.validateAccessToken(token));
    }


}
