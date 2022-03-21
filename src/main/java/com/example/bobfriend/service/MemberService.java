package com.example.bobfriend.service;

import com.example.bobfriend.model.dto.member.*;
import com.example.bobfriend.model.entity.Member;
import com.example.bobfriend.model.exception.MemberDuplicatedException;
import com.example.bobfriend.model.exception.MemberNotAllowedException;
import com.example.bobfriend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.RandomStringGenerator;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.bobfriend.util.AuthenticationUtil.getCurrentUsername;

@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Response getMemberWithAuthorities(String email) {
        Member member = getMember(email);
        return new Response(member);
    }

    @Transactional(readOnly = true)
    public Response getMyMemberWithAuthorities() {
        String currentUsername = getCurrentUsername();

        Member member = getMember(currentUsername);
        return new Response(member);
    }


    @Transactional
    public Response update(Update update) {
        if (memberRepository.existsMemberByNickname(update.getNickname()))
            throw new MemberDuplicatedException(update.getNickname());
        Member currentMember = getCurrentMember();
        Member incoming = convertToEntity(update);
        currentMember.update(incoming);
        return new Response(currentMember);
    }

    @Transactional
    public void updatePassword(UpdatePassword updatePassword) {
        Member currentMember = getCurrentMember();
        currentMember.setPassword(updatePassword.getPassword(), passwordEncoder);
    }

    public Exist existsByEmail(String email) {
        return new Exist(
                memberRepository.existsMemberByEmail(email));
    }

    public Exist existsByNickname(String nickname) {
        return new Exist(
                memberRepository.existsMemberByNickname(nickname));
    }

    @Transactional
    public Response rateMember(String nickname, Score rate) {
        Member member = getMemberByNickname(nickname);
        Double score = rate.getScore();
        member.addRating(score);
        return new Response(member);
    }

    @Transactional
    public String resetPassword(ResetPassword resetPassword) {
        String email = resetPassword.getEmail();
        Member member = getMember(email);
        if (!member.getBirth().equals(resetPassword.getBirth()))
            throw new MemberNotAllowedException(member.getEmail());

        String newPassword = generatePassword();

        member.setPassword(newPassword, passwordEncoder);

        return newPassword;
    }

    private Member getMemberByNickname(String nickname) {
        return memberRepository.findMemberByNickname(nickname)
                .orElseThrow(() -> {
                            throw new UsernameNotFoundException(
                                    nickname + " not found");
                        }
                );
    }

    private Member getMember(String currentUsername) {
        return memberRepository.findMemberWithAuthoritiesByEmail(currentUsername)
                .orElseThrow(() -> {
                            throw new UsernameNotFoundException(currentUsername);
                        }
                );
    }


    @Transactional
    Member getCurrentMember() {
        String currentUsername = getCurrentUsername();
        Member currentMember = getMember(currentUsername);
        return currentMember;
    }


    Member convertToEntity(Request request) {
        return Member.builder()
                .email(request.getEmail())
                .nickname(request.getNickname())
                .birth(request.getBirth())
                .sex(request.getSex())
                .build();
    }



    private String generatePassword() {
        return new RandomStringGenerator.Builder()
                .withinRange('0', 'z')
                .filteredBy(codePoint -> codePoint != '\\')
                .build()
                .generate(10);
    }


}
