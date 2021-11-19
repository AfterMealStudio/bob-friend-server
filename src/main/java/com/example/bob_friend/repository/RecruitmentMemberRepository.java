package com.example.bob_friend.repository;

import com.example.bob_friend.model.entity.Member;
import com.example.bob_friend.model.entity.Recruitment;
import com.example.bob_friend.model.entity.RecruitmentMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecruitmentMemberRepository extends JpaRepository<RecruitmentMember,Long> {
    void deleteAllByMember(Member member);

    void deleteAllByRecruitment(Recruitment recruitment);
}
