package com.example.bobfriend.service;

import com.example.bobfriend.model.dto.Condition;
import com.example.bobfriend.model.dto.member.Preview;
import com.example.bobfriend.model.dto.recruitment.*;
import com.example.bobfriend.model.entity.*;
import com.example.bobfriend.model.exception.AlreadyJoined;
import com.example.bobfriend.model.exception.MemberNotAllowedException;
import com.example.bobfriend.model.exception.RecruitmentNotFoundException;
import com.example.bobfriend.repository.RecruitmentRepository;
import com.example.bobfriend.repository.WritingReportRepository;
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
    @Mock
    WritingReportRepository reportRepository;
    @InjectMocks
    RecruitmentService recruitmentService;

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


        testAuthor.setup();
    }


    @Test
    public void findByIdSuccess() {
        given(recruitmentRepository.findById(testRecruitment.getId()))
                .willReturn(Optional.ofNullable(testRecruitment));

        DetailResponse byId = recruitmentService
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
                () -> recruitmentService.findById(0L)
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
        Page<SimpleResponse> responseDtoList = recruitmentService.findAll(pageRequest);

        assertThat(responseDtoList,
                equalTo(page));
    }


    @Test
    public void create() {
        when(memberService.getCurrentMember()).thenReturn(testAuthor);
        when(recruitmentRepository.save(any()))
                .thenReturn(testRecruitment);
        when(recruitmentRepository.findById(any())).thenReturn(Optional.ofNullable(testRecruitment));
        Create requestDto = new Create(testRecruitment);

        DetailResponse add = recruitmentService.create(requestDto);

        DetailResponse byId = recruitmentService.findById(testRecruitment.getId());

        assertThat(add, equalTo(byId));
        assertThat(testRecruitment.getAuthor().getCreatedWritings().size(),
                equalTo(1));
    }


    @Test
    public void join() throws AlreadyJoined {
        Member testMember = Member.builder()
                .id(1)
                .email("testMember@test.com")
                .password("testPassword")
                .sex(Sex.FEMALE)
                .birth(LocalDate.now())
                .active(true)
                .rating(0.0)
                .numberOfJoin(0)
                .build();
        when(memberService.getCurrentMember()).thenReturn(testMember); // testMember가 참여를 요청하는 상황

        given(recruitmentRepository.findById(testRecruitment.getId()))
                .willReturn(Optional.ofNullable(testRecruitment));


        DetailResponse recruitmentDetailResponseDto =
                recruitmentService.joinOrUnjoin(testRecruitment.getId());

        Set<Preview> members = recruitmentDetailResponseDto.getMembers();
        assertTrue(members.contains(new Preview(testMember)));
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
                .rating(0.0)
                .numberOfJoin(0)
                .build();
        when(memberService.getCurrentMember()).thenReturn(testMember); // testMember가 참여를 요청하는 상황

        given(recruitmentRepository.findById(testRecruitment.getId()))
                .willReturn(Optional.ofNullable(testRecruitment));
        testRecruitment.getMembers().add(testMember);

        DetailResponse recruitmentDetailResponseDto =
                recruitmentService.joinOrUnjoin(testRecruitment.getId());

        Set<Preview> members = recruitmentDetailResponseDto.getMembers();
        assertFalse(members.contains(new Preview(testMember)));
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
                recruitmentService.findAllJoined(pageRequest);

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
                recruitmentService.findAllAvailable(pageRequest);

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
                recruitmentService.findMyRecruitments(pageRequest);

        assertThat(responseDtoList.toList(),
                equalTo(recruitmentList.stream()
                        .map(r -> new SimpleResponse(r))
                        .collect(Collectors.toList())));
    }


    @Test
    void findAllByRestaurantAddress() {
        PageRequest pageRequest = PageRequest.of(0, 1);
        when(recruitmentRepository.findAllByRestaurant(
                any(), any()
        )).thenReturn(new PageImpl<>(Arrays.asList(testRecruitment)));

        String restaurantAddress = "restaurantAddress";
        Condition.Search searchCondition = new Condition.Search();
        searchCondition.setRestaurantAddress(restaurantAddress);
        Page<SimpleResponse> restaurantList = recruitmentService
                .findAllByRestaurant(searchCondition, pageRequest);

        assertThat(restaurantList.toList(),
                equalTo(Arrays.asList(
                        new SimpleResponse(testRecruitment)
                )));
    }

    @Test
    void findAllByRestaurantNameAndAddress() {
        PageRequest pageRequest = PageRequest.of(0, 1);
        when(recruitmentRepository.findAllByRestaurant(
                any(), any()
        )).thenReturn(new PageImpl<>(Arrays.asList(testRecruitment)));

        String restaurantName = "restaurantName";
        String restaurantAddress = "restaurantAddress";

        Condition.Search searchCondition = new Condition.Search();
        searchCondition.setRestaurantName(restaurantName);
        searchCondition.setRestaurantAddress(restaurantAddress);
        Page<SimpleResponse> restaurantList = recruitmentService
                .findAllByRestaurant(
                        searchCondition,
                        pageRequest);

        assertThat(restaurantList.toList(),
                equalTo(Arrays.asList(
                        new SimpleResponse(testRecruitment)
                )));
    }

    @Test
    void deleteRecruitment_Success() {
        when(memberService.getCurrentMember())
                .thenReturn(testAuthor);
        when(recruitmentRepository.findById(any()))
                .thenReturn(Optional.ofNullable(testRecruitment));
        Member author = testRecruitment.getAuthor();

        recruitmentService.delete(testRecruitment.getId());

        assertThat(author.getCreatedWritings().size(),
                equalTo(0));
    }

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
                .rating(0.0)
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
    void closeRecruitment() {
        when(recruitmentRepository.findById(any()))
                .thenReturn(Optional.ofNullable(testRecruitment));
        when(memberService.getCurrentMember())
                .thenReturn(testAuthor);
        recruitmentService.close(testRecruitment.getId());

        assertThat(testRecruitment.isActive(), equalTo(false));
    }


    @Test
    void search() {
        List<Recruitment> recruitments = Arrays.asList(testRecruitment);
        when(recruitmentRepository.searchByTitle(any(), any()))
                .thenReturn(new PageImpl<>(recruitments));
        PageRequest pageRequest = PageRequest.of(0, 1);

        List<DetailResponse> collect = recruitments.stream()
                .map(DetailResponse::new)
                .collect(Collectors.toList());
        Condition.Search search = new Condition.Search();
        search.setKeyword(testRecruitment.getTitle());
        Page<DetailResponse> responsePage =
                recruitmentService.searchTitle(search, pageRequest);

        assertThat(responsePage, equalTo(new PageImpl<>(collect)));
    }


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
        AddressCollection allLocations = recruitmentService.findAllLocations(lat, lon, 3);

        assertThat(allLocations.getAddresses(), equalTo(List.of(address)));
    }


}