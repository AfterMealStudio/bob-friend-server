package com.example.bob_friend.controller;

import com.example.bob_friend.model.dto.MemberSignupDto;
import com.example.bob_friend.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity signup(@Valid @RequestBody MemberSignupDto memberSignupDto) {
        return ResponseEntity.ok(memberService.signup(memberSignupDto));
    }

    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity getMyUserInfo() {
        return ResponseEntity.ok(memberService.getMyMemberWithAuthorities());
    }

    @GetMapping("/user/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity getUserInfo(@PathVariable String username) {
        return ResponseEntity.ok(memberService.getMemberWithAuthorities(username));
    }


}
