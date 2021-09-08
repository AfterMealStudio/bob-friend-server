package com.example.bob_friend.service;

import com.example.bob_friend.model.dto.RecruitmentRequestDto;
import com.example.bob_friend.model.dto.RecruitmentResponseDto;
import com.example.bob_friend.model.entity.Member;
import com.example.bob_friend.model.entity.Recruitment;
import com.example.bob_friend.model.entity.Sex;
import com.example.bob_friend.model.exception.RecruitmentAlreadyJoined;
import com.example.bob_friend.model.exception.RecruitmentNotFoundException;
import com.example.bob_friend.repository.RecruitmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import static org.junit.jupiter.api.Assertions.*;
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
    RecruitmentService recruitmentService;

    Recruitment testRecruitment;
    Member testAuthor;

    @BeforeEach
    public void setup() {
        testAuthor = Member.builder()
                .id(1)
                .email("testAuthor@test.com")
                .username("testAuthor")
                .nickname("testAuthor")
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
    @DisplayName(value = "한 건 조회 성공")
    public void findByIdSuccess() {
        given(recruitmentRepository.findById(testRecruitment.getId()))
                .willReturn(Optional.ofNullable(testRecruitment));

        RecruitmentResponseDto byId = recruitmentService.findById(testRecruitment.getId());

        RecruitmentResponseDto dtoFromEntity = new RecruitmentResponseDto(testRecruitment);

        assertThat(byId, equalTo(dtoFromEntity));
    }

    @Test
    @DisplayName(value = "한 건 조회 실패")
    public void findByIdFail() {
        given(recruitmentRepository.findById(0L))
                .willReturn(Optional.empty());

        assertThrows(RecruitmentNotFoundException.class,
                () -> recruitmentService.findById(0L)
        );

    }

    @Test
    @DisplayName(value = "전체 조회")
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
    @DisplayName(value = "생성")
    public void create() {
        when(memberService.getCurrentMember()).thenReturn(testAuthor);
        when(recruitmentRepository.save(any()))
                .thenReturn(testRecruitment);
        when(recruitmentRepository.findById(any())).thenReturn(Optional.ofNullable(testRecruitment));
        RecruitmentRequestDto requestDto = new RecruitmentRequestDto(testRecruitment);

        RecruitmentResponseDto add = recruitmentService.createRecruitment(requestDto);

        RecruitmentResponseDto byId = recruitmentService.findById(testRecruitment.getId());

        assertThat(add, equalTo(byId));
    }

    @Test
    @DisplayName(value = "참여")
    public void join() throws RecruitmentAlreadyJoined {
        Member testMember = Member.builder()
                .id(1)
                .email("testMember@test.com")
                .username("testMember")
                .password("testPassword")
                .sex(Sex.FEMALE)
                .birth(LocalDate.now())
                .active(true)
                .build();
        when(memberService.getCurrentMember()).thenReturn(testMember); // testMember가 참여를 요청하는 상황

        given(recruitmentRepository.findById(testRecruitment.getId()))
                .willReturn(Optional.ofNullable(testRecruitment));

//        when(recruitmentRepository.save(any())) joinOrUnjoin 메소드는 영속상태의 엔티티를 이용하므로
//                .thenReturn(testRecruitment);   변경감지가 일어나 save 할 필요 없다.

        RecruitmentResponseDto recruitmentResponseDto =
                recruitmentService.joinOrUnjoin(testRecruitment.getId());

        assertTrue(recruitmentResponseDto.getMembers().contains(
                testMember.getNickname()));
    }

    @Test
    @DisplayName(value = "참여 취소")
    public void unJoin() {
        Member testMember = Member.builder()
                .id(1)
                .email("testMember@test.com")
                .username("testMember")
                .password("testPassword")
                .sex(Sex.FEMALE)
                .birth(LocalDate.now())
                .active(true)
                .build();
        when(memberService.getCurrentMember()).thenReturn(testMember); // testMember가 참여를 요청하는 상황

        given(recruitmentRepository.findById(testRecruitment.getId()))
                .willReturn(Optional.ofNullable(testRecruitment));
        testRecruitment.getMembers().add(testMember);

        RecruitmentResponseDto recruitmentResponseDto =
                recruitmentService.joinOrUnjoin(testRecruitment.getId());

        assertFalse(recruitmentResponseDto.getMembers().contains(
                testMember.getNickname()));
    }

    @Test
    @DisplayName(value = "참여한 모집 조회")
    public void findAllJoinedRecruitments() {
        Member testMember = Member.builder()
                .id(1)
                .email("testMember@test.com")
                .username("testMember")
                .nickname("testMember")
                .password("testPassword")
                .sex(Sex.FEMALE)
                .birth(LocalDate.now())
                .active(true)
                .build();
        Recruitment recruitment = Recruitment.builder()
                .id(1000L)
                .title("addedRecruitment")
                .content("")
                .author(testAuthor)
                .members(new HashSet<>(Arrays.asList(testMember)))
                .currentNumberOfPeople(2)
                .totalNumberOfPeople(3)
                .restaurantName("testRestaurantName")
                .restaurantAddress("testRestaurantAddress")
                .latitude(0.0)
                .longitude(0.0)
                .createdAt(LocalDateTime.now())
                .appointmentTime(LocalDateTime.now().plusHours(4))
                .endAt(LocalDateTime.now().plusDays(1))
                .full(false)
                .active(true)
                .build();
        when(memberService.getCurrentMember()).thenReturn(testMember);

        List<Recruitment> recruitmentList = Arrays.asList(testRecruitment, recruitment);
        given(recruitmentRepository.findAll())
                .willReturn(recruitmentList);

        List<RecruitmentResponseDto> responseDtoList =
                recruitmentService.findAllJoinedRecruitments();

        assertThat(responseDtoList,
                equalTo(recruitmentList.stream()
                        .filter(r ->
                                r.hasMember(testMember) ||
                                        r.getAuthor().equals(testMember))
                        .map(r -> new RecruitmentResponseDto(r))
                        .collect(Collectors.toList())));
    }

    @Test
    @DisplayName(value = "참여 가능한 모집 조회")
    public void findAllAvailableRecruitments() {
        Member testMember = Member.builder()
                .id(1)
                .email("testMember@test.com")
                .username("testMember")
                .nickname("testMember")
                .password("testPassword")
                .sex(Sex.FEMALE)
                .birth(LocalDate.now())
                .active(true)
                .build();
        Recruitment recruitment = Recruitment.builder()
                .id(1000L)
                .title("addedRecruitment")
                .content("")
                .author(testAuthor)
                .members(new HashSet<>(Arrays.asList(testMember)))
                .currentNumberOfPeople(2)
                .totalNumberOfPeople(3)
                .restaurantName("testRestaurantName")
                .restaurantAddress("testRestaurantAddress")
                .latitude(0.0)
                .longitude(0.0)
                .createdAt(LocalDateTime.now())
                .appointmentTime(LocalDateTime.now().plusHours(4))
                .endAt(LocalDateTime.now().plusDays(1))
                .full(false)
                .active(true)
                .build();
        when(memberService.getCurrentMember()).thenReturn(testMember);

        List<Recruitment> recruitmentList = Arrays.asList(testRecruitment, recruitment);
        given(recruitmentRepository.findAll())
                .willReturn(recruitmentList);

        List<RecruitmentResponseDto> responseDtoList =
                recruitmentService.findAllAvailableRecruitments();

        assertThat(responseDtoList,
                equalTo(recruitmentList.stream()
                        .filter(r ->
                                !r.hasMember(testMember) &&
                                        !r.getAuthor().equals(testMember))
                        .map(r -> new RecruitmentResponseDto(r))
                        .collect(Collectors.toList())));
    }

    @Test
    @DisplayName(value = "내가 생성한 모집 조회")
    public void findMyRecruitments() {
        Member testMember = Member.builder()
                .id(1)
                .email("testMember@test.com")
                .username("testMember")
                .nickname("testMember")
                .password("testPassword")
                .sex(Sex.FEMALE)
                .birth(LocalDate.now())
                .active(true)
                .build();
        Recruitment recruitment = Recruitment.builder()
                .id(1000L)
                .title("addedRecruitment")
                .content("")
                .author(testAuthor)
                .members(new HashSet<>(Arrays.asList(testMember)))
                .currentNumberOfPeople(2)
                .totalNumberOfPeople(3)
                .restaurantName("testRestaurantName")
                .restaurantAddress("testRestaurantAddress")
                .latitude(0.0)
                .longitude(0.0)
                .createdAt(LocalDateTime.now())
                .appointmentTime(LocalDateTime.now().plusHours(4))
                .endAt(LocalDateTime.now().plusDays(1))
                .full(false)
                .active(true)
                .build();
        when(memberService.getCurrentMember()).thenReturn(testMember);

        List<Recruitment> recruitmentList = Arrays.asList(testRecruitment, recruitment);
        given(recruitmentRepository.findAllByAuthor(any()))
                .willReturn(recruitmentList);

        List<RecruitmentResponseDto> responseDtoList =
                recruitmentService.findMyRecruitments();

        assertThat(responseDtoList,
                equalTo(recruitmentList.stream()
                        .map(r -> new RecruitmentResponseDto(r))
                        .collect(Collectors.toList())));
    }


}