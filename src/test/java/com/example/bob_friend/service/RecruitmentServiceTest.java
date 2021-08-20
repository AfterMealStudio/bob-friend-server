package com.example.bob_friend.service;

import com.example.bob_friend.model.entity.Recruitment;
import com.example.bob_friend.model.dto.RecruitmentRequestDto;
import com.example.bob_friend.model.dto.RecruitmentResponseDto;
import com.example.bob_friend.model.exception.RecruitmentNotFoundException;
import com.example.bob_friend.repository.RecruitmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecruitmentServiceTest {
    @Mock
    RecruitmentRepository recruitmentRepository;
    @InjectMocks
    RecruitmentServiceImpl recruitmentService;

    Recruitment recruitment1;
    Recruitment recruitment2;

    @BeforeEach
    public void setup() {
        recruitment1 = Recruitment.builder()
                .id(1L)
                .title("test 1")
                .content("test 1")
                .build();
        recruitment2 = Recruitment.builder()
                .id(2L)
                .title("test 2")
                .content("test 2")
                .build();
    }

    @Test
    public void findByIdSuccess() {
        given(recruitmentRepository.findById(recruitment1.getId()))
                .willReturn(Optional.ofNullable(recruitment1));

        RecruitmentResponseDto byId = recruitmentService.findById(recruitment1.getId());
        RecruitmentResponseDto dtoFromDomain = new RecruitmentResponseDto(recruitment1);

        assertThat(byId, equalTo(dtoFromDomain));
    }

    @Test
    public void findByIdFail() {
        given(recruitmentRepository.findById(0L))
                .willReturn(Optional.empty());

        assertThrows(RecruitmentNotFoundException.class,
                () -> recruitmentService.findById(0L)
        );

    }

    @Test
    public void findAll() {
        List<Recruitment> recruitmentList = Arrays.asList(recruitment1, recruitment2);
        given(recruitmentRepository.findAll())
                .willReturn(recruitmentList);

        List<RecruitmentResponseDto> responseDtoList = recruitmentService.findAll();

        assertThat(responseDtoList, equalTo(recruitmentList.stream().map(r -> new RecruitmentResponseDto(r)).collect(Collectors.toList())));

    }

    @Test
    public void add() {
        when(recruitmentRepository.save(any()))
                .thenReturn(recruitment1);

        RecruitmentRequestDto requestDto = new RecruitmentRequestDto(recruitment1);
        RecruitmentResponseDto responseDto = new RecruitmentResponseDto(recruitment1);

        RecruitmentResponseDto add = recruitmentService.add(requestDto);

        assertThat(add, equalTo(responseDto));
    }
}