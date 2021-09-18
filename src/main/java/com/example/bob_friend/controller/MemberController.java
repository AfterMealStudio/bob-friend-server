package com.example.bob_friend.controller;

import com.example.bob_friend.model.dto.MemberDto;
import com.example.bob_friend.model.exception.MemberDuplicatedException;
import com.example.bob_friend.service.MemberService;
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

    @GetMapping("")
    public ResponseEntity verifyEmail(@RequestParam String email, @RequestParam String code) {
        memberService.checkMemberWithCode(email, code);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/signup")
    public ResponseEntity signup(@Valid @RequestBody MemberDto.Signup memberSignupDto) throws MemberDuplicatedException {
        return ResponseEntity.ok(memberService.signup(memberSignupDto));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity checkEmail(@PathVariable String email) {
        return ResponseEntity.ok(memberService.isExistByEmail(email));
    }

    @GetMapping("/nickname/{nickname}")
    public ResponseEntity checkNickname(@PathVariable String nickname) {
        return ResponseEntity.ok(memberService.isExistByNickname(nickname));
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
    public ResponseEntity deleteUserById(@PathVariable Long memberId) {
        memberService.deleteById(memberId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("user/{username}")
    public ResponseEntity reportUser(@PathVariable String username) {
        return ResponseEntity.ok(memberService.reportMember(username));
    }

}
