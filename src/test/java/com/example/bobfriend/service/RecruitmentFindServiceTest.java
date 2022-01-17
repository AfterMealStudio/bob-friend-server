package com.example.bobfriend.service;

import com.example.bobfriend.model.dto.recruitment.Address;
import com.example.bobfriend.model.dto.recruitment.Addresses;
import com.example.bobfriend.model.dto.recruitment.DetailResponse;
import com.example.bobfriend.model.dto.recruitment.SimpleResponse;
import com.example.bobfriend.model.entity.*;
import com.example.bobfriend.model.exception.RecruitmentNotFoundException;
import com.example.bobfriend.repository.RecruitmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecruitmentFindServiceTest {
    @Mock
    RecruitmentRepository recruitmentRepository;
    @Mock
    MemberService memberService;

    @InjectMocks
    RecruitmentFindService recruitmentFindService;


    Recruitment testRecruitment;
    Member testAuthor;
    private Comment testComment;
    private Reply testReply;


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
                .rating(0.0)
                .numberOfJoin(0)
                .build();

        testReply = Reply.builder()
                .id(1L)
                .author(testAuthor)
                .comment(testComment)
                .content("test reply")
                .createdAt(LocalDateTime.now())
                .build();

        testComment = Comment.builder()
                .id(1L)
                .author(testAuthor)
                .recruitment(testRecruitment)
                .content("test comment")
                .replies(List.of(testReply))
                .createdAt(LocalDateTime.now())
                .build();


        testRecruitment = Recruitment.builder()
                .id(1L)
                .title("title")
                .content("content")
                .author(testAuthor)
                .members(new HashSet<>())
                .comments(List.of(testComment))
                .totalNumberOfPeople(4)
                .sexRestriction(Sex.FEMALE)
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

        DetailResponse byId = recruitmentFindService
                .findById(testRecruitment.getId());

        DetailResponse dtoFromEntity =
                new DetailResponse(testRecruitment);

        assertThat(byId, equalTo(dtoFromEntity));
    }


    @Test
    public void findByIdFail() {
        given(recruitmentRepository.findById(0L))
                .willReturn(Optional.empty());

        assertThrows(RecruitmentNotFoundException.class,
                () -> recruitmentFindService.findById(0L)
        );

    }


    @Test
    public void findAll() {
        List<Recruitment> recruitmentList = Arrays.asList(testRecruitment);
        PageRequest pageRequest = PageRequest.of(0, 1);
        List<SimpleResponse> collect = recruitmentList.stream()
                .map(r -> new SimpleResponse(r))
                .collect(Collectors.toList());
        Page<SimpleResponse> page = new PageImpl<>(collect);

        given(recruitmentRepository.findAll(pageRequest))
                .willReturn(new PageImpl<>(recruitmentList));
        Page<SimpleResponse> responseDtoList = recruitmentFindService.findAll(pageRequest);

        assertThat(responseDtoList,
                equalTo(page));
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
                .rating(0.0)
                .numberOfJoin(0)
                .build();

        Recruitment recruitment = Recruitment.builder()
                .id(1000L)
                .title("addedRecruitment")
                .content("")
                .author(testAuthor)
                .members(new HashSet<>(Arrays.asList(testMember)))
                .comments(List.of(testComment))
                .totalNumberOfPeople(3)
                .restaurantName("testRestaurantName")
                .restaurantAddress("testRestaurantAddress")
                .latitude(0.0)
                .longitude(0.0)
                .createdAt(LocalDateTime.now())
                .appointmentTime(LocalDateTime.now().plusHours(4))
                .endAt(LocalDateTime.now().plusDays(1))
                .active(true)
                .build();

        when(memberService.getCurrentMember()).thenReturn(testMember);
        PageRequest pageRequest = PageRequest.of(0, 1);

        List<Recruitment> recruitmentList = Arrays.asList(testRecruitment, recruitment);
        given(recruitmentRepository.findAllJoined(any(), any()))
                .willReturn(new PageImpl<>(recruitmentList));

        Page<SimpleResponse> responseDtoList =
                recruitmentFindService.findAllJoined(pageRequest);

        assertThat(responseDtoList.toList(),
                equalTo(recruitmentList.stream()
                        .map(r -> new SimpleResponse(r))
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
                .rating(0.0)
                .numberOfJoin(0)
                .build();
        Recruitment recruitment = Recruitment.builder()
                .id(1000L)
                .title("addedRecruitment")
                .content("")
                .author(testAuthor)
                .members(new HashSet<>())
                .comments(new LinkedList<>())
                .totalNumberOfPeople(3)
                .restaurantName("testRestaurantName")
                .restaurantAddress("testRestaurantAddress")
                .latitude(0.0)
                .longitude(0.0)
                .createdAt(LocalDateTime.now())
                .appointmentTime(LocalDateTime.now().plusHours(4))
                .endAt(LocalDateTime.now().plusDays(1))
                .active(true)
                .build();
        when(memberService.getCurrentMember()).thenReturn(testMember);
        PageRequest pageRequest = PageRequest.of(0, 1);
        List<Recruitment> recruitmentList = Arrays.asList(recruitment);
        given(recruitmentRepository.findAllAvailable(any(), any()))
                .willReturn(new PageImpl<>(recruitmentList));
        Page<SimpleResponse> responseDtoList =
                recruitmentFindService.findAllAvailable(pageRequest);

        assertThat(responseDtoList.toList(),
                equalTo(Arrays.asList(new SimpleResponse(recruitment))));
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
                .rating(0.0)
                .numberOfJoin(0)
                .build();
        Recruitment recruitment = Recruitment.builder()
                .id(1000L)
                .title("addedRecruitment")
                .content("")
                .author(testAuthor)
                .members(new HashSet<>(Arrays.asList(testMember)))
                .comments(List.of(testComment))
                .totalNumberOfPeople(3)
                .restaurantName("testRestaurantName")
                .restaurantAddress("testRestaurantAddress")
                .latitude(0.0)
                .longitude(0.0)
                .createdAt(LocalDateTime.now())
                .appointmentTime(LocalDateTime.now().plusHours(4))
                .endAt(LocalDateTime.now().plusDays(1))
                .active(true)
                .build();
        when(memberService.getCurrentMember()).thenReturn(testMember);

        PageRequest pageRequest = PageRequest.of(0, 1);

        List<Recruitment> recruitmentList = Arrays.asList(testRecruitment, recruitment);
        given(recruitmentRepository.findAllByAuthor(any(), any()))
                .willReturn(new PageImpl<>(recruitmentList));

        Page<SimpleResponse> responseDtoList =
                recruitmentFindService.findMyRecruitments(pageRequest);

        assertThat(responseDtoList.toList(),
                equalTo(recruitmentList.stream()
                        .map(r -> new SimpleResponse(r))
                        .collect(Collectors.toList())));
    }


    @Test
    void findAllByRestaurantAddress() {
        PageRequest pageRequest = PageRequest.of(0, 1);
        when(recruitmentRepository.findAllByAddress(
                any(), any()
        )).thenReturn(new PageImpl<>(Arrays.asList(testRecruitment)));

        String restaurantAddress = "restaurantAddress";

        Page<SimpleResponse> restaurantList = recruitmentFindService
                .findAllByRestaurantAddress(restaurantAddress, pageRequest);

        assertThat(restaurantList.toList(),
                equalTo(Arrays.asList(
                        new SimpleResponse(testRecruitment)
                )));
    }

//    @Test
//    void findAllByRestaurantNameAndAddress() {
//        PageRequest pageRequest = PageRequest.of(0, 1);
//        when(recruitmentRepository.findAllByRestaurant(
//                any(), any()
//        )).thenReturn(new PageImpl<>(Arrays.asList(testRecruitment)));
//
//        String restaurantName = "restaurantName";
//        String restaurantAddress = "restaurantAddress";
//
//        Condition.Search searchCondition = new Condition.Search();
//        searchCondition.setRestaurantName(restaurantName);
//        searchCondition.setRestaurantAddress(restaurantAddress);
//        Page<SimpleResponse> restaurantList = recruitmentFindService
//                .findAllByRestaurant(
//                        searchCondition,
//                        pageRequest);
//
//        assertThat(restaurantList.toList(),
//                equalTo(Arrays.asList(
//                        new SimpleResponse(testRecruitment)
//                )));
//    }


    @Test
    void findAllLocationsTest() {
        double lat = 33.4566084914484;
        double lon = 126.56207301534569;

        List<Recruitment> recruitments = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            Recruitment recruitment = Recruitment.builder()
                    .latitude(lat)
                    .longitude(lon)
                    .restaurantAddress("test")
                    .author(testAuthor)
                    .appointmentTime(LocalDateTime.now())
                    .sexRestriction(Sex.NONE)
                    .active(true)
                    .build();
            recruitments.add(recruitment);
        }
        when(recruitmentRepository.findAllByLocation(any(), any(), any()))
                .thenReturn(recruitments);

        Address address = new Address(recruitments.get(0));
        address.setCount(recruitments.size());
        Addresses allLocations = recruitmentFindService.findAllLocations(lat, lon, 3);

        assertThat(allLocations.getAddresses(), equalTo(List.of(address)));
    }

}
