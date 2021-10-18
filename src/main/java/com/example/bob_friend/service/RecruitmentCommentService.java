package com.example.bob_friend.service;

import com.example.bob_friend.model.dto.CommentDto;
import com.example.bob_friend.model.dto.ReplyDto;
import com.example.bob_friend.model.entity.Comment;
import com.example.bob_friend.model.entity.Member;
import com.example.bob_friend.model.entity.Recruitment;
import com.example.bob_friend.model.entity.Reply;
import com.example.bob_friend.model.exception.CommentNotFoundException;
import com.example.bob_friend.model.exception.RecruitmentNotFoundException;
import com.example.bob_friend.repository.RecruitmentCommentRepository;
import com.example.bob_friend.repository.RecruitmentRepository;
import com.example.bob_friend.repository.ReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RecruitmentCommentService {
    private final RecruitmentCommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final RecruitmentRepository recruitmentRepository;
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
    public ReplyDto.Response createReply(
            Long commentId,
            ReplyDto.Request replyDto) {
        Member author = memberService.getCurrentMember();
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> {
            throw new CommentNotFoundException(commentId);
        });
        Reply reply = replyDto.convertToEntity();
        reply.setAuthor(author);
        reply.setComment(comment);

        Reply save = replyRepository.save(reply);
        return new ReplyDto.Response(save);
    }



}
