package com.example.bobfriend.repository;

import com.example.bobfriend.model.entity.Member;
import com.example.bobfriend.model.entity.Recruitment;
import com.example.bobfriend.model.entity.RecruitmentMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecruitmentMemberRepository extends JpaRepository<RecruitmentMember,Long> {

    List<RecruitmentMember> findAllByMember(Member member);

    void deleteAllByMember(Member member);

    void deleteAllByRecruitment(Recruitment recruitment);
}
