package com.example.bob_friend.controller;

import com.example.bob_friend.jwt.JwtTokenProvider;
import com.example.bob_friend.model.dto.MemberDto;
import com.example.bob_friend.model.dto.TokenDto;
import com.example.bob_friend.model.entity.Member;
import com.example.bob_friend.model.entity.Sex;
import com.example.bob_friend.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static com.example.bob_friend.document.ApiDocumentUtils.getDocumentRequest;
import static com.example.bob_friend.document.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({AuthenticationController.class})
@WebMvcTest(useDefaultFilters = false)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
public class AuthenticationControllerTest {
    @MockBean
    JwtTokenProvider tokenProvider;
    @Autowired
    AuthenticationManagerBuilder authenticationManagerBuilder;
    @MockBean
    MemberService memberService;
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;

    Member testMember;
    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setup() {
        testMember = Member.builder()
                .id(1)
                .email("testMember@test.com")
                .nickname("testMember")
                .password("testPassword")
                .sex(Sex.FEMALE)
                .birth(LocalDate.now())
                .agree(true)
                .active(true)
                .verified(false)
                .build();
    }
    @Test
    public void signin() throws Exception {
        when(memberService.isExistByEmail(any())).thenReturn(true);
        when(tokenProvider.createToken(any())).thenReturn("jwt-token-example");

        MemberDto.Login login = MemberDto.Login.builder()
                .email(testMember.getEmail())
                .password(testMember.getPassword())
                .build();

        mvc.perform(post("/api/signin")
                        .content(objectMapper.writeValueAsString(login))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new TokenDto("jwt-token-example")
                )))

                .andDo(document("member/signin",
                        getDocumentRequest(),
                        getDocumentResponse())
                );

    }
}