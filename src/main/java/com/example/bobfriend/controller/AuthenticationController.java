package com.example.bobfriend.controller;

import com.example.bobfriend.jwt.JwtTokenProvider;
import com.example.bobfriend.model.dto.member.Signin;
import com.example.bobfriend.model.dto.member.Signup;
import com.example.bobfriend.model.dto.token.*;
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
    public ResponseEntity signup(@Valid @RequestBody Signup signupDto) throws MemberDuplicatedException {
        return ResponseEntity.ok(authService.signup(signupDto));
    }


    @PostMapping("/signin")
    public ResponseEntity signin(@Valid @RequestBody Signin signinDto)
            throws AuthenticationException {
        Token tokenDto = authService.signin(signinDto);
        return new ResponseEntity(tokenDto, HttpStatus.OK);
    }


    @PostMapping("/issue")
    public ResponseEntity issueToken(@RequestBody Token token) {
        return ResponseEntity.ok(authService.issueToken(token));
    }


    @GetMapping("/validate")
    public ResponseEntity validateToken(HttpServletRequest request) {
        String token = tokenProvider.resolveToken(request);
        return ResponseEntity.ok(new Validation(tokenProvider.validateAccessToken(token)));
    }


}
