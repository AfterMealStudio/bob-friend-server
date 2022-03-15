package com.example.bobfriend.validator;

import com.example.bobfriend.model.entity.Member;
import com.example.bobfriend.model.exception.MemberNotFoundException;
import com.example.bobfriend.repository.MemberRepository;
import com.example.bobfriend.util.AuthenticationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
public class PasswordCorrectValidator implements ConstraintValidator<PasswordCorrect, String> {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return false;

        String email = AuthenticationUtil.getCurrentUsername();
        Member currentMember = memberRepository.findMemberByEmail(email)
                .orElseThrow(MemberNotFoundException::new);

        return passwordEncoder.matches(value, currentMember.getPassword());
    }
}
