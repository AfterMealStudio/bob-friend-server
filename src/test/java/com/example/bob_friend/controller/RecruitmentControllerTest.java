package com.example.bob_friend.controller;

import com.example.bob_friend.model.entity.Recruitment;
import com.example.bob_friend.model.dto.RecruitmentRequestDto;
import com.example.bob_friend.model.dto.RecruitmentResponseDto;
import com.example.bob_friend.service.RecruitmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(RecruitmentController.class)
@WebMvcTest(useDefaultFilters = false)
class RecruitmentControllerTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    RecruitmentService recruitmentService;

    Recruitment recruitment;

    @BeforeEach
    public void setup() {
        // recruitment 객체를 하나 생성
        recruitment = Recruitment.builder()
                .id(1L)
                .title("title")
                .content("content")
                .build();
    }
    @Test
    void getAllRecruitment() throws Exception {
        // 전체 recruitment를 list 형태로 받아옴
        RecruitmentResponseDto responseDto1 = new RecruitmentResponseDto(Recruitment.builder().id(1L).title("response 1").content("response 1").build());
        RecruitmentResponseDto responseDto2 = new RecruitmentResponseDto(Recruitment.builder().id(2L).title("response 2").content("response 2").build());

        given(recruitmentService.findAll())
                .willReturn(Arrays.asList(responseDto1,responseDto2));
        mvc.perform(get("/recruitments"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Arrays.asList(responseDto1,responseDto2))))
                .andDo(print());
    }

    @Test
    void getRecruitment() throws Exception {
        RecruitmentResponseDto responseDto = new RecruitmentResponseDto(recruitment);
        given(recruitmentService.findById(any()))
                .willReturn(responseDto);

        mvc.perform(get("/recruitments/{id}",1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)))
                .andDo(print());
    }

    @Test
    void createRecruitment() throws Exception {
        RecruitmentResponseDto responseDto = new RecruitmentResponseDto(recruitment);
        given(recruitmentService.add(any()))
                .willReturn(responseDto);

        mvc.perform(post("/recruitments"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)))
                .andDo(print());
    }

    @Test
    void updateRecruitment() throws Exception {
        Recruitment update = Recruitment.builder()
                .id(1L)
                .title("update title")
                .content("update content")
                .build();
        RecruitmentResponseDto responseDto = new RecruitmentResponseDto(update);
        RecruitmentRequestDto requestDto = new RecruitmentRequestDto(update);

        given(recruitmentService.update(any(),any()))
                .willReturn(responseDto);

        mvc.perform(put("/recruitments/{id}",1)
                        .content(objectMapper.writeValueAsString(requestDto))
                )
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
}