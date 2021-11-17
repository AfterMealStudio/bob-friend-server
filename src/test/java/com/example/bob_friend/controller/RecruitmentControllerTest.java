package com.example.bob_friend.controller;

import com.example.bob_friend.model.dto.RecruitmentDto;
import com.example.bob_friend.model.entity.*;
import com.example.bob_friend.service.CommentService;
import com.example.bob_friend.service.RecruitmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.example.bob_friend.document.ApiDocumentUtils.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(RecruitmentController.class)
@WebMvcTest(useDefaultFilters = false)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
class RecruitmentControllerTest {
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    RecruitmentService recruitmentService;
    @MockBean
    CommentService commentService;

    Recruitment testRecruitment;
    Member testAuthor;
    Member testMember;
    Comment testComment;
    Reply testReply;

    @BeforeEach
    public void setup() {
        testMember = Member.builder()
                .id(2)
                .email("testMember@test.com")
                .nickname("testMember")
                .password("testPassword")
                .sex(Sex.FEMALE)
                .birth(LocalDate.now())
                .rating(0.0)
                .numberOfJoin(0)
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
                .rating(0.0)
                .numberOfJoin(0)
                .agree(true)
                .active(true)
                .build();

        testReply = Reply.builder()
                .id(3L)
                .author(testAuthor)
                .comment(testComment)
                .content("test reply")
                .createdAt(LocalDateTime.now())
                .reportCount(0)
                .build();
        testComment = Comment.builder()
                .id(2L)
                .author(testAuthor)
                .recruitment(testRecruitment)
                .content("test comment")
                .replies(Set.of(testReply))
                .createdAt(LocalDateTime.now())
                .reportCount(0)
                .build();


        testRecruitment = Recruitment.builder()
                .id(1L)
                .title("title")
                .content("content")
                .author(testAuthor)
                .members(new HashSet<>())
                .comments(List.of(testComment))
                .totalNumberOfPeople(4)
                .restaurantName("testRestaurantName")
                .restaurantAddress("testRestaurantAddress")
                .latitude(0.0)
                .longitude(0.0)
                .createdAt(LocalDateTime.now())
                .appointmentTime(LocalDateTime.now().plusHours(4))
                .endAt(LocalDateTime.now().plusDays(1))
                .sexRestriction(Sex.NONE)
                .active(true)
                .reportCount(0)
                .build();
        testRecruitment.addMember(testMember);
    }

