package com.example.bobfriend.controller;

import com.example.bobfriend.jwt.JwtTokenProvider;
import com.example.bobfriend.model.dto.member.Response;
import com.example.bobfriend.model.dto.member.Signin;
import com.example.bobfriend.model.dto.member.Signup;
import com.example.bobfriend.model.dto.token.Token;
import com.example.bobfriend.model.dto.token.Validation;
import com.example.bobfriend.model.entity.Member;
import com.example.bobfriend.model.entity.Sex;
import com.example.bobfriend.service.AuthService;
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
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static com.example.bobfriend.document.ApiDocumentUtils.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    AuthService authService;
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
                .birth(LocalDate.parse("1997-06-04"))
                .numberOfJoin(0)
                .rating(0.0)
                .agree(true)
                .active(true)
                .emailVerified(false)
                .build();
    }


    @Test
    void signin() throws Exception {
        Token tokenDto = new Token("jwt-access-token-example", "jwt-refresh-token-example");
        when(authService.signin(any())).thenReturn(tokenDto);

        Signin login = Signin.builder()
                .email(testMember.getEmail())
                .password(testMember.getPassword())
                .build();

        mvc.perform(post("/api/signin")
                        .content(objectMapper.writeValueAsString(login))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        tokenDto
                )))

                .andDo(document("auth/signin",
                        getDocumentRequest(),
                        getDocumentResponse())
                );
    }


    @Test
    void signup() throws Exception {
        Token tokenDto = new Token("jwt-access-token-example", "jwt-refresh-token-example");
        Signup signup = Signup.builder()
                .email(testMember.getEmail())
                .nickname(testMember.getNickname())
                .password("1234")
                .birth(testMember.getBirth())
                .sex(Sex.MALE)
                .agree(true)
                .build();
        Response response = Response.builder()
                .id(testMember.getId())
                .email(testMember.getEmail())
                .nickname(testMember.getNickname())
                .age(testMember.getAge())
                .sex(testMember.getSex())
                .rating(testMember.getRating())
                .accumulatedReports(testMember.getAccumulatedReports())
                .reportCount(testMember.getReportCount())
                .agree(testMember.getAgree())
                .active(testMember.isActive())
                .build();

        when(authService.signup(any()))
                .thenReturn(response);


        mvc.perform(post("/api/signup")
                        .content(objectMapper.writeValueAsString(signup))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        response
                )))

                .andDo(document("auth/signup",
                        getDocumentRequest(),
                        getDocumentResponse())
                );
    }


    @Test
    void reissueTest() throws Exception {
        Token tokenDto = Token.builder()
                .accessToken("new-access-token-example")
                .refreshToken("new-refresh-token-example")
                .build();
        when(authService.issueToken(any()))
                .thenReturn(tokenDto);
        mvc.perform(post("/api/issue")
                        .content(objectMapper.writeValueAsString(
                                Token.builder()
                                        .accessToken("old-access-token")
                                        .refreshToken("old-refresh-token")
                                        .build()
                        ))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(tokenDto)))
                .andDo(document("auth/issue",
                        getDocumentRequest(),
                        getDocumentResponse()));
    }


    @Test
    void validateToken() throws Exception {
        when(tokenProvider.validateAccessToken(any()))
                .thenReturn(true);

        mvc.perform(getRequestBuilder(get("/api/validate")))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(
                                new Validation(tokenProvider.validateAccessToken(any()))
                        )
                ))
                .andDo(document("auth/validate",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("토큰")
                        )));
    }
}
