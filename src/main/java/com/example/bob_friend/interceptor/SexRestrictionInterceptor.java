package com.example.bob_friend.interceptor;

import com.example.bob_friend.model.dto.MemberResponseDto;
import com.example.bob_friend.model.dto.RecruitmentResponseDto;
import com.example.bob_friend.model.entity.Sex;
import com.example.bob_friend.model.exception.RestrictionFailedException;
import com.example.bob_friend.service.MemberService;
import com.example.bob_friend.service.RecruitmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
public class SexRestrictionInterceptor implements HandlerInterceptor {
    @Autowired
    private final RecruitmentService recruitmentService;
    @Autowired
    private final MemberService memberService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!request.getMethod().equals("PATCH")) {
            return true;
        }
        String requestURI = request.getRequestURI();
        String pattern = "/recruitments/*";

        AntPathMatcher pathMatcher = new AntPathMatcher();

        Long recruitmentId = Long.parseLong(pathMatcher.extractPathWithinPattern(pattern, requestURI));

        MemberResponseDto member = memberService.getMyMemberWithAuthorities();
        RecruitmentResponseDto recruitment =
                recruitmentService.findById(recruitmentId);

        if (!checkSexRestriction(recruitment, member)) {
            throw new RestrictionFailedException(member.getUsername());
        }

        return true;
    }

    public boolean checkSexRestriction(RecruitmentResponseDto recruitment,
                                       MemberResponseDto member) {
        String restriction = recruitment.getSexRestriction().name();
        if (restriction.equals(Sex.NONE) || restriction.equals(member.getSex().name())) {
            return true;
        }
        return false;
    }

}
