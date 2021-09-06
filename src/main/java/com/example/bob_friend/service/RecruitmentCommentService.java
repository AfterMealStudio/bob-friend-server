package com.example.bob_friend.service;

import com.example.bob_friend.model.dto.CommentRequestDto;
import com.example.bob_friend.model.dto.CommentResponseDto;
import com.example.bob_friend.model.dto.MemberResponseDto;
import com.example.bob_friend.model.entity.Comment;
import com.example.bob_friend.model.entity.Member;
import com.example.bob_friend.model.entity.Recruitment;
import com.example.bob_friend.model.exception.RecruitmentNotFoundException;
import com.example.bob_friend.repository.RecruitmentCommentRepository;
import com.example.bob_friend.repository.RecruitmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RecruitmentCommentService {
    private final RecruitmentCommentRepository commentRepository;
    private final RecruitmentRepository recruitmentRepository;
    private final MemberService memberService;

    public List<CommentResponseDto> getAllCommentByRecruitmentId(Long recruitmentId) {
        return commentRepository.findAllByRecruitmentId(recruitmentId).stream()
                .map(CommentResponseDto::new)
                .collect(Collectors.toList());
    }

    public CommentResponseDto createCommentToRecruitment(CommentRequestDto commentRequestDto, Long recruitmentId) {
        Comment comment = commentRequestDto.convertToEntity();
        String currentUsername = memberService.getCurrentUsername();
        MemberResponseDto member = memberService.getMemberWithAuthorities(currentUsername);
        Recruitment recruitment = recruitmentRepository.findById(recruitmentId).orElseThrow(
                () -> {
                    throw new RecruitmentNotFoundException(recruitmentId);
                }
        );
        comment.setAuthor(member.convertToEntity());
        comment.setRecruitment(recruitment);
        return new CommentResponseDto(commentRepository.save(comment));
    }


}
