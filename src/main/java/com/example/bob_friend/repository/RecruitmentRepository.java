package com.example.bob_friend.repository;

import com.example.bob_friend.model.entity.Member;
import com.example.bob_friend.model.entity.Recruitment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RecruitmentRepository extends JpaRepository<Recruitment, Long>, RecruitmentCustomRepository {
    List<Recruitment> findAllByAuthor(Member author);

    List<Recruitment> findAllByActiveTrue();
}