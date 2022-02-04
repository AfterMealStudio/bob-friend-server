package com.example.bobfriend.controller;

import com.example.bobfriend.jwt.JwtTokenProvider;
import com.example.bobfriend.model.dto.member.Response;
import com.example.bobfriend.model.dto.member.Signin;
import com.example.bobfriend.model.dto.member.Signup;
import com.example.bobfriend.model.dto.token.Token;
import com.example.bobfriend.model.dto.token.Validation;
import com.example.bobfriend.model.exception.MemberDuplicatedException;
import com.example.bobfriend.service.AuthService;
import com.example.bobfriend.service.MemberAgreementService;
import com.example.bobfriend.service.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final JwtTokenProvider tokenProvider;
    private final AuthService authService;
    private final VerificationService verificationService;
    private final MemberAgreementService agreementService;

    @PostMapping("/signup")
    public ResponseEntity signup(@Valid @RequestBody Signup signupDto) throws MemberDuplicatedException {
        agreementService.save(signupDto);
        Response signup = authService.signup(signupDto);
        verificationService.sendVerification(signupDto.getEmail());
        return ResponseEntity.ok(signup);
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


    @GetMapping("/verify")
    public ModelAndView verifyEmail(@RequestParam String email, @RequestParam String code) {
        verificationService.confirm(email, code);
        ModelAndView modelAndView = new ModelAndView("/pages/emailVerification.html");
        return modelAndView;
    }


    @GetMapping("/verify/retry")
    public ResponseEntity retryVerify(@RequestParam String email) {
        verificationService.sendVerification(email);
        return ResponseEntity.ok().build();
    }
}