    @Test
    void getAllRecruitment() throws Exception {
        RecruitmentDto.ResponseList responseDto1 =
                new RecruitmentDto.ResponseList(testRecruitment);

        PageImpl<RecruitmentDto.ResponseList> responsePage =
                new PageImpl<>(Arrays.asList(responseDto1));
        given(recruitmentService.findAllByRestaurant(any(),any()))
                .willReturn(responsePage);

        mvc.perform(getRequestBuilder(
                        get("/recruitments"))
                )
                .andExpect(status().isOk())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(responsePage)
                ))
                .andDo(document("recruitment/get-all-recruitments",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("토큰")
                        )
                ));
    }

    @Test
    void getAllRecruitment_my() throws Exception {
        RecruitmentDto.ResponseList responseDto1 =
                new RecruitmentDto.ResponseList(testRecruitment);

        PageImpl<RecruitmentDto.ResponseList> responsePage =
                new PageImpl<>(Arrays.asList(responseDto1));

        when(recruitmentService.findMyRecruitments(any()))
                .thenReturn(responsePage);

        mvc.perform(getRequestBuilder(
                        get("/recruitments"))
                        .param("type","owned")
                )
                .andExpect(status().isOk())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(responsePage)
                ))
                .andDo(document("recruitment/get-all-recruitments-my",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("토큰")
                        )
                ));
    }


    @Test
    void getAllRecruitments_restaurantAddress() throws Exception {
        RecruitmentDto.ResponseList responseDto1 =
                new RecruitmentDto.ResponseList(testRecruitment);

        String testRestaurantAddress = "testRestaurantAddress";
        PageImpl<RecruitmentDto.ResponseList> responsePage =
                new PageImpl<>(Arrays.asList(responseDto1));
        given(recruitmentService.findAllByRestaurant(any(), any()))
                .willReturn(responsePage);
        mvc.perform(getRequestBuilder(
                        get("/recruitments"))
                        .param("restaurantAddress", testRestaurantAddress))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responsePage)))
                .andDo(document("recruitment/get-all-recruitments-by-address",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestParameters(
                                parameterWithName("restaurantAddress")
                                        .description("식당 주소")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("토큰")
                        )
                ));
    }

    @Test
    void getAllRecruitments_restaurant() throws Exception {
        RecruitmentDto.ResponseList responseDto1 =
                new RecruitmentDto.ResponseList(testRecruitment);
        PageImpl<RecruitmentDto.ResponseList> responsePage =
                new PageImpl<>(Arrays.asList(responseDto1));

        String testRestaurantName = "testRestaurantName";
        String testRestaurantAddress = "testRestaurantAddress";

        given(recruitmentService
                .findAllByRestaurant(
                        any(),
                        any()))
                .willReturn(responsePage);

        mvc.perform(getRequestBuilder(
                        get("/recruitments"))
                        .param("restaurantAddress", testRestaurantAddress)
                        .param("restaurantName", testRestaurantName)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responsePage)))
                .andDo(document("recruitment/get-all-recruitments-by-restaurant",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestParameters(
                                parameterWithName("restaurantName")
                                        .description("식당 이름").optional(),
                                parameterWithName("restaurantAddress")
                                        .description("식당 주소").optional()
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("토큰")
                        )
                ));
    }

    @Test
    void getAllLocations() throws Exception {
        RecruitmentDto.Address addressDto =
                new RecruitmentDto.Address(testRecruitment);

        addressDto.setCount(1);

        given(recruitmentService.findAllLocations())
                .willReturn(Set.of(addressDto));

        mvc.perform(getRequestBuilder(
                        get("/recruitments/locations"))
                )
                .andExpect(status().isOk())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(Arrays.asList(addressDto))))
                .andDo(document("recruitment/get-all-recruitments-locations",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("토큰")
                        ),
                        responseFields(
                                fieldWithPath("[].latitude").description("위도"),
                                fieldWithPath("[].longitude").description("경도"),
                                fieldWithPath("[].address").description("주소"),
                                fieldWithPath("[].count").description("해당 좌표에 존재하는 약속의 수")
                        )));
    }

    @Test
    void getRecruitment() throws Exception {
        RecruitmentDto.Response responseDto =
                new RecruitmentDto.Response(testRecruitment);
        given(recruitmentService.findById(any()))
                .willReturn(responseDto);

        mvc.perform(getRequestBuilder(
                        get("/recruitments/{recruitmentId}",
                                1))
                )
                .andExpect(status().isOk())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(responseDto)))
                .andDo(document("recruitment/get-one-recruitment",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("recruitmentId")
                                        .description("글 번호")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("토큰")
                        )
//                        ,
//                        responseFields(
//                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("글 번호"),
//                                fieldWithPath("title").type(JsonFieldType.STRING).description("글 제목"),
//                                fieldWithPath("content").type(JsonFieldType.STRING).description("글 내용"),
//                                fieldWithPath("author").type(JsonFieldType.STRING).description("작성자"),
//                                fieldWithPath("members").type(JsonFieldType.ARRAY).description("참여 중인 사람"),
//                                fieldWithPath("totalNumberOfPeople").type(JsonFieldType.NUMBER).description("총 인원"),
//                                fieldWithPath("currentNumberOfPeople").type(JsonFieldType.NUMBER).description("현재 인원"),
//                                fieldWithPath("full").type(JsonFieldType.BOOLEAN).description("가득 찼는지 여부"),
//                                fieldWithPath("restaurantName").type(JsonFieldType.STRING).description("식당 이름"),
//                                fieldWithPath("restaurantAddress").type(JsonFieldType.STRING).description("식당 주소"),
//                                fieldWithPath("latitude").type(JsonFieldType.NUMBER).description("위도"),
//                                fieldWithPath("longitude").type(JsonFieldType.NUMBER).description("경도"),
//                                fieldWithPath("sexRestriction").type(JsonFieldType.VARIES).description("성별 제한"),
//                                fieldWithPath("appointmentTime").type(JsonFieldType.STRING).description("약속 시간"),
//                                fieldWithPath("createdAt").type(JsonFieldType.STRING).description("작성 시간")
//                                )
                ));
    }


    @Test
    void createRecruitment() throws Exception {
        RecruitmentDto.Response responseDto =
                new RecruitmentDto.Response(testRecruitment);
        RecruitmentDto.Request requestDto =
                new RecruitmentDto.Request(testRecruitment);
        given(recruitmentService.createRecruitment(any()))
                .willReturn(responseDto);

        mvc.perform(getRequestBuilder(
                        post("/recruitments"))
                        .content(objectMapper.writeValueAsString(requestDto))
                )
                .andExpect(status().isOk())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(responseDto)))
                .andDo(document("recruitment/create-recruitment",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("토큰")
                        )
//                        ,
//                        responseFields(
//                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("글 번호"),
//                                fieldWithPath("title").type(JsonFieldType.STRING).description("글 제목"),
//                                fieldWithPath("content").type(JsonFieldType.STRING).description("글 내용"),
//                                fieldWithPath("author").type(JsonFieldType.STRING).description("작성자"),
//                                fieldWithPath("members").type(JsonFieldType.ARRAY).description("참여 중인 사람"),
//                                fieldWithPath("totalNumberOfPeople").type(JsonFieldType.NUMBER).description("총 인원"),
//                                fieldWithPath("currentNumberOfPeople").type(JsonFieldType.NUMBER).description("현재 인원"),
//                                fieldWithPath("full").type(JsonFieldType.BOOLEAN).description("가득 찼는지 여부"),
//                                fieldWithPath("restaurantName").type(JsonFieldType.STRING).description("식당 이름"),
//                                fieldWithPath("restaurantAddress").type(JsonFieldType.STRING).description("식당 주소"),
//                                fieldWithPath("latitude").type(JsonFieldType.NUMBER).description("위도"),
//                                fieldWithPath("longitude").type(JsonFieldType.NUMBER).description("경도"),
//                                fieldWithPath("sexRestriction").type(JsonFieldType.VARIES).description("성별 제한"),
//                                fieldWithPath("appointmentTime").type(JsonFieldType.STRING).description("약속 시간"),
//                                fieldWithPath("createdAt").type(JsonFieldType.STRING).description("작성 시간")
//                        )
                ));
    }

    @Test
    void joinRecruitmentTest() throws Exception {
        Member testMember2 = Member.builder()
                .id(1)
                .email("testMember2@test.com")
                .nickname("testMember2")
                .password("1234")
                .sex(Sex.FEMALE)
                .birth(LocalDate.now())
                .rating(0.0)
                .numberOfJoin(0)
                .agree(true)
                .active(true)
                .build();
        testRecruitment.addMember(testMember2);
        RecruitmentDto.Response response = new RecruitmentDto.Response(testRecruitment);
        when(recruitmentService.joinOrUnjoin(any()))
                .thenReturn(response);

        mvc.perform(getRequestBuilder(
                        patch("/recruitments/{recruitmentId}",
                                testRecruitment.getId())
                ))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(response)
                ))
                .andDo(document("recruitment/join-recruitment",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("recruitmentId")
                                        .description("글 번호")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("토큰")
                        )
                ));
    }

    @Test
    void deleteRecruitment() throws Exception {
        mvc.perform(getRequestBuilder(
                        delete("/recruitments/{recruitmentId}",
                                1))
                )
                .andExpect(status().isOk())
                .andDo(document("recruitment/delete-recruitment",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                pathParameters(
                                        parameterWithName("recruitmentId")
                                                .description("글 번호")
                                ),
                                requestHeaders(
                                        headerWithName(HttpHeaders.AUTHORIZATION)
                                                .description("토큰")
                                )
                        )
                );
    }


    @Test
    void searchRecruitmentTest() throws Exception {
        RecruitmentDto.Response responseDto =
                new RecruitmentDto.Response(testRecruitment);
        PageImpl<RecruitmentDto.Response> responsePage =
                new PageImpl<>(Arrays.asList(responseDto));
        when(recruitmentService.searchTitle(any(), any()))
                .thenReturn(new PageImpl<>(Arrays.asList(responseDto)));
        mvc.perform(getRequestBuilder(
                        get("/recruitments/search"))
                        .param("category", "title")
                        .param("keyword", "ti")
                )
                .andExpect(status().isOk())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(responsePage)
                ))
                .andDo(document("recruitment/search-recruitment",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestParameters(
                                parameterWithName("category").description("검색 분류(title, place, content, time)"),
                                parameterWithName("keyword").description("검색어")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("토큰")
                        )
                ));
    }


    @Test
    void reportRecruitment() throws Exception {
        mvc.perform(getRequestBuilder(
                        patch("/recruitments/{recruitmentId}/report",
                                1)))
                .andExpect(status().isOk())
                .andDo(document("recruitment/report-recruitment",
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
    void closeRecruitment() throws Exception {
        mvc.perform(getRequestBuilder(
                        patch("/recruitments/{recruitmentId}/close",
                                1)))
                .andExpect(status().isOk())
                .andDo(document("recruitment/close-recruitment",
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
}