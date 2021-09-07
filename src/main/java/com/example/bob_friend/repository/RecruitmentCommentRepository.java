package com.example.bob_friend.repository;

import com.example.bob_friend.model.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecruitmentCommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByRecruitmentId(Long recruitmentId);
}
