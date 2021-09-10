package com.example.bob_friend.interceptor;

import com.example.bob_friend.model.entity.Member;
import com.example.bob_friend.model.exception.MemberNotActiveException;
import com.example.bob_friend.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

@RequiredArgsConstructor
public class MemberValidateInterceptor implements HandlerInterceptor {
    @Autowired
    private final MemberService memberService;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        Member currentMember = memberService.getCurrentMember();
        LocalDate reportEnd = currentMember.getReportEnd();
        if (reportEnd != null) {
            if (LocalDate.now().isAfter(reportEnd)) {
                currentMember.setActive();
            }
        }

        if (!currentMember.isActive()) {
            throw new MemberNotActiveException(currentMember.getUsername());
        }

        return true;
    }

}
