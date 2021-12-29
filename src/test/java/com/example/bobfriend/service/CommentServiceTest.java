package com.example.bobfriend.service;

import com.example.bobfriend.model.dto.comment.Create;
import com.example.bobfriend.model.dto.comment.Response;
import com.example.bobfriend.model.dto.member.Preview;
import com.example.bobfriend.model.entity.*;
import com.example.bobfriend.model.exception.MemberNotAllowedException;
import com.example.bobfriend.repository.CommentRepository;
import com.example.bobfriend.repository.RecruitmentRepository;
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
    @Mock
    CommentRepository commentRepository;
    @Mock
    ReplyRepository replyRepository;
    @Mock
    RecruitmentRepository recruitmentRepository;
    @Mock
    WritingReportRepository reportRepository;
    @Mock
    MemberService memberService;
    @InjectMocks
    CommentService commentService;

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

        testAuthor.setup();
    }

    @Test
    void createCommentTest() {
        when(memberService.getCurrentMember()).thenReturn(testAuthor);
        when(commentRepository.save(any())).thenReturn(testComment);
        when(recruitmentRepository.findById(any()))
                .thenReturn(java.util.Optional.ofNullable(testRecruitment));
        Create commentRequestDto = new Create();
        commentRequestDto.setContent(testComment.getContent());
        Response commentDto =
                commentService.create(commentRequestDto,
                        testRecruitment.getId());

        assertThat(commentDto.getAuthor(), equalTo(
                new Preview(testAuthor)));
        assertThat(commentDto.getContent(), equalTo(
                testComment.getContent()));

        assertThat(testComment.getAuthor().getCreatedWritings().size(),
                equalTo(1));
    }


    @Test
    void deleteCommentTest() {
        when(memberService.getCurrentMember())
                .thenReturn(testAuthor);
        when(commentRepository.findById(any()))
                .thenReturn(java.util.Optional.ofNullable(testComment));

        Member author = testReply.getAuthor();

        commentService.delete(testComment.getId());

        assertThat(testComment.getAuthor().getEmail(), equalTo("unknown"));

        assertThat(testComment.getContent(), equalTo(null));

        assertThat(author.getCreatedWritings().size(),
                equalTo(0));
    }


    @Test
    void deleteCommentTest_fail_memberNotAllowed() {
        Member member = Member.builder()
                .id(2L).build();
        when(memberService.getCurrentMember())
                .thenReturn(member);
        when(commentRepository.findById(any()))
                .thenReturn(java.util.Optional.ofNullable(testComment));

        assertThrows(MemberNotAllowedException.class, () -> {
            commentService.delete(testComment.getId());
        });
    }


    @Test
    void getAllCommentsTest() {
        List<Comment> commentList = Arrays.asList(testComment);
        when(commentRepository.findAllByRecruitmentId(any()))
                .thenReturn(commentList);
        List<Response> allComment =
                commentService.getAllCommentByRecruitmentId(
                        testRecruitment.getId());
        List<Response> responseList = commentList.stream()
                .map((comment) -> new Response(comment))
                .collect(Collectors.toList());
        assertThat(allComment, equalTo(
                responseList));
    }


}
