package com.example.bob_friend.service;

import com.example.bob_friend.model.dto.MemberDto;
import com.example.bob_friend.model.dto.RecruitmentDto;
import com.example.bob_friend.model.entity.Member;
import com.example.bob_friend.model.entity.Recruitment;
import com.example.bob_friend.model.entity.Sex;
import com.example.bob_friend.model.exception.MemberNotAllowedException;
import com.example.bob_friend.model.exception.RecruitmentAlreadyJoined;
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
import java.util.*;
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
    public void findByIdSuccess() {
        given(recruitmentRepository.findById(testRecruitment.getId()))
                .willReturn(Optional.ofNullable(testRecruitment));

        RecruitmentDto.Response byId = recruitmentService.findById(testRecruitment.getId());

        RecruitmentDto.Response dtoFromEntity = new RecruitmentDto.Response(testRecruitment);

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

        List<RecruitmentDto.Response> responseDtoList = recruitmentService.findAll();

        assertThat(responseDtoList,
                equalTo(recruitmentList.stream()
                        .map(r -> new RecruitmentDto.Response(r))
                        .collect(Collectors.toList())));
    }


    @Test
    public void create() {
        when(memberService.getCurrentMember()).thenReturn(testAuthor);
        when(recruitmentRepository.save(any()))
                .thenReturn(testRecruitment);
        when(recruitmentRepository.findById(any())).thenReturn(Optional.ofNullable(testRecruitment));
        RecruitmentDto.Request requestDto = new RecruitmentDto.Request(testRecruitment);

        RecruitmentDto.Response add = recruitmentService.createRecruitment(requestDto);

        RecruitmentDto.Response byId = recruitmentService.findById(testRecruitment.getId());

        assertThat(add, equalTo(byId));
    }


    @Test
    public void join() throws RecruitmentAlreadyJoined {
        Member testMember = Member.builder()
                .id(1)
                .email("testMember@test.com")
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

        RecruitmentDto.Response recruitmentResponseDto =
                recruitmentService.joinOrUnjoin(testRecruitment.getId());

        Set<MemberDto.Preview> members = recruitmentResponseDto.getMembers();
        assertTrue(members.contains(new MemberDto.Preview(testMember)));
    }


    @Test
    public void unJoin() {
        Member testMember = Member.builder()
                .id(1)
                .email("testMember@test.com")
                .password("testPassword")
                .sex(Sex.FEMALE)
                .birth(LocalDate.now())
                .active(true)
                .build();
        when(memberService.getCurrentMember()).thenReturn(testMember); // testMember가 참여를 요청하는 상황

        given(recruitmentRepository.findById(testRecruitment.getId()))
                .willReturn(Optional.ofNullable(testRecruitment));
        testRecruitment.getMembers().add(testMember);

        RecruitmentDto.Response recruitmentResponseDto =
                recruitmentService.joinOrUnjoin(testRecruitment.getId());

        Set<MemberDto.Preview> members = recruitmentResponseDto.getMembers();
        assertFalse(members.contains(new MemberDto.Preview(testMember)));
    }


    @Test
    public void findAllJoinedRecruitments() {
        Member testMember = Member.builder()
                .id(1)
                .email("testMember@test.com")
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

        List<RecruitmentDto.Response> responseDtoList =
                recruitmentService.findAllJoinedRecruitments();

        assertThat(responseDtoList,
                equalTo(recruitmentList.stream()
                        .filter(r ->
                                r.hasMember(testMember) ||
                                        r.getAuthor().equals(testMember))
                        .map(r -> new RecruitmentDto.Response(r))
                        .collect(Collectors.toList())));
    }


    @Test
    public void findAllAvailableRecruitments() {
        Member testMember = Member.builder()
                .id(1)
                .email("testMember@test.com")
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

        List<RecruitmentDto.Response> responseDtoList =
                recruitmentService.findAllAvailableRecruitments();

        assertThat(responseDtoList,
                equalTo(recruitmentList.stream()
                        .filter(r ->
                                !r.hasMember(testMember) &&
                                        !r.getAuthor().equals(testMember))
                        .map(r -> new RecruitmentDto.Response(r))
                        .collect(Collectors.toList())));
    }


    @Test
    public void findMyRecruitments() {
        Member testMember = Member.builder()
                .id(1)
                .email("testMember@test.com")
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

        List<RecruitmentDto.Response> responseDtoList =
                recruitmentService.findMyRecruitments();

        assertThat(responseDtoList,
                equalTo(recruitmentList.stream()
                        .map(r -> new RecruitmentDto.Response(r))
                        .collect(Collectors.toList())));
    }


    @Test
    void findAllByRestaurantAddress() {
        when(recruitmentRepository.findAllByRestaurantAddress(
                any()
        )).thenReturn(Arrays.asList(testRecruitment));

        String restaurantAddress = "restaurantAddress";

        List<RecruitmentDto.Response> restaurantList = recruitmentService
                .findAllByRestaurantAddress(restaurantAddress);

        assertThat(restaurantList,
                equalTo(Arrays.asList(
                        new RecruitmentDto.Response(testRecruitment)
                )));
    }

    @Test
    void findAllByRestaurantNameAndAddress() {
        when(recruitmentRepository.findAllByRestaurantNameAndRestaurantAddress(
                any(), any()
        )).thenReturn(Arrays.asList(testRecruitment));

        String restaurantName = "restaurantName";
        String restaurantAddress = "restaurantAddress";

        List<RecruitmentDto.Response> restaurantList = recruitmentService
                .findAllByRestaurantNameAndRestaurantAddress(
                        restaurantName, restaurantAddress);

        assertThat(restaurantList,
                equalTo(Arrays.asList(
                        new RecruitmentDto.Response(testRecruitment)
                )));
    }

    @Test
    void findAllAvailableLocations() {

    }

//    @Test
//    void deleteRecruitmentSuccess() {
//        when(memberService.getCurrentMember())
//                .thenReturn(testAuthor);
//        when(recruitmentRepository.findById(any()))
//                .thenReturn(Optional.ofNullable(testRecruitment));
//
//        recruitmentService.delete(testRecruitment.getId());
//
//    }

    @Test
    void deleteRecruitmentFail_RecruitmentNotFound() {
        when(memberService.getCurrentMember())
                .thenReturn(testAuthor);
        when(recruitmentRepository.findById(any()))
                .thenReturn(Optional.empty());
        assertThrows(RecruitmentNotFoundException.class, () -> {
            recruitmentService.delete(testRecruitment.getId());
        });
    }

    @Test
    void deleteRecruitmentFail_MemberNotAllowed() {
        Member testMember = Member.builder()
                .id(2)
                .email("testMember@test.com")
                .nickname("testMember")
                .password("testPassword")
                .sex(Sex.FEMALE)
                .birth(LocalDate.now())
                .active(true)
                .build();
        when(memberService.getCurrentMember())
                .thenReturn(testMember);
        when(recruitmentRepository.findById(any()))
                .thenReturn(Optional.ofNullable(testRecruitment));

        assertThrows(MemberNotAllowedException.class, () -> {
            recruitmentService.delete(testRecruitment.getId());
        });
    }

    @Test
    void closeRecruitmentTest() {
        when(recruitmentRepository.findById(any()))
                .thenReturn(Optional.ofNullable(testRecruitment));
        when(memberService.getCurrentMember())
                .thenReturn(testAuthor);
        recruitmentService.closeRecruitment(testRecruitment.getId());

        assertThat(testRecruitment.isActive(), equalTo(false));
    }
}