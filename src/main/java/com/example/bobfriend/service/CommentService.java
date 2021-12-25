package com.example.bobfriend.service;

import com.example.bobfriend.model.dto.CommentDto;
import com.example.bobfriend.model.entity.Comment;
import com.example.bobfriend.model.entity.Member;
import com.example.bobfriend.model.entity.Recruitment;
import com.example.bobfriend.model.exception.CommentNotFoundException;
import com.example.bobfriend.model.exception.MemberNotAllowedException;
import com.example.bobfriend.model.exception.RecruitmentNotFoundException;
import com.example.bobfriend.repository.CommentRepository;
import com.example.bobfriend.repository.RecruitmentRepository;
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
    public CommentDto.Response create(
            CommentDto.Request commentDto,
            Long recruitmentId) {
        Member currentMember = memberService.getCurrentMember();
        Recruitment recruitment = getRecruitment(recruitmentId);

        Comment comment = Comment.builder()
                .content(commentDto.getContent())
                .author(currentMember)
                .recruitment(recruitment)
                .build();

        return new CommentDto.Response(commentRepository.save(comment));
    }


    @Transactional
    public void delete(Long commentId) {
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


    public void report(Long commentId) {
        Comment comment = getComment(commentId);
        Member member = memberService.getCurrentMember();
        reportService.reportWriting(member, comment);
    }


    private Comment getComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> {
                    throw new CommentNotFoundException(commentId);
                });
    }



    private Recruitment getRecruitment(Long recruitmentId) {
        return recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> {
                    throw new RecruitmentNotFoundException(recruitmentId);
                });
    }
}
