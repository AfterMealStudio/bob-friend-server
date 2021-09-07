package com.example.bob_friend.configuration;

import com.example.bob_friend.interceptor.MemberValidateInterceptor;
import com.example.bob_friend.interceptor.RecruitmentJoinInterceptor;
import com.example.bob_friend.service.MemberService;
import com.example.bob_friend.service.RecruitmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
//@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private RecruitmentService recruitmentService;
    @Autowired
    private MemberService memberService;

    @Bean
    public MemberValidateInterceptor memberValidateInterceptor(MemberService memberService) {
        return new MemberValidateInterceptor(memberService);
    }

    @Bean
    public RecruitmentJoinInterceptor recruitmentJoinInterceptor(RecruitmentService recruitmentService,
                                                                 MemberService memberService) {
        return new RecruitmentJoinInterceptor(recruitmentService, memberService);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(memberValidateInterceptor(memberService))
                .addPathPatterns("/recruitments/**");
        registry.addInterceptor(recruitmentJoinInterceptor(recruitmentService, memberService))
                .addPathPatterns("/recruitments/**");
    }
}
