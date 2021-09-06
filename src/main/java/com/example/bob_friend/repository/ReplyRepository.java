package com.example.bob_friend.repository;

import com.example.bob_friend.model.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
}
