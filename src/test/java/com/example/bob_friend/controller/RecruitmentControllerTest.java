package com.example.bob_friend.controller;

import com.example.bob_friend.model.dto.RecruitmentDto;
import com.example.bob_friend.model.entity.Member;
import com.example.bob_friend.model.entity.Recruitment;
import com.example.bob_friend.model.entity.Sex;
import com.example.bob_friend.service.RecruitmentCommentService;
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
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;

import static com.example.bob_friend.document.ApiDocumentUtils.getDocumentRequest;
import static com.example.bob_friend.document.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
    RecruitmentCommentService recruitmentCommentService;

    Recruitment testRecruitment;
    Member testAuthor;
    Member testMember;

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
                .currentNumberOfPeople(1)
                .totalNumberOfPeople(4)
                .full(false)
                .restaurantName("testRestaurantName")
                .restaurantAddress("testRestaurantAddress")
                .latitude(0.0)
                .longitude(0.0)
                .createdAt(LocalDateTime.now())
                .appointmentTime(LocalDateTime.now().plusHours(4))
                .endAt(LocalDateTime.now().plusDays(1))
                .build();

    }

    @Test
    void getAllRecruitment() throws Exception {
        RecruitmentDto.Response responseDto1 = new RecruitmentDto.Response(testRecruitment);

        given(recruitmentService.findAll())
                .willReturn(Arrays.asList(responseDto1));
        mvc.perform(get("/recruitments"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Arrays.asList(responseDto1))))
                .andDo(document("get-all-recruitments",
                        getDocumentRequest(),
                        getDocumentResponse()
                ));

    }

    @Test
    void getRecruitment() throws Exception {
        RecruitmentDto.Response responseDto =
                new RecruitmentDto.Response(testRecruitment);
        given(recruitmentService.findById(any()))
                .willReturn(responseDto);

        mvc.perform(RestDocumentationRequestBuilders.get("/recruitments/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(responseDto)))
                .andDo(document("get-one-recruitment",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("글 번호")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("글 번호"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("글 제목"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("글 내용"),
                                fieldWithPath("author").type(JsonFieldType.STRING).description("작성자"),
                                fieldWithPath("members").type(JsonFieldType.ARRAY).description("참여 중인 사람"),
                                fieldWithPath("totalNumberOfPeople").type(JsonFieldType.NUMBER).description("총 인원"),
                                fieldWithPath("currentNumberOfPeople").type(JsonFieldType.NUMBER).description("현재 인원"),
                                fieldWithPath("full").type(JsonFieldType.BOOLEAN).description("가득 찼는지 여부"),
                                fieldWithPath("restaurantName").type(JsonFieldType.STRING).description("식당 이름"),
                                fieldWithPath("restaurantAddress").type(JsonFieldType.STRING).description("식당 주소"),
                                fieldWithPath("latitude").type(JsonFieldType.NUMBER).description("위도"),
                                fieldWithPath("longitude").type(JsonFieldType.NUMBER).description("경도"),
                                fieldWithPath("sexRestriction").type(JsonFieldType.VARIES).description("성별 제한"),
                                fieldWithPath("appointmentTime").type(JsonFieldType.STRING).description("약속 시간"),
                                fieldWithPath("createdAt").type(JsonFieldType.STRING).description("작성 시간")
                                )
                ));
    }

    // 단위테스트에서는 controller의 동작만 확인하고, controllerAdvice의 동작은 통합테스트로 넘기기로 한다.
//    @Test
//    void getRecruitmentFail() throws Exception {
//        long recruitmentId = -1L;
////        given(recruitmentService.findById(recruitmentId))
////                .willReturn(null);
////        when(recruitmentService.findById(any())).thenThrow(new RecruitmentNotFoundException(recruitmentId));
//        mvc.perform(get("/recruitments/{id}", 1))
//                .andExpect(status().isNotFound())
//                .andExpect(result -> assertTrue((result.getResolvedException()).getClass().isAssignableFrom(RecruitmentNotFoundException.class)
//                ))
//                .andDo(print());
//    }


    @Test
    void createRecruitment() throws Exception {
        RecruitmentDto.Response responseDto = new RecruitmentDto.Response(testRecruitment);
        RecruitmentDto.Request requestDto = new RecruitmentDto.Request(testRecruitment);
        given(recruitmentService.createRecruitment(any()))
                .willReturn(responseDto);

        mvc.perform(post("/recruitments").
                        content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)))
                .andDo(document("create-recruitment",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("글 번호"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("글 제목"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("글 내용"),
                                fieldWithPath("author").type(JsonFieldType.STRING).description("작성자"),
                                fieldWithPath("members").type(JsonFieldType.ARRAY).description("참여 중인 사람"),
                                fieldWithPath("totalNumberOfPeople").type(JsonFieldType.NUMBER).description("총 인원"),
                                fieldWithPath("currentNumberOfPeople").type(JsonFieldType.NUMBER).description("현재 인원"),
                                fieldWithPath("full").type(JsonFieldType.BOOLEAN).description("가득 찼는지 여부"),
                                fieldWithPath("restaurantName").type(JsonFieldType.STRING).description("식당 이름"),
                                fieldWithPath("restaurantAddress").type(JsonFieldType.STRING).description("식당 주소"),
                                fieldWithPath("latitude").type(JsonFieldType.NUMBER).description("위도"),
                                fieldWithPath("longitude").type(JsonFieldType.NUMBER).description("경도"),
                                fieldWithPath("sexRestriction").type(JsonFieldType.VARIES).description("성별 제한"),
                                fieldWithPath("appointmentTime").type(JsonFieldType.STRING).description("약속 시간"),
                                fieldWithPath("createdAt").type(JsonFieldType.STRING).description("작성 시간")
                        )
                ));
    }

    @Test
    void deleteRecruitment() throws Exception {
        mvc.perform(delete("/recruitments/{id}", 1))
                .andExpect(status().isOk())
                .andDo(print());
    }

    //update 기능 보류
//    @Test
//    void updateRecruitment() throws Exception {
//        Recruitment update = Recruitment.builder()
//                .id(1L)
//                .title("update title")
//                .content("update content")
//                .build();
//        RecruitmentResponseDto responseDto = new RecruitmentResponseDto(update);
//        RecruitmentRequestDto requestDto = new RecruitmentRequestDto(update);
//
//        given(recruitmentService.update(any(),any()))
//                .willReturn(responseDto);
//
//        mvc.perform(put("/recruitments/{id}",1)
//                        .content(objectMapper.writeValueAsString(requestDto))
//                )
//                .andExpect(status().isOk())
//                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)))
//                .andDo(print());

    //    }
}