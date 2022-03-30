package com.example.bobfriend.repository;

import com.example.bobfriend.model.entity.Member;
import com.example.bobfriend.model.entity.Recruitment;
import com.example.bobfriend.model.entity.RecruitmentBan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecruitmentBanRepository extends JpaRepository<RecruitmentBan, Long> {

    Optional<RecruitmentBan> findByMemberAndRecruitment(Member member, Recruitment recruitment);

    boolean existsByMemberAndRecruitment(Member member, Recruitment recruitment);
}
