package com.example.bobfriend.controller;

import com.example.bobfriend.model.dto.MemberDto;
import com.example.bobfriend.model.entity.Member;
import com.example.bobfriend.model.entity.Sex;
import com.example.bobfriend.service.AuthService;
import com.example.bobfriend.service.MemberService;
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
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static com.example.bobfriend.document.ApiDocumentUtils.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(MemberController.class)
@WebMvcTest(useDefaultFilters = false)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
class MemberControllerTest {
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    AuthService authService;
    @MockBean
    MemberService memberService;

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
                .emailVerified(false)
                .rating(0.0)
                .numberOfJoin(0)
                .build();
    }

    @Test
    public void getMyUserInfo() throws Exception {
        MemberDto.Response responseDto = new MemberDto.Response(testMember);
        when(memberService.getMyMemberWithAuthorities())
                .thenReturn(responseDto);
        mvc.perform(getRequestBuilder(
                        get("/api/user"))
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)))
                .andDo(document("member/getMyUserInfo",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("토큰")
                        )
                ));
    }

    @Test
    public void getUserInfo() throws Exception {
        MemberDto.Response responseDto = new MemberDto.Response(testMember);
        when(memberService.getMemberWithAuthorities(any()))
                .thenReturn(responseDto);
        mvc.perform(getRequestBuilder(
                        get("/api/user/{email}", testMember.getEmail()))
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)))
                .andDo(document("member/getUserInfo",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("email").description("이메일")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("토큰")
                        )
                ));
    }

    @Test
    public void checkEmail() throws Exception {
        when(memberService.checkExistByEmail(any()))
                .thenReturn(new MemberDto.DuplicationCheck(false));
        mvc.perform(get("/api/email/{email}", testMember.getEmail()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new MemberDto.DuplicationCheck(false)
                )))
                .andDo(document("member/check-email",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("email").description("이메일")
                        )
                ));

    }

    @Test
    public void checkNickname() throws Exception {
        when(memberService.checkExistByNickname(any()))
                .thenReturn(
                        new MemberDto.DuplicationCheck(false)
                );
        mvc.perform(get("/api/nickname/{nickname}", testMember.getNickname()))
                .andExpect(status().isOk())
                .andExpect(content()
                        .json(objectMapper.writeValueAsString(
                                new MemberDto.DuplicationCheck(false)
                        )))
                .andDo(document("member/check-nickname",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("nickname").description("닉네임")
                        )
                ));

    }


    @Test
    void deleteMember() throws Exception {
        MemberDto.Delete delete = new MemberDto.Delete(testMember.getPassword());
        mvc.perform(getRequestBuilder(
                        delete("/api/user"))
                        .content(objectMapper.writeValueAsString(delete)))
                .andExpect(status().isOk())
                .andDo(document("member/delete",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("토큰")
                        )
                ));
    }

    @Test
    void rateMemberTest() throws Exception {
        MemberDto.Rate rate = new MemberDto.Rate();
        rate.setScore(3.2);
        mvc.perform(getRequestBuilder(
                        post("/api/user/{nickname}/score", testMember.getNickname()))
                        .content(objectMapper.writeValueAsString(rate)))
                .andExpect(status().isOk())
                .andDo(document("member/rate",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("nickname").description("닉네임")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("토큰")
                        )));
    }

}