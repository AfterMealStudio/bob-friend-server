package com.example.bobfriend.service;

import com.example.bobfriend.model.dto.ReplyDto;
import com.example.bobfriend.model.entity.Comment;
import com.example.bobfriend.model.entity.Member;
import com.example.bobfriend.model.entity.Reply;
import com.example.bobfriend.model.exception.CommentNotFoundException;
import com.example.bobfriend.model.exception.MemberNotAllowedException;
import com.example.bobfriend.model.exception.ReplyNotFoundException;
import com.example.bobfriend.repository.CommentRepository;
import com.example.bobfriend.repository.ReplyRepository;
import com.example.bobfriend.repository.WritingReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ReplyService {
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final MemberService memberService;
    private final ReportService reportService;
    private final WritingReportRepository reportRepository;


    @Transactional
    public ReplyDto.Response create(
            Long commentId,
            ReplyDto.Request replyDto) {
        Member author = memberService.getCurrentMember();
        Comment comment = getComment(commentId);

        Reply reply = Reply.builder()
                .content(replyDto.getContent())
                .author(author)
                .comment(comment)
                .build();

        Reply save = replyRepository.save(reply);
        return new ReplyDto.Response(save);
    }


    @Transactional
    public void delete(Long replyId) {
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


    public void report(Long replyId) {
        Reply reply = getReply(replyId);
        Member member = memberService.getCurrentMember();
        reportService.reportWriting(member, reply);
    }


    private Reply getReply(Long replyId) {
        return replyRepository.findById(replyId)
                .orElseThrow(() -> {
                    throw new ReplyNotFoundException(replyId);
                });
    }


    private Comment getComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(()-> new CommentNotFoundException(commentId));
    }
}
