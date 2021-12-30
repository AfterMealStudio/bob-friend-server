package com.example.bobfriend.service;

import com.example.bobfriend.model.dto.reply.Create;
import com.example.bobfriend.model.dto.reply.Response;
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
    public Response create(
            Long commentId,
            Create replyDto) {
        Member author = memberService.getCurrentMember();
        Comment comment = getComment(commentId);

        Reply reply = Reply.builder()
                .content(replyDto.getContent())
                .build();

        reply.setAuthor(author);
        reply.setComment(comment);
        Reply save = replyRepository.save(reply);
        return new Response(save);
    }


    @Transactional
    public void delete(Long replyId) {
        Member author = memberService.getCurrentMember();
        Reply reply = getReply(replyId);
        if (!reply.getAuthor().equals(author))
            throw new MemberNotAllowedException(author.getNickname());

        author.removeFromCreatedWritings(reply);
        reportRepository.deleteAllByWriting(reply);
        Comment comment = reply.getComment();
        reply.delete();

        if (comment.getAuthor().isUnknown() &&
                comment.getReplies().size() == 0) {
            commentRepository.delete(comment);
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
                .orElseThrow(() -> new CommentNotFoundException(commentId));
    }
}
