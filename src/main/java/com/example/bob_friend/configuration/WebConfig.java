package com.example.bob_friend.configuration;

import com.example.bob_friend.interceptor.RecruitmentJoinInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//@Configuration
//@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new RecruitmentJoinInterceptor())
//                .addPathPatterns("/recruitments/**");
//    }
}
