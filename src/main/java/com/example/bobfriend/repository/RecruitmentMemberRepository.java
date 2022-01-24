package com.example.bobfriend.repository;

import com.example.bobfriend.model.entity.Member;
import com.example.bobfriend.model.entity.Recruitment;
import com.example.bobfriend.model.entity.RecruitmentMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecruitmentMemberRepository extends JpaRepository<RecruitmentMember,Long> {
    void deleteAllByMember(Member member);

    void deleteAllByRecruitment(Recruitment recruitment);
}
