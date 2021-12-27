package com.example.bobfriend.service;

import com.example.bobfriend.model.dto.Condition;
import com.example.bobfriend.model.dto.MemberDto;
import com.example.bobfriend.model.dto.RecruitmentDto;
import com.example.bobfriend.model.entity.*;
import com.example.bobfriend.model.exception.AlreadyJoined;
import com.example.bobfriend.model.exception.MemberNotAllowedException;
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
    public void create() {
        when(memberService.getCurrentMember()).thenReturn(testAuthor);
        when(recruitmentRepository.save(any()))
                .thenReturn(testRecruitment);

        RecruitmentDto.Request requestDto = new RecruitmentDto.Request(testRecruitment);

        RecruitmentDto.Response add = recruitmentService.create(requestDto);

        assertThat(add.getAuthor().getId(),
                equalTo( testRecruitment.getAuthor().getId()));
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
                .rating(0.0)
                .numberOfJoin(0)
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
        recruitmentService.closeById(testRecruitment.getId());

        assertThat(testRecruitment.isActive(), equalTo(false));
    }


    @Test
    void search() {
        List<Recruitment> recruitments = Arrays.asList(testRecruitment);
        when(recruitmentRepository.searchByTitle(any(), any()))
                .thenReturn(new PageImpl<>(recruitments));
        PageRequest pageRequest = PageRequest.of(0, 1);

        List<RecruitmentDto.Response> collect = recruitments.stream()
                .map(RecruitmentDto.Response::new)
                .collect(Collectors.toList());
        Condition.Search search = new Condition.Search();
        search.setKeyword(testRecruitment.getTitle());
        Page<RecruitmentDto.Response> responsePage =
                recruitmentService.searchTitle(search, pageRequest);

        assertThat(responsePage, equalTo(new PageImpl<>(collect)));
    }




}