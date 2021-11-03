package com.example.bob_friend.service;

import com.example.bob_friend.model.dto.CommentDto;
import com.example.bob_friend.model.dto.ReplyDto;
import com.example.bob_friend.model.entity.Comment;
import com.example.bob_friend.model.entity.Member;
import com.example.bob_friend.model.entity.Recruitment;
import com.example.bob_friend.model.entity.Reply;
import com.example.bob_friend.model.exception.CommentNotFoundException;
import com.example.bob_friend.model.exception.MemberNotAllowedException;
import com.example.bob_friend.model.exception.RecruitmentNotFoundException;
import com.example.bob_friend.model.exception.ReplyNotFoundException;
import com.example.bob_friend.repository.CommentRepository;
import com.example.bob_friend.repository.RecruitmentRepository;
import com.example.bob_friend.repository.ReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final RecruitmentRepository recruitmentRepository;
    private final ReportService reportService;
    private final MemberService memberService;

    public List<CommentDto.Response> getAllCommentByRecruitmentId(Long recruitmentId) {
        return commentRepository.findAllByRecruitmentId(recruitmentId).stream()
                .map(CommentDto.Response::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentDto.Response createComment(
            CommentDto.Request commentRequestDto,
            Long recruitmentId) {
        Comment comment = commentRequestDto.convertToEntity();
        Member currentMember = memberService.getCurrentMember();
        Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
                .orElseThrow(
                        () -> {
                            throw new RecruitmentNotFoundException(recruitmentId);
                        }
                );
        comment.setAuthor(currentMember);
        comment.setRecruitment(recruitment);
        return new CommentDto.Response(commentRepository.save(comment));
    }

    @Transactional
    public void deleteComment(Long commentId) {
        Member author = memberService.getCurrentMember();
        Comment comment = getComment(commentId);
        if (comment.getAuthor().equals(author)) {
            comment.clear();
        } else {
            throw new MemberNotAllowedException(author.getNickname());
        }
    }

    @Transactional
    public ReplyDto.Response createReply(
            Long commentId,
            ReplyDto.Request replyDto) {
        Member author = memberService.getCurrentMember();
        Comment comment = getComment(commentId);
        Reply reply = replyDto.convertToEntity();
        reply.setAuthor(author);
        reply.setComment(comment);

        Reply save = replyRepository.save(reply);
        return new ReplyDto.Response(save);
    }

    @Transactional
    public void deleteReply(Long replyId) {
        Member author = memberService.getCurrentMember();
        Reply reply = getReply(replyId);
        if (reply.getAuthor().equals(author)) {
            replyRepository.delete(reply);
        } else {
            throw new MemberNotAllowedException(author.getNickname());
        }
    }


    public void reportComment(Long commentId) {
        Comment comment = getComment(commentId);
        Member author = comment.getAuthor();
        reportService.reportWriting(author, comment);
    }

    public void reportReply(Long replyId) {
        Reply reply = getReply(replyId);
        Member author = reply.getAuthor();
        reportService.reportWriting(author, reply);
    }

    private Comment getComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> {
                    throw new CommentNotFoundException(commentId);
                });
    }

    private Reply getReply(Long replyId) {
        return replyRepository.findById(replyId)
                .orElseThrow(() -> {
                    throw new ReplyNotFoundException(replyId);
                });
    }
}
