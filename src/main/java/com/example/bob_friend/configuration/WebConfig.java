package com.example.bob_friend.configuration;

import com.example.bob_friend.interceptor.MemberValidateInterceptor;
import com.example.bob_friend.service.MemberService;
import com.example.bob_friend.service.RecruitmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
//@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private MemberService memberService;

    @Bean
    public HandlerInterceptor memberValidateInterceptor(MemberService memberService) {
        return new MemberValidateInterceptor(memberService);
    }



    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(memberValidateInterceptor(memberService))
                .addPathPatterns("/recruitments/**");
    }
}
