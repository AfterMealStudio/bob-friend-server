package com.example.bobfriend.service;

import com.example.bobfriend.model.dto.CommentDto;
import com.example.bobfriend.model.dto.ReplyDto;
import com.example.bobfriend.model.entity.Comment;
import com.example.bobfriend.model.entity.Member;
import com.example.bobfriend.model.entity.Recruitment;
import com.example.bobfriend.model.entity.Reply;
import com.example.bobfriend.model.exception.CommentNotFoundException;
import com.example.bobfriend.model.exception.MemberNotAllowedException;
import com.example.bobfriend.model.exception.RecruitmentNotFoundException;
import com.example.bobfriend.model.exception.ReplyNotFoundException;
import com.example.bobfriend.repository.CommentRepository;
import com.example.bobfriend.repository.RecruitmentRepository;
import com.example.bobfriend.repository.ReplyRepository;
import com.example.bobfriend.repository.WritingReportRepository;
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
    private final WritingReportRepository reportRepository;
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
        Recruitment recruitment = getRecruitment(recruitmentId);
        comment.setAuthor(currentMember);
        comment.setRecruitment(recruitment);
        return new CommentDto.Response(commentRepository.save(comment));
    }

    @Transactional
    public void deleteComment(Long commentId) {
        Member author = memberService.getCurrentMember();
        Comment comment = getComment(commentId);
        if (comment.getAuthor().equals(author)) {
            reportRepository.deleteAllByWriting(comment);
            if (comment.getReplies().size() > 0) // 대댓글이 달려있으면 내용만 삭제
                comment.clear();
            else
                commentRepository.delete(comment);
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
            reportRepository.deleteAllByWriting(reply); // 신고 내역 삭제
            replyRepository.delete(reply);
            Comment comment = reply.getComment();
            if (comment.getAuthor().isUnknown() &&
                    comment.getReplies().size() <= 1) {// 원 댓글이 삭제된 상태이고 대댓글이 하나도 남지 않았다면 원 댓글 db에서 삭제
                commentRepository.delete(comment);
            }
        } else {
            throw new MemberNotAllowedException(author.getNickname());
        }
    }


    public void reportComment(Long commentId) {
        Comment comment = getComment(commentId);
        Member member = memberService.getCurrentMember();
        reportService.reportWriting(member, comment);
    }

    public void reportReply(Long replyId) {
        Reply reply = getReply(replyId);
        Member member = memberService.getCurrentMember();
        reportService.reportWriting(member, reply);
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

    private Recruitment getRecruitment(Long recruitmentId) {
        return recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> {
                    throw new RecruitmentNotFoundException(recruitmentId);
                });
    }
}
