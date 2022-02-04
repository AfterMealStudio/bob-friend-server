package com.example.bobfriend.service;

import com.example.bobfriend.model.dto.member.*;
import com.example.bobfriend.model.entity.Member;
import com.example.bobfriend.model.entity.Writing;
import com.example.bobfriend.model.exception.MemberDuplicatedException;
import com.example.bobfriend.model.exception.MemberNotAllowedException;
import com.example.bobfriend.repository.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.RandomStringGenerator;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final WritingReportRepository reportRepository;
    private final RecruitmentMemberRepository recruitmentMemberRepository;
    private final WritingRepository writingRepository;
    private final PasswordEncoder passwordEncoder;
    private final MemberAgreementRepository agreementRepository;

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
    public void delete(Delete delete) {
        Member currentMember = getCurrentMember();

        if (!passwordEncoder.matches(delete.getPassword(),
                currentMember.getPassword())) {
            throw new BadCredentialsException("password not correct");
        }

        recruitmentMemberRepository.deleteAllByMember(currentMember);
        agreementRepository.deleteById(currentMember.getEmail());
        
        for (Writing writing : writingRepository.findAllByAuthor(currentMember)) {
            writing.setAuthor(null);
        }

        reportRepository.deleteAllByMember(currentMember);

        memberRepository.delete(currentMember);
    }


    @Transactional
    public Response update(Update update) {
        if (memberRepository.existsMemberByNickname(update.getNickname()))
            throw new MemberDuplicatedException(update.getNickname());
        Member currentMember = getCurrentMember();
        Member incoming = convertToEntity(update);
        return new Response(currentMember.update(incoming));
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

    private String getCurrentUsername() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new AuthenticationException("Authentication not found") {
                @Override
                public String getMessage() {
                    return super.getMessage();
                }
            };
        }

        String username = null;
        if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails user = (UserDetails) authentication.getPrincipal();
            username = user.getUsername();
        } else if (authentication.getPrincipal() instanceof String) {
            username = (String) authentication.getPrincipal();
        }

        return username;
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
                .password((request.getPassword() == null) ? null : passwordEncoder.encode(request.getPassword()))
                .birth(request.getBirth())
                .sex(request.getSex())
                .build();
    }


    @Transactional
    public String resetPassword(ResetPassword resetPassword) {
        String email = resetPassword.getEmail();
        Member member = getMember(email);
        if (!member.getBirth().equals(resetPassword.getBirth()))
            throw new MemberNotAllowedException(member.getEmail());

        String newPassword = generatePassword();

        Member incoming = convertToEntity(new Request() {
            @Override
            public String getPassword() {
                return newPassword;
            }
        });

        member.update(incoming);

        return newPassword;
    }


    private String generatePassword() {
        return new RandomStringGenerator.Builder()
                .withinRange('0', 'z')
                .filteredBy(codePoint -> codePoint != '\\')
                .build()
                .generate(10);
    }


}
