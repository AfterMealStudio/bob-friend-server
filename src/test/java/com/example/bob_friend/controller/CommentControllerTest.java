package com.example.bob_friend.controller;

import com.example.bob_friend.model.dto.CommentDto;
import com.example.bob_friend.model.dto.ReplyDto;
import com.example.bob_friend.model.entity.*;
import com.example.bob_friend.service.CommentService;
import com.example.bob_friend.service.ReportService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static com.example.bob_friend.document.ApiDocumentUtils.*;
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

@AutoConfigureRestDocs
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(useDefaultFilters = false)
@Import(CommentController.class)
public class CommentControllerTest {
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    CommentService commentService;

    @MockBean
    ReportService reportService;

    Recruitment testRecruitment;
    Member testAuthor;
    Member testMember;
    private Comment testComment;
    private Reply testReply;

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
                .build();

        testAuthor = Member.builder()
                .id(1)
                .email("testAuthor@test.com")
                .nickname("testAuthor")
                .password("testPassword")
                .sex(Sex.FEMALE)
                .birth(LocalDate.now())
                .agree(true)
                .active(true)
                .build();

        testRecruitment = Recruitment.builder()
                .id(1L)
                .title("title")
                .content("content")
                .author(testAuthor)
                .members(new HashSet<>())
//                .currentNumberOfPeople(1)
                .totalNumberOfPeople(4)
//                .full(false)
                .restaurantName("testRestaurantName")
                .restaurantAddress("testRestaurantAddress")
                .latitude(0.0)
                .longitude(0.0)
                .createdAt(LocalDateTime.now())
                .appointmentTime(LocalDateTime.now().plusHours(4))
                .endAt(LocalDateTime.now().plusDays(1))
                .active(true)
                .reportCount(0)
                .build();

        testComment = Comment.builder()
                .id(1L)
                .author(testAuthor)
                .recruitment(testRecruitment)
                .content("test comment")
                .replies(new HashSet<>())
                .reportCount(0)
                .createdAt(LocalDateTime.now())
                .build();

        testReply = Reply.builder()
                .id(1L)
                .author(testAuthor)
                .comment(testComment)
                .content("test reply")
                .reportCount(0)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void getAllComments() throws Exception {
        CommentDto.Response commentDto = new CommentDto.Response(testComment);

        List<CommentDto.Response> responseList = Arrays.asList(commentDto);
        when(commentService.getAllCommentByRecruitmentId(any()))
                .thenReturn(responseList);

        mvc.perform(getRequestBuilder(
                        get("/recruitments/{recruitmentId}/comments", 1)))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(responseList)
                ))
                .andDo(document("comment/get-all-comments-by-recruitment",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("recruitmentId").description("약속 번호")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("토큰")
                        )
                ));
    }

    @Test
    void createComment() throws Exception {
        CommentDto.Request requestDto = new CommentDto.Request(testComment);
        CommentDto.Response responseDto = new CommentDto.Response(testComment);

        when(commentService.createComment(any(), any()))
                .thenReturn(responseDto);

        mvc.perform(getRequestBuilder(
                        post("/recruitments/{recruitmentId}/comments", 1))
                        .content(objectMapper.writeValueAsString(requestDto))
                )
                .andExpect(status().isOk())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(responseDto)
                ))
                .andDo(document("comment/create-comment",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("recruitmentId").description("약속 번호")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("토큰")
                        )
                ));
    }

    @Test
    void reportComment() throws Exception {
        mvc.perform(getRequestBuilder(
                        patch("/recruitments/{recruitmentId}/comments/{commentId}/report",
                                1, 1)))
                .andExpect(status().isOk())
                .andDo(document("comment/report-comment",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("recruitmentId").description("약속 번호"),
                                parameterWithName("commentId").description("댓글 번호")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("토큰")
                        )));

    }

    @Test
    void createReplyToComment() throws Exception {
        ReplyDto.Request requestDto = new ReplyDto.Request(testReply);
        ReplyDto.Response responseDto = new ReplyDto.Response(testReply);

        when(commentService.createReply(any(), any()))
                .thenReturn(responseDto);

        mvc.perform(getRequestBuilder(
                        post("/recruitments/{recruitmentId}/comments/{commentId}/replies", 1, 1))
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(responseDto)
                ))
                .andDo(document("comment/reply/create-reply",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("recruitmentId").description("약속 번호"),
                                parameterWithName("commentId").description("댓글 번호")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("토큰")
                        )
                ));
    }


    @Test
    void deleteCommentTest() throws Exception {
        mvc.perform(getRequestBuilder(
                        delete("/recruitments/{recruitmentId}/comments/{commentId}", 1, 1)))
                .andExpect(status().isOk())
                .andDo(document("comment/delete-comment",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("recruitmentId").description("약속 번호"),
                                parameterWithName("commentId").description("댓글 번호")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("토큰")
                        )
                ));
    }


    @Test
    void deleteReplyTest() throws Exception {
        mvc.perform(getRequestBuilder(
                        delete("/recruitments/{recruitmentId}/comments/{commentId}/replies/{replyId}", 1, 1, 1)))
                .andExpect(status().isOk())
                .andDo(document("comment/reply/delete-reply",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("recruitmentId").description("약속 번호"),
                                parameterWithName("commentId").description("댓글 번호"),
                                parameterWithName("replyId").description("대댓글 번호")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("토큰")
                        )
                ));
    }

    @Test
    void reportReply() throws Exception {
        mvc.perform(getRequestBuilder(
                        patch(
                                "/recruitments/{recruitmentId}/comments/{commentId}/replies/{replyId}/report",
                                1, 1, 1)))
                .andExpect(status().isOk())
                .andDo(document("comment/reply/report-reply",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("recruitmentId").description("약속 번호"),
                                parameterWithName("commentId").description("댓글 번호"),
                                parameterWithName("replyId").description("대댓글 번호")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("토큰")
                        )));

    }
}
