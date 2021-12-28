package com.example.bobfriend.controller;

import com.example.bobfriend.model.dto.recruitment.*;
import com.example.bobfriend.model.entity.*;
import com.example.bobfriend.service.*;
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

import static com.example.bobfriend.document.ApiDocumentUtils.*;
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
    RecruitmentFindService recruitmentFindService;
    @MockBean
    RecruitmentSearchService recruitmentSearchService;
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
                .replies(List.of(testReply))
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
                .latitude(33.4566084914484)
                .longitude(126.56207301534569)
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
        SimpleResponse responseDto1 =
                new SimpleResponse(testRecruitment);

        PageImpl<SimpleResponse> responsePage =
                new PageImpl<>(Arrays.asList(responseDto1));
        given(recruitmentFindService.findAll(any()))
                .willReturn(responsePage);

        mvc.perform(requestBuilderWithAuthorizationHeader(
                        get("/api/recruitments"))
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
        SimpleResponse responseDto1 =
                new SimpleResponse(testRecruitment);

        PageImpl<SimpleResponse> responsePage =
                new PageImpl<>(Arrays.asList(responseDto1));

        when(recruitmentFindService.findMyRecruitments(any()))
                .thenReturn(responsePage);

        mvc.perform(requestBuilderWithAuthorizationHeader(
                        get("/api/recruitments"))
                        .param("type", "owned")
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
        SimpleResponse responseDto1 =
                new SimpleResponse(testRecruitment);

        String testRestaurantAddress = "testRestaurantAddress";
        PageImpl<SimpleResponse> responsePage =
                new PageImpl<>(Arrays.asList(responseDto1));
        given(recruitmentFindService.findAllByRestaurantAddress(any(), any()))
                .willReturn(responsePage);
        mvc.perform(requestBuilderWithAuthorizationHeader(
                        get("/api/recruitments"))
                        .param("type", "specific")
                        .param("address", testRestaurantAddress))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responsePage)))
                .andDo(document("recruitment/get-all-recruitments-by-address",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestParameters(
                                parameterWithName("type")
                                        .description("specific"),
                                parameterWithName("address")
                                        .description("식당 주소")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("토큰")
                        )
                ));
    }

//    @Test
//    void getAllRecruitments_restaurant() throws Exception {
//        RecruitmentDto.ResponseList responseDto1 =
//                new RecruitmentDto.ResponseList(testRecruitment);
//        PageImpl<RecruitmentDto.ResponseList> responsePage =
//                new PageImpl<>(Arrays.asList(responseDto1));
//
//        String testRestaurantName = "testRestaurantName";
//        String testRestaurantAddress = "testRestaurantAddress";
//
//        given(recruitmentService
//                .findAllByRestaurant(
//                        any(),
//                        any()))
//                .willReturn(responsePage);
//
//        mvc.perform(getRequestBuilder(
//                        get("/recruitments"))
//                        .param("restaurantAddress", testRestaurantAddress)
//                        .param("restaurantName", testRestaurantName)
//                )
//                .andExpect(status().isOk())
//                .andExpect(content().json(objectMapper.writeValueAsString(responsePage)))
//                .andDo(document("recruitment/get-all-recruitments-by-restaurant",
//                        getDocumentRequest(),
//                        getDocumentResponse(),
//                        requestParameters(
//                                parameterWithName("restaurantName")
//                                        .description("식당 이름").optional(),
//                                parameterWithName("restaurantAddress")
//                                        .description("식당 주소").optional()
//                        ),
//                        requestHeaders(
//                                headerWithName(HttpHeaders.AUTHORIZATION)
//                                        .description("토큰")
//                        )
//                ));
//    }

    @Test
    void getAllLocations() throws Exception {
        Address addressDto =
                new Address(testRecruitment);

        addressDto.setCount(1);

        Addresses value = new Addresses(List.of(addressDto));
        given(recruitmentFindService.findAllLocations(any(), any(), any()))
                .willReturn(value);

        mvc.perform(requestBuilderWithAuthorizationHeader(
                        get("/api/recruitments/locations"))
                        .param("zoom", String.valueOf(3))
                        .param("longitude", String.valueOf(126.56207301534569))
                        .param("latitude", String.valueOf(33.4566084914484))
                )
                .andExpect(status().isOk())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(value)))
                .andDo(document("recruitment/get-all-recruitments-locations",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("토큰")
                        ),
                        responseFields(
                                fieldWithPath("addresses[].latitude").description("위도"),
                                fieldWithPath("addresses[].longitude").description("경도"),
                                fieldWithPath("addresses[].address").description("주소"),
                                fieldWithPath("addresses[].count").description("해당 좌표에 존재하는 약속의 수")
                        )
                ));
    }

    @Test
    void getRecruitment() throws Exception {
        DetailResponse detailResponseDto =
                new DetailResponse(testRecruitment);
        given(recruitmentFindService.findById(any()))
                .willReturn(detailResponseDto);

        mvc.perform(requestBuilderWithAuthorizationHeader(
                        get("/api/recruitments/{recruitmentId}",
                                1))
                )
                .andExpect(status().isOk())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(detailResponseDto)))
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
        DetailResponse detailResponseDto =
                new DetailResponse(testRecruitment);
        Create requestDto =
                new Create(testRecruitment);
        given(recruitmentService.create(any()))
                .willReturn(detailResponseDto);

        mvc.perform(requestBuilderWithAuthorizationHeader(
                        post("/api/recruitments"))
                        .content(objectMapper.writeValueAsString(requestDto))
                )
                .andExpect(status().isOk())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(detailResponseDto)))
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
        DetailResponse detailResponse = new DetailResponse(testRecruitment);
        when(recruitmentService.joinOrUnjoin(any()))
                .thenReturn(detailResponse);

        mvc.perform(requestBuilderWithAuthorizationHeader(
                        patch("/api/recruitments/{recruitmentId}",
                                testRecruitment.getId())
                ))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(detailResponse)
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
        mvc.perform(requestBuilderWithAuthorizationHeader(
                        delete("/api/recruitments/{recruitmentId}",
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
        DetailResponse detailResponseDto =
                new DetailResponse(testRecruitment);
        PageImpl<DetailResponse> responsePage =
                new PageImpl<>(Arrays.asList(detailResponseDto));
        when(recruitmentSearchService.searchTitle(any(), any()))
                .thenReturn(new PageImpl<>(Arrays.asList(detailResponseDto)));
        mvc.perform(requestBuilderWithAuthorizationHeader(
                        get("/api/recruitments/search"))
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
                                parameterWithName("category").description("검색 분류(all, title, place, content)"),
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
        mvc.perform(requestBuilderWithAuthorizationHeader(
                        patch("/api/recruitments/{recruitmentId}/report",
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
        mvc.perform(requestBuilderWithAuthorizationHeader(
                        patch("/api/recruitments/{recruitmentId}/close",
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