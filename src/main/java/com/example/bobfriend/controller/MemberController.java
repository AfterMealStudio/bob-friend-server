package com.example.bobfriend.controller;

import com.example.bobfriend.model.dto.member.Delete;
import com.example.bobfriend.model.dto.member.ResetPassword;
import com.example.bobfriend.model.dto.member.Score;
import com.example.bobfriend.model.dto.member.Update;
import com.example.bobfriend.service.EmailService;
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
    private final EmailService emailService;


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

    @DeleteMapping("/user")
    public ResponseEntity delete(
            @Valid @RequestBody Delete delete) {
        memberService.delete(delete);
        return ResponseEntity.ok().build();
    }


    @PutMapping("/user")
    public ResponseEntity updateUserInfo(
            @RequestBody Update update) {
        return ResponseEntity.ok(memberService.update(update));
    }


    @PostMapping("/user/{nickname}/score")
    public ResponseEntity rateMember(
            @PathVariable String nickname,
            @Valid @RequestBody Score scoreDto) {
        memberService.rateMember(nickname, scoreDto);
        return ResponseEntity.ok().build();
    }


    @PutMapping("/user/password")
    public ResponseEntity resetPassword(@RequestBody ResetPassword resetPassword) {
        String newPassword = memberService.resetPassword(resetPassword);
        emailService.sendMail(resetPassword.getEmail(), "밥친구함 password 관련 메일", newPassword);
        return ResponseEntity.ok().build();
    }

}
