package com.example.bob_friend.controller;

import com.example.bob_friend.model.dto.MemberSignupDto;
import com.example.bob_friend.model.exception.MemberDuplicatedException;
import com.example.bob_friend.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    @PostMapping("/signup")
    public ResponseEntity signup(@Valid @RequestBody MemberSignupDto memberSignupDto) throws MemberDuplicatedException {
        return ResponseEntity.ok(memberService.signup(memberSignupDto));
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


    @ExceptionHandler(value = MemberDuplicatedException.class)
    public ResponseEntity handleMemberDuplicated(MemberDuplicatedException e) {
        return new ResponseEntity(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = UsernameNotFoundException.class)
    public ResponseEntity handleUsernameNotFound(UsernameNotFoundException e) {
        return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
    }
}
