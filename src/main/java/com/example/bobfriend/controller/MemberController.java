package com.example.bobfriend.controller;

import com.example.bobfriend.model.dto.member.*;
import com.example.bobfriend.service.EmailService;
import com.example.bobfriend.service.MemberDeleteService;
import com.example.bobfriend.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class MemberController {
    private final MemberService memberService;
    private final EmailService emailService;
    private final MemberDeleteService deleteService;

    @GetMapping("/email/{email}")
    public ResponseEntity checkEmail(@PathVariable String email) {
        return ResponseEntity.ok(memberService.existsByEmail(email));
    }

    @GetMapping("/nickname/{nickname}")
    public ResponseEntity checkNickname(@PathVariable String nickname) {
        return ResponseEntity.ok(memberService.existsByNickname(nickname));
    }

    @GetMapping
    public ResponseEntity getMyUserInfo() throws UsernameNotFoundException {
        return ResponseEntity.ok(memberService.getMyMemberWithAuthorities());
    }

    @GetMapping("/{username}")
    public ResponseEntity getUserInfo(@PathVariable String username) throws UsernameNotFoundException {
        return ResponseEntity.ok(memberService.getMemberWithAuthorities(username));
    }

    @DeleteMapping
    public ResponseEntity delete(
            @RequestBody @Valid Delete delete) {
        deleteService.delete();
        return ResponseEntity.ok().build();
    }


    @PutMapping
    public ResponseEntity updateUserInfo(
            @RequestBody Update update) {
        return ResponseEntity.ok(memberService.update(update));
    }


    @PostMapping("/{nickname}/score")
    public ResponseEntity rateMember(
            @PathVariable String nickname,
            @Valid @RequestBody Score scoreDto) {
        memberService.rateMember(nickname, scoreDto);
        return ResponseEntity.ok().build();
    }


    @PatchMapping("/password/reset")
    public ResponseEntity resetPassword(@RequestBody @Valid ResetPassword resetPassword) {
        String newPassword = memberService.resetPassword(resetPassword);
        emailService.sendMail(resetPassword.getEmail(), "밥친구함 password 관련 메일", newPassword);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/password")
    public ResponseEntity updatePassword(@RequestBody @Valid UpdatePassword updatePassword) {
        memberService.updatePassword(updatePassword);
        return ResponseEntity.ok().build();
    }

}
