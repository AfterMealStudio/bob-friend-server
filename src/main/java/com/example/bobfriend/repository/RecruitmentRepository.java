package com.example.bobfriend.repository;

import com.example.bobfriend.model.entity.Member;
import com.example.bobfriend.model.entity.Recruitment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface RecruitmentRepository extends JpaRepository<Recruitment, Long>, RecruitmentCustomRepository {
    List<Recruitment> findAllByAuthor(Member author);

    List<Recruitment> findAllByActiveTrue();

    Page<Recruitment> findAllByActiveTrue(Pageable pageable);
}