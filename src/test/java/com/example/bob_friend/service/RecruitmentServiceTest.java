package com.example.bob_friend.service;

import com.example.bob_friend.model.dto.MemberResponseDto;
import com.example.bob_friend.model.dto.RecruitmentRequestDto;
import com.example.bob_friend.model.dto.RecruitmentResponseDto;
import com.example.bob_friend.model.entity.Member;
import com.example.bob_friend.model.entity.Recruitment;
import com.example.bob_friend.model.entity.Sex;
import com.example.bob_friend.model.exception.RecruitmentNotFoundException;
import com.example.bob_friend.repository.RecruitmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecruitmentServiceTest {
    @Mock
    RecruitmentRepository recruitmentRepository;
    @Mock
    MemberService memberService;
    @InjectMocks
    RecruitmentServiceImpl recruitmentService;

    Recruitment testRecruitment;
    Member testAuthor;

    @BeforeEach
    public void setup() {
        testAuthor = Member.builder()
                .id(1)
                .email("testAuthor@test.com")
                .username("testAuthor")
                .password("testPassword")
                .sex(Sex.FEMALE)
                .birth(LocalDate.now())
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
                .active(true)
                .build();
    }

    @Test
    public void findByIdSuccess() {
        given(recruitmentRepository.findById(testRecruitment.getId()))
                .willReturn(Optional.ofNullable(testRecruitment));

        RecruitmentResponseDto byId = recruitmentService.findById(testRecruitment.getId());
        RecruitmentResponseDto dtoFromEntity = new RecruitmentResponseDto(testRecruitment);

        assertThat(byId, equalTo(dtoFromEntity));
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
        List<Recruitment> recruitmentList = Arrays.asList(testRecruitment);
        given(recruitmentRepository.findAll())
                .willReturn(recruitmentList);

        List<RecruitmentResponseDto> responseDtoList = recruitmentService.findAll();

        assertThat(responseDtoList,
                equalTo(recruitmentList.stream()
                        .map(r -> new RecruitmentResponseDto(r))
                        .collect(Collectors.toList())));
    }

    @Test
    public void add() {
        when(memberService.getCurrentUsername()).thenReturn(testAuthor.getUsername());
        when(memberService.getMemberWithAuthorities(any())).thenReturn(new MemberResponseDto(testAuthor));

        when(recruitmentRepository.save(any()))
                .thenReturn(testRecruitment);

        RecruitmentRequestDto requestDto = new RecruitmentRequestDto(testRecruitment);
        RecruitmentResponseDto responseDto = new RecruitmentResponseDto(testRecruitment);

        RecruitmentResponseDto add = recruitmentService.add(requestDto);

        assertThat(add, equalTo(responseDto));
    }

    @Test
    public void join() {
        Member testMember = Member.builder()
                .id(1)
                .email("testMember@test.com")
                .username("testMember")
                .password("testPassword")
                .sex(Sex.FEMALE)
                .birth(LocalDate.now())
                .active(true)
                .build();
        when(memberService.getCurrentUsername()).thenReturn(testMember.getUsername());
        when(memberService.getMemberWithAuthorities(any())).thenReturn(new MemberResponseDto(testMember));
        given(recruitmentRepository.findById(testRecruitment.getId()))
                .willReturn(Optional.ofNullable(testRecruitment));
        when(recruitmentRepository.save(any()))
                .thenReturn(testRecruitment);

        RecruitmentResponseDto recruitmentResponseDto =
                recruitmentService.joinOrUnJoin(testRecruitment.getId());

        assertTrue(recruitmentResponseDto.getMembers().contains(
                new MemberResponseDto(testMember)));
    }
}