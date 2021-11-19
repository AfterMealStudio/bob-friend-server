package com.example.bob_friend.repository;

import com.example.bob_friend.model.entity.Member;
import com.example.bob_friend.model.entity.Report;
import com.example.bob_friend.model.entity.Writing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WritingReportRepository extends JpaRepository<Report, Long> {
    boolean existsByMemberAndWriting(Member member, Writing writing);

    void deleteAllByWriting(Writing writing);

    void deleteAllByMember(Member member);

}
