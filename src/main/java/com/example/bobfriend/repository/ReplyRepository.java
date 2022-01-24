package com.example.bobfriend.repository;

import com.example.bobfriend.model.entity.Member;
import com.example.bobfriend.model.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    List<Reply> findAllByAuthor(Member author);

    void deleteAllByAuthor(Member author);
}
