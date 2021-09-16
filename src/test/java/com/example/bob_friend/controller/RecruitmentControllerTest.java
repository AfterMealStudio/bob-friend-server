package com.example.bob_friend.controller;

import com.example.bob_friend.model.dto.RecruitmentRequestDto;
import com.example.bob_friend.model.dto.RecruitmentResponseDto;
import com.example.bob_friend.model.entity.Member;
import com.example.bob_friend.model.entity.Recruitment;
import com.example.bob_friend.model.entity.Sex;
import com.example.bob_friend.service.RecruitmentCommentService;
import com.example.bob_friend.service.RecruitmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(RecruitmentController.class)
@WebMvcTest(useDefaultFilters = false)
@AutoConfigureMockMvc(addFilters = false)
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
//                .username("testMember")
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
//                .username("testAuthor")
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
        // 전체 recruitment를 list 형태로 받아옴
        RecruitmentResponseDto responseDto1 = new RecruitmentResponseDto(testRecruitment);

        given(recruitmentService.findAll())
                .willReturn(Arrays.asList(responseDto1));
        mvc.perform(get("/recruitments"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Arrays.asList(responseDto1))))
                .andDo(print());
    }

    @Test
    void getRecruitment() throws Exception {
        RecruitmentResponseDto responseDto =
                new RecruitmentResponseDto(testRecruitment);
        given(recruitmentService.findById(any()))
                .willReturn(responseDto);

        mvc.perform(get("/recruitments/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(responseDto)))
                .andDo(print());
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
        RecruitmentResponseDto responseDto = new RecruitmentResponseDto(testRecruitment);
        RecruitmentRequestDto requestDto = new RecruitmentRequestDto(testRecruitment);
        given(recruitmentService.createRecruitment(any()))
                .willReturn(responseDto);

        mvc.perform(post("/recruitments").
                        content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)))
                .andDo(print());
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