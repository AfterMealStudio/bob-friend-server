package com.example.bobfriend.controller;

import com.example.bobfriend.model.dto.MemberDto;
import com.example.bobfriend.service.AuthService;
import com.example.bobfriend.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {
    private final MemberService memberService;
    private final AuthService authService;

    @GetMapping("")
    public ResponseEntity verifyEmail(@RequestParam String email, @RequestParam String code) {
        memberService.checkMemberWithCode(email, code);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/email/{email}")
    public ResponseEntity checkEmail(@PathVariable String email) {
        return ResponseEntity.ok(memberService.checkExistByEmail(email));
    }

    @GetMapping("/nickname/{nickname}")
    public ResponseEntity checkNickname(@PathVariable String nickname) {
        return ResponseEntity.ok(memberService.checkExistByNickname(nickname));
    }

    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity getMyUserInfo() throws UsernameNotFoundException {
        return ResponseEntity.ok(memberService.getMyMemberWithAuthorities());
    }

    @GetMapping("/user/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity getUserInfo(@PathVariable String username) throws UsernameNotFoundException {
        return ResponseEntity.ok(memberService.getMemberWithAuthorities(username));
    }

    @DeleteMapping("/user/{memberId}")
    public ResponseEntity deleteUserById(
            @PathVariable Long memberId,
            @Valid @RequestBody MemberDto.Delete delete) {
        authService.checkPassword(delete);
        memberService.deleteById(memberId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/user/{nickname}/score")
    public ResponseEntity rateMember(
            @PathVariable String nickname,
            @Valid @RequestBody MemberDto.Rate rate) {
        memberService.rateMember(nickname, rate);
        return ResponseEntity.ok().build();
    }
}