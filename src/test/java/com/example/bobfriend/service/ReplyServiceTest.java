package com.example.bobfriend.service;

import com.example.bobfriend.model.dto.member.Preview;
import com.example.bobfriend.model.dto.reply.Create;
import com.example.bobfriend.model.dto.reply.Response;
import com.example.bobfriend.model.entity.*;
import com.example.bobfriend.model.exception.MemberNotAllowedException;
import com.example.bobfriend.repository.CommentRepository;
import com.example.bobfriend.repository.ReplyRepository;
import com.example.bobfriend.repository.WritingReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReplyServiceTest {
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ReplyRepository replyRepository;
    @Mock
    private MemberService memberService;
    @Mock
    private ReportService reportService;
    @Mock
    private WritingReportRepository reportRepository;
    @InjectMocks
    ReplyService replyService;
    private Member testAuthor;
    private Comment testComment;
    private Recruitment testRecruitment;
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

        testRecruitment = Recruitment.builder()
                .id(1L)
                .title("title")
                .content("content")
                .author(testAuthor)
                .members(new HashSet<>())
                .totalNumberOfPeople(4)
                .restaurantName("testRestaurantName")
                .restaurantAddress("testRestaurantAddress")
                .latitude(0.0)
                .longitude(0.0)
                .createdAt(LocalDateTime.now())
                .appointmentTime(LocalDateTime.now().plusHours(4))
                .endAt(LocalDateTime.now().plusDays(1))
                .active(true)
                .build();

        testComment = Comment.builder()
                .id(1L)
                .author(testAuthor)
                .recruitment(testRecruitment)
                .content("test comment")
                .replies(List.of())
                .createdAt(LocalDateTime.now())
                .build();
        testReply = Reply.builder()
                .id(1L)
                .author(testAuthor)
                .comment(testComment)
                .content("test reply")
                .createdAt(LocalDateTime.now())
                .build();
        testAuthor.setup();
    }


    @Test
    void createReplyTest() {
        when(memberService.getCurrentMember())
                .thenReturn(testAuthor);
        when(commentRepository.findById(any()))
                .thenReturn(java.util.Optional.ofNullable(testComment));
        when(replyRepository.save(any()))
                .thenReturn(testReply);
        Create requestDto = new Create();
        requestDto.setContent(testReply.getContent());

        Response replyDto =
                replyService.create(
                        testComment.getId(), requestDto);

        assertThat(replyDto.getId(), equalTo(testReply.getId()));
        assertThat(replyDto.getAuthor(), equalTo(
                new Preview(testAuthor)
        ));

        assertThat(testReply.getAuthor().getCreatedWritings().size(),
                equalTo(1));
    }


    @Test
    void deleteReplyTest() {
        when(memberService.getCurrentMember())
                .thenReturn(testAuthor);

        when(replyRepository.findById(any()))
                .thenReturn(java.util.Optional.ofNullable(testReply));
        Member author = testReply.getAuthor();

        replyService.delete(testReply.getId());

        assertThat(author.getCreatedWritings().size(),
                equalTo(0));
    }


    @Test
    void deleteReplyTest_fail_memberNotAllowed() {
        Member member = Member.builder()
                .id(2L).build();
        when(memberService.getCurrentMember())
                .thenReturn(member);
        when(replyRepository.findById(any()))
                .thenReturn(java.util.Optional.ofNullable(testReply));

        assertThrows(MemberNotAllowedException.class, () ->
                replyService.delete(testReply.getId())
        );
    }
}
