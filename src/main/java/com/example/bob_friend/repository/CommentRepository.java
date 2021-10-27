package com.example.bob_friend.repository;

import com.example.bob_friend.model.entity.Comment;
import com.example.bob_friend.model.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByRecruitmentId(Long recruitmentId);

    List<Comment> findAllByAuthor(Member author);
}
