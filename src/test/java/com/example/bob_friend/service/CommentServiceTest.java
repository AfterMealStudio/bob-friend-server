package com.example.bob_friend.service;

import com.example.bob_friend.model.dto.CommentDto;
import com.example.bob_friend.model.dto.MemberDto;
import com.example.bob_friend.model.dto.ReplyDto;
import com.example.bob_friend.model.entity.*;
import com.example.bob_friend.model.exception.MemberNotAllowedException;
import com.example.bob_friend.repository.CommentRepository;
import com.example.bob_friend.repository.RecruitmentRepository;
import com.example.bob_friend.repository.ReplyRepository;
import com.example.bob_friend.repository.WritingReportRepository;
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
import java.util.Set;
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
                .replies(Set.of(testReply))
                .createdAt(LocalDateTime.now())
                .build();

    }

    @Test
    void createCommentTest() {
        when(memberService.getCurrentMember()).thenReturn(testAuthor);
        when(commentRepository.save(any())).thenReturn(testComment);
        when(recruitmentRepository.findById(any()))
                .thenReturn(java.util.Optional.ofNullable(testRecruitment));
        CommentDto.Request commentRequestDto = new CommentDto.Request();
        commentRequestDto.setContent(testComment.getContent());
        CommentDto.Response commentDto =
                commentService.createComment(commentRequestDto,
                        testRecruitment.getId());

        assertThat(commentDto.getAuthor(), equalTo(
                new MemberDto.Preview(testAuthor)));
        assertThat(commentDto.getContent(), equalTo(
                testComment.getContent()));
    }


    @Test
    void deleteCommentTest() {
        when(memberService.getCurrentMember())
                .thenReturn(testAuthor);
        when(commentRepository.findById(any()))
                .thenReturn(java.util.Optional.ofNullable(testComment));

        commentService.deleteComment(testComment.getId());

        assertThat(testComment.getAuthor().getEmail(), equalTo("unknown"));

        assertThat(testComment.getContent(), equalTo(null));
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
            commentService.deleteComment(testComment.getId());
        });
    }


    @Test
    void getAllCommentsTest() {
        List<Comment> commentList = Arrays.asList(testComment);
        when(commentRepository.findAllByRecruitmentId(any()))
                .thenReturn(commentList);
        List<CommentDto.Response> allComment =
                commentService.getAllCommentByRecruitmentId(
                        testRecruitment.getId());
        List<CommentDto.Response> responseList = commentList.stream()
                .map((comment) -> new CommentDto.Response(comment))
                .collect(Collectors.toList());
        assertThat(allComment, equalTo(
                responseList));
    }

    @Test
    void createReplyTest() {
        when(memberService.getCurrentMember())
                .thenReturn(testAuthor);
        when(commentRepository.findById(any()))
                .thenReturn(java.util.Optional.ofNullable(testComment));
        when(replyRepository.save(any()))
                .thenReturn(testReply);
        ReplyDto.Request requestDto = new ReplyDto.Request();
        requestDto.setContent(testReply.getContent());

        ReplyDto.Response replyDto =
                commentService.createReply(
                        testComment.getId(), requestDto);

        assertThat(replyDto.getId(), equalTo(testReply.getId()));
        assertThat(replyDto.getAuthor(), equalTo(
                new MemberDto.Preview(testAuthor)
        ));
    }


    @Test
    void deleteReplyTest() {
        testReply = Reply.builder()
                .id(1L)
                .author(testAuthor)
                .comment(testComment)
                .content("test reply")
                .createdAt(LocalDateTime.now())
                .build();
        when(memberService.getCurrentMember())
                .thenReturn(testAuthor);

        when(replyRepository.findById(any()))
                .thenReturn(java.util.Optional.ofNullable(testReply));

        commentService.deleteReply(testReply.getId());

    }

    @Test
    void deleteReplyTest_fail_memberNotAllowed() {
        Member member = Member.builder()
                .id(2L).build();
        when(memberService.getCurrentMember())
                .thenReturn(member);
        when(replyRepository.findById(any()))
                .thenReturn(java.util.Optional.ofNullable(testReply));

        assertThrows(MemberNotAllowedException.class, () -> {
                    commentService.deleteReply(testReply.getId());
                }
        );
    }
}
