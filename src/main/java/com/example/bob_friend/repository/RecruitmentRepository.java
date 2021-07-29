package com.example.bob_friend.repository;

import com.example.bob_friend.model.card.domain.Recruitment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecruitmentRepository extends JpaRepository<Recruitment, Long> {
}